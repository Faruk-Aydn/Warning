package com.example.warning.presentation.ui.screens.register

import android.R.attr.country
import android.R.attr.phoneNumber
import android.app.Activity
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
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
import com.example.warning.presentation.viewModel.ContactListenerViewmodel
import com.example.warning.presentation.viewModel.ProfileListenerViewModel
import com.example.warning.presentation.viewModel.UserRegistrationState
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// NOTE: Bu dosya SignUp ekranını içerir.
// Dosya konumu: ui/screens/SignUpScreen.kt



@Composable
fun SignUpScreen(
    navController: NavHostController ,
    verificationViewModel: VerificationViewModel = hiltViewModel(),
    registrationViewModel: RegistrationViewModel = hiltViewModel(),
    userview: ProfileListenerViewModel = hiltViewModel(),
    contactview: ContactListenerViewmodel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // --- UI State ---
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf("+90") }
    var countryQuery by remember { mutableStateOf("") }
    val countryList = listOf("+1", "+90")

    var locationPermission by remember { mutableStateOf(false) }
    var contactPermission by remember { mutableStateOf(false) }

    var expanded by remember { mutableStateOf(false) }
    var verificationStep by remember { mutableStateOf(VerificationStep.EnterPhone) }
    var codeInput by remember { mutableStateOf("") }

    val isLoading by remember { derivedStateOf { verificationViewModel.isLoading } }
    val isVerified by remember { derivedStateOf { verificationViewModel.isVerified } }
    val errorMessage by remember { derivedStateOf { verificationViewModel.errorMessage } }

    var registrationSuccess by remember { mutableStateOf(false) }

    // Registration state takibi

    // --- Registration State ---
    val _state = MutableStateFlow<UserRegistrationState>(UserRegistrationState.Idle)
    val state: StateFlow<UserRegistrationState> = _state

    LaunchedEffect(state) {
        if(state is UserRegistrationState.RegistrationSuccess) {
            navController.navigate("main")
        }
    }

    when (state) {
        UserRegistrationState.CheckingRegistration -> Log.i("kontrol","Kayıt kontrol ediliyor...")
        UserRegistrationState.RegistrationConfirmed -> Log.i("ontrol","Firebase kaydı bekleniyor...")
        UserRegistrationState.LoadingFromRoom -> Log.i("Veriler yükleniyor...","")
            is UserRegistrationState.Error ->Log.i("Hata: ","${state.message}")
//        UserRegistrationState.RegistrationSuccess -> navController.navigate("main")
        else -> "" // Idle zaten return ile önleniyor
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Kayıt Ol",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                UserFormSection(
                    name = name,
                    onNameChange = { name = it },
                    phone = phone,
                    onPhoneChange = { input ->
                        val digits = input.filter { it.isDigit() }
                        if (digits.length <= 10) phone = digits
                    },
                    selectedCountry = selectedCountry,
                    onCountryChange = { selectedCountry = it },
                    countryQuery = countryQuery,
                    onCountryQueryChange = { countryQuery = it },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    countryList = countryList,
                    locationPermission = locationPermission,
                    onLocationPermissionChange = { locationPermission = it },
                    contactPermission = contactPermission,
                    onContactPermissionChange = { contactPermission = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                when (verificationStep) {
                    VerificationStep.EnterPhone -> {
                        PhoneVerificationSection(
                            isLoading = isLoading,
                            phone = phone,
                            selectedCountry = selectedCountry,
                            name = name,
                            activity = activity,
                            registrationViewModel = registrationViewModel,
                            verificationViewModel = verificationViewModel,
                            snackbarHostState = snackbarHostState,
                            coroutineScope = coroutineScope,
                            onVerificationStepChange = { verificationStep = it }
                        )
                    }

                    VerificationStep.EnterCode -> {
                        CodeVerificationSection(
                            codeInput = codeInput,
                            onCodeChange = { codeInput = it },
                            selectedCountry = selectedCountry,
                            activity = activity,
                            verificationViewModel = verificationViewModel,
                            snackbarHostState = snackbarHostState,
                            coroutineScope = coroutineScope,
                            isVerified = isVerified,
                            errorMessage = errorMessage,
                            onRegistrationSuccess = { registrationSuccess = it },
                            phone= phone
                        )
                    }

                    VerificationStep.Verified -> {
                        Text("Doğrulama başarılı, yönlendiriliyorsunuz...")
                        registrationViewModel.registerUser(profile= Profile(
                            id = null,
                            phoneNumber = phone,
                            country = selectedCountry,
                            profilePhoto = "",
                            name = name,
                            emergencyMessage = null,
                            locationPermission = locationPermission
                        ))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                BottomNavigationSection(navController)
            }

            if (registrationSuccess) {
                Log.i("signup ", "registrationSuccess is true. loading is start")
            }
        }
    }
}

