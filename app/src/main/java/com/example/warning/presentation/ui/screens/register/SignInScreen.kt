package com.example.warning.presentation.ui.screens.register

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.warning.presentation.ui.components.PrimaryButton
import com.example.warning.presentation.ui.theme.AppColorScheme
import com.example.warning.presentation.viewModel.ContactListenerViewmodel
import com.example.warning.presentation.viewModel.ProfileListenerViewModel
import com.example.warning.presentation.viewModel.RegistrationViewModel
import com.example.warning.presentation.viewModel.VerificationStep
import com.example.warning.presentation.viewModel.VerificationViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    navController: NavHostController,
    registrationViewModel: RegistrationViewModel = hiltViewModel(),
    verificationViewModel: VerificationViewModel = hiltViewModel(),
    userview: ProfileListenerViewModel = hiltViewModel(),
    contactview: ContactListenerViewmodel= hiltViewModel()
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
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
                PrimaryButton(
                    text = "Giriş Yap",
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
                }
                )
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

                PrimaryButton(
                    text = "Doğrula",
                    onClick = {
                        verificationViewModel.verifyCode(smsCode)
                        if (verificationViewModel.isVerified == true) {
                            // ✅ Başarılı giriş

                            step = VerificationStep.Verified
                        } else {
                            errorMessage = verificationViewModel.errorMessage
                        }
                    }
                )

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
                Log.i("signIn","giriş başarılı")
                userview.startUserListener(phoneNumber)
                contactview.startContactListener(phoneNumber)
                navController.navigate("main") {
                    popUpTo("signIn") { inclusive = true }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ⚠️ Hata mesajı
        errorMessage?.let {
            Text(
                it,
                color = AppColorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
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
