package com.example.warning.presentation.ui.screens

import android.app.Activity
import androidx.activity.compose.LocalActivity
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.warning.presentation.viewModel.VerificationViewModel

@Composable
fun VeriificationEntry(
    viewModel: VerificationViewModel
){

    val activity = LocalActivity.current as Activity
    VerificationScreen(viewModel, activity)
}

@Composable
fun VerificationScreen(
    viewModel: VerificationViewModel = hiltViewModel(),
    activity: Activity
) {
    val warningMessage by remember { mutableStateOf("Please geçerli bir numara girin") }
    var phoneNumber by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Telefon Numarası (+90...)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                    viewModel.sendVerificationCode(phoneNumber, activity)
                      },
            enabled = !viewModel.isLoading
        ) {
            Text("Kodu Gönder")
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = code,
            onValueChange = { code = it },
            label = { Text("Doğrulama Kodu") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.verifyCode(code) },
            enabled = !viewModel.isLoading
        ) {
            Text("Doğrula")
        }

        Spacer(modifier = Modifier.height(16.dp))

        viewModel.isLoading.let {
            if (it) {
                CircularProgressIndicator()
            }
        }

        viewModel.isVerified?.let { verified ->
            if (verified) {
                Text("✅ Telefon numarası doğrulandı!", color = Color.Green)
            } else {
                Text("❌ Doğrulama başarısız: ${viewModel.errorMessage ?: "Bilinmeyen hata"}", color = Color.Red)
            }
        }
    }
}
