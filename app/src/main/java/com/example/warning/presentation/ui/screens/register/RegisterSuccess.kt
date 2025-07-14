package com.example.warning.presentation.ui.screens.register

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RegisterSuccess() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp) // en az 200dp
            .padding(16.dp)
    ) {
        Text("✔️ Kayıt başarılı!")
        // Devam et butonu vs.
    }
}
