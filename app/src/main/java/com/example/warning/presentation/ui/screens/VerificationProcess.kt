package com.example.warning.presentation.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.warning.presentation.viewModel.VerificationStep
import com.example.warning.presentation.viewModel.VerificationViewModel
import kotlinx.coroutines.delay

@Composable
fun VerificationProcess(viewModel: VerificationViewModel = hiltViewModel()) {

    var step by remember { mutableStateOf(VerificationStep.EnterPhone) }
    var phoneNumber by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    val context = LocalContext.current
    val activity = (LocalActivity.current as? Activity)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        when (step) {
            VerificationStep.EnterPhone -> {
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Telefon Numarası (+90...)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (phoneNumber.length == 13 && phoneNumber.startsWith("+90")) {
                            if (activity != null) {
                                viewModel.sendVerificationCode(phoneNumber, activity)
                                step = VerificationStep.EnterCode
                            } else {
                                Toast.makeText(context, "Activity bulunamadı", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Lütfen '+90' ile başlayan geçerli bir numara girin", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = !viewModel.isLoading
                ) {
                    Text("Kodu Gönder")
                }
            }

            VerificationStep.EnterCode -> {
                Text("Telefon numarasına SMS ile gönderilen kodu girin")
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Doğrulama Kodu") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        viewModel.verifyCode(code)
                    },
                    enabled = !viewModel.isLoading
                ) {
                    Text("Doğrula")
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (viewModel.isLoading) {
                    CircularProgressIndicator()
                }

                viewModel.isVerified?.let { verified ->
                    if (verified) {
                        Text("✅ Telefon numarası doğrulandı!", color = Color.Green)
                        LaunchedEffect(Unit) {
                            delay(1000)
                            step = VerificationStep.Verified
                        }
                    } else {
                        Text(
                            "❌ Doğrulama başarısız: ${viewModel.errorMessage ?: "Bilinmeyen hata"}",
                            color = Color.Red
                        )
                    }
                }
            }

            VerificationStep.Verified -> {
                Text("Telefon numaranız başarıyla doğrulandı!")
                // İstersen sonraki adımlar için buton koyabilirsin
            }
        }
    }
}