@Composable
fun PhoneVerificationSection(
    isLoading: Boolean,
    phone: String,
    selectedCountry: String,
    name: String,
    activity: Activity?,
    registrationViewModel: RegistrationViewModel,
    verificationViewModel: VerificationViewModel,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    onVerificationStepChange: (VerificationStep) -> Unit
) {
    Button(
        onClick = {
            when {
                name.isBlank() -> coroutineScope.launch { snackbarHostState.showSnackbar("İsim boş bırakılamaz") }
                phone.length != 10 -> coroutineScope.launch { snackbarHostState.showSnackbar("Telefon 10 hane olmalı") }
                else -> {
                    val fullNumber = selectedCountry + phone
                    coroutineScope.launch {
                        try {
                            val exists = registrationViewModel.checkingUser(phone).await()
                            if (exists) {
                                snackbarHostState.showSnackbar("Bu telefon zaten kayıtlı")
                            } else {
                                coroutineScope.launch {
                                    if (activity != null) {
                                        verificationViewModel.sendVerificationCode(
                                            phoneNumber = fullNumber,
                                            activity = activity,
                                            onSuccess = { onVerificationStepChange(VerificationStep.EnterCode) },
                                            onFailure = {
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar("Doğrulama kodu gönderilemedi")
                                                }
                                            }
                                        )
                                    } else {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Activity bulunamadı")
                                        }
                                    }
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
        modifier = Modifier.fillMaxWidth().height(52.dp),
        colors = ButtonDefaults.buttonColors(AppColorScheme.neutralLight)
    ) {
        if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
        else Text("Kayıt Ol")
    }
}

@Composable
fun UserFormSection(
    name: String,
    onNameChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    selectedCountry: String,
    onCountryChange: (String) -> Unit,
    countryQuery: String,
    onCountryQueryChange: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    countryList: List<String>,
    locationPermission: Boolean,
    onLocationPermissionChange: (Boolean) -> Unit,
    contactPermission: Boolean,
    onContactPermissionChange: (Boolean) -> Unit
) {
    // İsim
    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text("İsim") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Text
        )
    )

    Spacer(modifier = Modifier.height(12.dp))

    // Ülke kodu + telefon
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box {
            OutlinedButton(
                onClick = { onExpandedChange(true) },
                modifier = Modifier.height(56.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(selectedCountry)
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Ülke kodu aç")
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) }
            ) {
                OutlinedTextField(
                    value = countryQuery,
                    onValueChange = onCountryQueryChange,
                    label = { Text("Ara") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                )

                val filtered = if (countryQuery.isBlank()) countryList else countryList.filter { it.contains(countryQuery) }
                filtered.forEach { code ->
                    DropdownMenuItem(
                        text = { Text(code) },
                        onClick = {
                            onCountryChange(code)
                            onExpandedChange(false)
                            onCountryQueryChange("")
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = onPhoneChange,
            label = { Text("Telefon (10 hane)") },
            singleLine = true,
            modifier = Modifier.weight(1f).height(56.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    // İzinler
    PermissionSwitchRow("Konum izni", locationPermission, onLocationPermissionChange)
    PermissionSwitchRow("Rehber erişim izni", contactPermission, onContactPermissionChange)
}

@Composable
fun PermissionSwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(modifier = Modifier.weight(1f), text = label)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
        if (checked) Icon(Icons.Default.Check, contentDescription = "$label onaylandı", modifier = Modifier.padding(start = 8.dp))
    }
}
@Composable
fun CodeVerificationSection(
    codeInput: String,
    onCodeChange: (String) -> Unit,
    selectedCountry: String,
    activity: Activity?,
    verificationViewModel: VerificationViewModel,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    isVerified: Boolean?,
    errorMessage: String?,
    onRegistrationSuccess: (Boolean) -> Unit,
    phone: String
) {
    OutlinedTextField(
        value = codeInput,
        onValueChange = { onCodeChange(it.filter { ch -> ch.isDigit() }) },
        label = { Text("Doğrulama Kodu") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )

    Spacer(modifier = Modifier.height(8.dp))

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Button(onClick = { verificationViewModel.verifyCode(codeInput) }) {
            Text("Kodu Onayla")
        }

        TextButton(onClick = {
            val fullNumber = selectedCountry + phone
            if (activity != null) {
                verificationViewModel.sendVerificationCode(
                    phoneNumber = fullNumber,
                    activity = activity,
                    onSuccess = { coroutineScope.launch { snackbarHostState.showSnackbar("Kod tekrar gönderildi") } },
                    onFailure = { coroutineScope.launch { snackbarHostState.showSnackbar("Tekrar gönderilemedi") } }
                )
            }
        }) {
            Icon(Icons.Default.Refresh, contentDescription = "Tekrar gönder")
            Spacer(modifier = Modifier.width(6.dp))
            Text("Tekrar Gönder")
        }
    }

    LaunchedEffect(isVerified) {
        if (isVerified == true) {
            onRegistrationSuccess(true)
        }
        if (isVerified == false) {
            coroutineScope.launch { snackbarHostState.showSnackbar(errorMessage ?: "Doğrulama başarısız") }
        }
    }
}

@Composable
fun BottomNavigationSection(navController: NavHostController) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Text(text = "Zaten hesabın var mı? ")
        Text(
            text = "Giriş yap",
            modifier = Modifier.clickable { navController.navigate("SignInScreen") }
        )
    }
}

// -------------------------
// AppColorScheme placeholder - projenizde merkezi renk dosyanız zaten varsa bunu kullanın.
// Burada sadece örnek olması için tanımladım. Projenizde ui/theme/ColorScheme.kt dosyanızda tanımlı olmalı.

// -------------------------
// NOT: Bu örnekte bazı kısımlar (registrationState'in detayları vb.) genel tutulmuştur.
// Gerçekte RegistrationViewModel'in state modeline göre collectAsState/LaunchedEffect ile tam kontrol ekleyin.
// Ayrıca hata durumları, network hataları ve edge-case senaryoları için ekstra kullanıcı geri bildirimleri eklemenizi öneririm.
