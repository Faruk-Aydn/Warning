package com.example.warning.presentation.ui.signup

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.warning.presentation.viewModel.VerificationViewModel

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: VerificationViewModel = hiltViewModel()
) {
    var phoneNumber by remember { mutableStateOf("") }
    var isPhoneValid by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var codeSent by remember { mutableStateOf(false) }
    var verificationCode by remember { mutableStateOf("") }

    val context = LocalContext.current

    // Telefon numarasının geçerli olup olmadığını kontrol et
    LaunchedEffect(phoneNumber) {
        isPhoneValid = phoneNumber.length == 10 && phoneNumber.all { it.isDigit() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Giriş Yap",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Telefon Numarası") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    isLoading = true
                    viewModel.sendVerificationCode(
                        "+90$phoneNumber",
                        context as Activity,
                        onSuccess = {
                            isLoading = false
                            codeSent = true
                        },
                        onFailure = {
                            isLoading = false
                        }
                    )
                },
                enabled = isPhoneValid && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Doğrula")
                }
            }
        }

        if (codeSent) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = verificationCode,
                onValueChange = { verificationCode = it },
                label = { Text("Doğrulama Kodu") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            viewModel.verifyCode(verificationCode)
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = {
                    // örneğin doğrulama kodunu kontrol ettikten sonra ana ekrana geçilebilir
                    navController.navigate("home_screen")
                },
                enabled = verificationCode.isNotBlank()
            ) {
                Text("Giriş Yap")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { navController.navigate("register") }) {
            Text("Hesabınız yok mu? Kayıt olun")
        }
    }
}
