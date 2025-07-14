package com.example.warning.presentation.ui.screens.register

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SmsVerification(
    smsCode: String,
    onCodeChange: (String) -> Unit,
    onVerify: () -> Unit,
    isLoading: Boolean
) {
    Column {
        OutlinedTextField(value = smsCode, onValueChange = onCodeChange, label = { Text("Kod") })

        Button(onClick = onVerify, enabled = !isLoading) {
            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp)) else Text("Kod Doğrula")
        }
    }
}
