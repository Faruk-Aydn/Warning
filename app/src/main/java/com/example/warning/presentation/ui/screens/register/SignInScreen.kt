package com.example.warning.presentation.ui.screens.register

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.warning.presentation.ui.components.PrimaryButton
import com.example.warning.presentation.ui.theme.AppColorScheme
import com.example.warning.presentation.viewModel.VerificationStep

/**
 * Sign-in ekranının UI durumunu temsil eden sade data class.
 *
 * Bu sınıf yalnızca UI'nın ihtiyaç duyduğu verileri içerir; herhangi bir iş kuralı içermez.
 * Böylece state, üst seviyedeki (örneğin WarningNavGraph veya ViewModel) bir katmandan
 * bu ekrana "hoist" edilerek kolayca yönetilebilir.
 */
data class SignInUiState(
    val expanded: Boolean = false,
    val selectedCountryCode: String = "+90",
    val phoneNumber: String = "",
    val step: VerificationStep = VerificationStep.EnterPhone,
    val smsCode: String = "",
    val errorMessage: String? = null,
    val timeLeft: Int = 120
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    state: SignInUiState,
    onExpandedChange: (Boolean) -> Unit,
    onCountrySelected: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onRequestCodeClick: () -> Unit,
    onSmsCodeChange: (String) -> Unit,
    onVerifyClick: () -> Unit,
    onResendClick: () -> Unit,
    onErrorDismiss: () -> Unit,
    onSignUpClick: () -> Unit
) {
    // Hoisted state içinden okunacak sade yerel değişkenler
    val step = state.step
    val timeLeft = state.timeLeft
    val errorMessage = state.errorMessage

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
                    expanded = state.expanded,
                    onExpandedChange = { onExpandedChange(!state.expanded) }
                ) {
                    OutlinedTextField(
                        value = state.selectedCountryCode,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Ülke Kodu") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = state.expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = state.expanded,
                        onDismissRequest = { onExpandedChange(false) }
                    ) {
                        val countryCodes = listOf("+90", "+1", "+44", "+49")
                        countryCodes.forEach { code ->
                            DropdownMenuItem(
                                text = { Text(code) },
                                onClick = {
                                    onCountrySelected(code)
                                    onExpandedChange(false)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 📱 Telefon numarası girişi
                OutlinedTextField(
                    value = state.phoneNumber,
                    onValueChange = onPhoneNumberChange,
                    label = { Text("Telefon Numarası") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 🔘 Giriş yap butonu
                PrimaryButton(
                    text = "Giriş Yap",
                    onClick = onRequestCodeClick
                )
            }

            VerificationStep.EnterCode -> {
                // 🔑 SMS doğrulama kodu
                OutlinedTextField(
                    value = state.smsCode,
                    onValueChange = onSmsCodeChange,
                    label = { Text("SMS Kodu") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                PrimaryButton(
                    text = "Doğrula",
                    onClick = onVerifyClick
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ⏱ Süre ve tekrar gönderme
                if (timeLeft > 0) {
                    Text("Kalan süre: ${timeLeft}s", color = AppColorScheme.secondary)
                } else {
                    TextButton(
                        onClick = onResendClick
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
            Text(
                text = it,
                color = AppColorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 🔗 Kayıt Ol yönlendirme
        TextButton(onClick = onSignUpClick) {
            Text("Kayıt Ol", color = AppColorScheme.info)
        }
    }
}
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignInScreenPreview() {
    MaterialTheme {
        SignInScreen(
            state = SignInUiState(),
            onExpandedChange = {},
            onCountrySelected = {},
            onPhoneNumberChange = {},
            onRequestCodeClick = {},
            onSmsCodeChange = {},
            onVerifyClick = {},
            onResendClick = {},
            onErrorDismiss = {},
            onSignUpClick = {}
        )
    }
}
