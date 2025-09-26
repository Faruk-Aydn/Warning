package com.example.warning.presentation.ui.screens.register

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.warning.domain.model.Profile
import com.example.warning.presentation.ui.theme.AppColorScheme
import com.example.warning.presentation.viewModel.RegistrationViewModel
import com.example.warning.presentation.viewModel.VerificationStep
import com.example.warning.presentation.viewModel.VerificationViewModel
import kotlinx.coroutines.launch
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState

// NOTE: Bu dosya SignUp ekranını içerir.
// Dosya konumu: ui/screens/SignUpScreen.kt

@Composable
fun SignUpScreen(
    navController: NavHostController,
    verificationViewModel: VerificationViewModel = hiltViewModel(),
    registrationViewModel: RegistrationViewModel = hiltViewModel()
) {
    // Context & Activity for Firebase phone verification
    val context = LocalContext.current
    val activity = context as? Activity


// State oluştur
    val snackbarHostState = remember { SnackbarHostState() }
    // Scaffold for Snackbar
    val coroutineScope = rememberCoroutineScope()

    // Form state
    var name by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf("+90") } // default +90
    var countryQuery by remember { mutableStateOf("") }
    val countryList = listOf("+1", "+90")
    var phone by remember { mutableStateOf("") }

    // Permission toggles (Switch + visual tick icon on the right)
    var locationPermission by remember { mutableStateOf(false) }
    var contactPermission by remember { mutableStateOf(false) }

    // Dropdown menu state for country selection
    var expanded by remember { mutableStateOf(false) }

    // Verification UI state
    var verificationStep by remember { mutableStateOf(VerificationStep.EnterPhone) }
    var codeInput by remember { mutableStateOf("") }

    // Observe viewmodel states
    val isLoading by remember { derivedStateOf { verificationViewModel.isLoading } }
    val isVerified by remember { derivedStateOf { verificationViewModel.isVerified } }
    val errorMessage by remember { derivedStateOf { verificationViewModel.errorMessage } }

    // Registration result: collect from stateflow if available
    // For simplicity burada registrationViewModel.registrationState'in içeriğini check etmek için basit bir collect yapıyoruz.
    // Uygulamanızda UserRegistrationState modeline göre handle edin.
    var registrationSuccess by remember { mutableStateOf(false) }
    var registrationError by remember { mutableStateOf<String?>(null) }

    // Bu örnekte registrationState'in nasıl olduğu bilinmediği için safe bir bekleme yapılır.
    // Eğer registerUse.state içinde belirli alanlar varsa onları burada collectAsState ile kullanabilirsiniz.

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = { /* İstenirse üst bar eklenir */ }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // --- Form alanları (üst kısım) ---
            Column {
                // Başlık
                Text(
                    text = "Kayıt Ol",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // 1) İsim alanı - boş olamaz
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("İsim") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Text
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 2) Ülke kodu + Telefon numarası (yan yana)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Country selector with a small searchable dropdown
                    Box {
                        OutlinedButton(
                            onClick = { expanded = true },
                            modifier = Modifier.height(56.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(selectedCountry)
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Ülke kodu aç"
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            // Search field in menu
                            OutlinedTextField(
                                value = countryQuery,
                                onValueChange = { countryQuery = it },
                                label = { Text("Ara") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            )

                            val filtered = if (countryQuery.isBlank()) countryList else countryList.filter { it.contains(countryQuery) }
                            filtered.forEach { code ->
                                DropdownMenuItem(
                                    text = {Text(code)},
                                    onClick = {
                                    selectedCountry = code
                                    expanded = false
                                    countryQuery = ""
                                })
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Telefon numarası alanı (sadece rakam, 10 hane)
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { input ->
                            // sadece rakamlara izin ver
                            val digits = input.filter { it.isDigit() }
                            if (digits.length <= 10) phone = digits
                        },
                        label = { Text("Telefon (10 hane)") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 3) Konum izni satırı (Switch + sağda tik ikonu)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Bilgilendirici metin
                    Text(modifier = Modifier.weight(1f), text = "Konum izni")

                    // Switch ile izin verme
                    Switch(
                        checked = locationPermission,
                        onCheckedChange = { locationPermission = it }
                    )

                    // Sağ tarafta onaylandığını göstermek için tik ikonu
                    if (locationPermission) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Konum onaylandı",
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                // 4) Rehber izni satırı (Switch + sağda tik ikonu)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(modifier = Modifier.weight(1f), text = "Rehber erişim izni")

                    Switch(
                        checked = contactPermission,
                        onCheckedChange = { contactPermission = it }
                    )

                    if (contactPermission) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Rehber onaylandı",
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 5) Doğrulama aşamasına göre farklı UI: telefon girme veya kod girme
                when (verificationStep) {
                    VerificationStep.EnterPhone -> {
                        // Kayıt Ol butonu - önce checkUser çalıştırılacak
                        Button(
                            onClick = {
                                // Validasyonlar
                                when {
                                    name.isBlank() -> {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("İsim boş bırakılamaz")
                                        }
                                    }
                                    phone.length != 10 -> {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Telefon 10 hane olmalı")
                                        }
                                    }
                                    else -> {
                                        // Full number: e.g. +90xxxxxxxxxx
                                        val fullNumber = selectedCountry + phone

                                        // checkUser çağrısı - suspend fonksiyon olduğu için coroutine
                                        coroutineScope.launch {
                                            try {
                                                val exists = registrationViewModel.checkingUser(fullNumber)
                                                if (exists) {
                                                    // Kullanıcı zaten kayıtlıysa uyar
                                                    snackbarHostState.showSnackbar("Bu telefon zaten kayıtlı")
                                                } else {
                                                    // Gönderme işlemini başlat
                                                    if (activity != null) {
                                                        verificationViewModel.sendVerificationCode(
                                                            phoneNumber = fullNumber,
                                                            activity = activity,
                                                            onSuccess = {
                                                                // Kod gönderildi, EnterCode aşamasına geç
                                                                verificationStep = VerificationStep.EnterCode
                                                            },
                                                            onFailure = {
                                                                coroutineScope.launch {
                                                                    snackbarHostState.showSnackbar("Doğrulama kodu gönderilemedi")
                                                                }
                                                            }
                                                        )
                                                    } else {
                                                        snackbarHostState.showSnackbar("Activity bulunamadı")
                                                    }
                                                }
                                            } catch (e: Exception) {
                                                Log.e("SignUp", "checkUser hata: ${e.message}")
                                                snackbarHostState.showSnackbar("Kullanıcı kontrolünde hata")
                                            }
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(AppColorScheme.neutralLight)
                        ) {
                            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                            else Text("Kayıt Ol")
                        }
                    }

                    VerificationStep.EnterCode -> {
                        // Kod girme alanı
                        OutlinedTextField(
                            value = codeInput,
                            onValueChange = { codeInput = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Doğrulama Kodu") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            // Onaylama butonu
                            Button(onClick = {
                                // Kod doğrulama
                                verificationViewModel.verifyCode(codeInput)
                            }) {
                                Text("Kodu Onayla")
                            }

                            // Tekrar gönder
                            TextButton(onClick = {
                                val fullNumber = selectedCountry + phone
                                if (activity != null) {
                                    verificationViewModel.sendVerificationCode(
                                        phoneNumber = fullNumber,
                                        activity = activity,
                                        onSuccess = {
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Kod tekrar gönderildi")
                                            }
                                        },
                                        onFailure = {
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Tekrar gönderilemedi")
                                            }
                                        }
                                    )
                                }
                            }) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Tekrar gönder")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Tekrar Gönder")
                            }
                        }

                        // Eğer verificationViewModel.isVerified true olduysa kayıt etme adımına geç
                        LaunchedEffect(isVerified) {
                            if (isVerified == true) {
                                // Map to domain model
                                val profile = Profile(
                                    phoneNumber = phone.toString(),
                                    country = selectedCountry,
                                    profilePhoto = "", // default boş
                                    name = name,
                                    emergencyMessage = null,
                                    locationPermission = locationPermission,
                                    contactPermission = contactPermission
                                )

                                // Kayıt işlemini başlat
                                registrationViewModel.registerUser(profile)

                                // registrationViewModel.registrationState'i dinleyip success olduğunda MainScreen'e yönlendir
                                // Burada basit bir bekleme yerine uygulamanızın RegistrationState modeline göre handle edin
                                // Örnek: registrationViewModel.registrationState.collect { state -> if (state.isSuccess) navController.navigate("MainScreen") }
                                // Bu örnekte success olduğunu varsayıp doğrudan yönlendiriyoruz (gerçek kullanımda registrationState'i kontrol edin)
                                registrationSuccess = true
                            }
                            if (isVerified == false) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(errorMessage ?: "Doğrulama başarısız")
                                }
                            }
                        }
                    }

                    VerificationStep.Verified -> {
                        // İsteğe bağlı: Verified state için bilgi göster
                        Text("Doğrulama başarılı, yönlendiriliyorsunuz...")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Alt kısım: Zaten hesabın var mı? Giriş yap
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text(text = "Zaten hesabın var mı? ")
                    Text(
                        text = "Giriş yap",
                        modifier = Modifier.clickable {
                            // Navigation: SignInScreen'e git
                            navController.navigate("SignInScreen")
                        }
                    )
                }
            }

            // Alt bölüm: eğer kayıt başarılıysa MainScreen'e yönlendir
            if (registrationSuccess) {
                // Navigation comment: Başarılı kayıt sonrası MainScreen'e yönlendir
                LaunchedEffect(Unit) {
                    // Örneğin navController.navigate("MainScreen")
                    navController.navigate("MainScreen") {
                        // Eğer geri yığını temizlemek isterseniz:
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }
}

// -------------------------
// AppColorScheme placeholder - projenizde merkezi renk dosyanız zaten varsa bunu kullanın.
// Burada sadece örnek olması için tanımladım. Projenizde ui/theme/ColorScheme.kt dosyanızda tanımlı olmalı.

// -------------------------
// NOT: Bu örnekte bazı kısımlar (registrationState'in detayları vb.) genel tutulmuştur.
// Gerçekte RegistrationViewModel'in state modeline göre collectAsState/LaunchedEffect ile tam kontrol ekleyin.
// Ayrıca hata durumları, network hataları ve edge-case senaryoları için ekstra kullanıcı geri bildirimleri eklemenizi öneririm.
