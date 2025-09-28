package com.example.warning.presentation.ui.screens.register

import com.example.warning.presentation.viewModel.RegistrationViewModel
import com.example.warning.presentation.viewModel.VerificationStep
import com.example.warning.presentation.viewModel.VerificationViewModel

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.warning.presentation.ui.theme.AppColorScheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    navController: NavHostController,
    registrationViewModel: RegistrationViewModel = hiltViewModel(),
    verificationViewModel: VerificationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Ülke kodu dropdown için state
    var expanded by remember { mutableStateOf(false) }
    var selectedCountryCode by remember { mutableStateOf("+90") }
    val countryCodes = listOf("+90", "+1", "+44", "+49")

    // Telefon numarası
    var phoneNumber by remember { mutableStateOf("") }

    // Doğrulama adımları için state
    var step by remember { mutableStateOf(VerificationStep.EnterPhone) }

    // SMS kodu
    var smsCode by remember { mutableStateOf("") }

    // Hata/başarı mesajları
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Geri sayım süresi (2 dakika = 120 saniye)
    var timeLeft by remember { mutableStateOf(120) }
    val timerRunning = step == VerificationStep.EnterCode

    // Timer’ı çalıştır
    LaunchedEffect(timerRunning) {
        if (timerRunning) {
            timeLeft = 120
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (step) {
            VerificationStep.EnterPhone -> {
                // 🔽 Ülke kodu seçimi
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedCountryCode,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Ülke Kodu") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        countryCodes.forEach { code ->
                            DropdownMenuItem(
                                text = { Text(code) },
                                onClick = {
                                    selectedCountryCode = code
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 📱 Telefon numarası girişi
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Telefon Numarası") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 🔘 Giriş yap butonu
                Button(
                    onClick = {
                        if (phoneNumber.length != 10) {
                            // ❌ Numara geçersiz
                            errorMessage = "Telefon numarası 10 hane olmalı"
                        } else {
                            coroutineScope.launch {
                                val exists = registrationViewModel.checkingUser(phoneNumber).await()
                                if (exists) {
                                    // ✅ Kullanıcı var → SMS gönder
                                    verificationViewModel.sendVerificationCode(
                                        selectedCountryCode + phoneNumber,
                                        context as Activity,
                                        onSuccess = { step = VerificationStep.EnterCode },
                                        onFailure = { errorMessage = "Kod gönderilemedi" }
                                    )
                                } else {
                                    errorMessage = "Kullanıcı bulunamadı"
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColorScheme.primary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Giriş Yap", color = AppColorScheme.neutralLight)
                }
            }

            VerificationStep.EnterCode -> {
                // 🔑 SMS doğrulama kodu
                OutlinedTextField(
                    value = smsCode,
                    onValueChange = { smsCode = it },
                    label = { Text("SMS Kodu") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        verificationViewModel.verifyCode(smsCode)
                        if (verificationViewModel.isVerified == true) {
                            // ✅ Başarılı giriş

                            step = VerificationStep.Verified
                            navController.navigate("main") {
                                popUpTo("signin") { inclusive = true }
                            }
                        } else {
                            errorMessage = verificationViewModel.errorMessage
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColorScheme.primary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Doğrula", color = AppColorScheme.neutralLight)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ⏱ Süre ve tekrar gönderme
                if (timeLeft > 0) {
                    Text("Kalan süre: ${timeLeft}s", color = AppColorScheme.secondary)
                } else {
                    TextButton(
                        onClick = {
                            verificationViewModel.sendVerificationCode(
                                selectedCountryCode + phoneNumber,
                                context as Activity,
                                onSuccess = { timeLeft = 120 },
                                onFailure = { errorMessage = "Tekrar gönderilemedi" }
                            )
                        }
                    ) {
                        Text("Tekrar Gönder", color = AppColorScheme.info)
                    }
                }
            }

            VerificationStep.Verified -> {
                Text("Giriş başarılı!", color = AppColorScheme.success)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ⚠️ Hata mesajı
        errorMessage?.let {
            Text(it, color = AppColorScheme.error)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 🔗 Kayıt Ol yönlendirme
        TextButton(onClick = {
            navController.navigate("signup") {
                popUpTo("signin") { inclusive = true }
            }
        }) {
            Text("Kayıt Ol", color = AppColorScheme.info)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignInScreenPreview() {
    // Preview için sahte NavController
    val navController = rememberNavController()

    // Temaya uygun çalışması için MaterialTheme ile sarmaladık
    MaterialTheme {
        SignInScreen(navController = navController)
    }
}
