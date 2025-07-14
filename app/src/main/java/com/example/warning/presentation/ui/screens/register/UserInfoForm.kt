package com.example.warning.presentation.ui.screens.register

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun UserInfoForm(
    name: String,
    onNameChange: (String) -> Unit,
    countryCode: String,
    onCountryCodeChange: (String) -> Unit,
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    locationPermission: Boolean,
    onLocationPermissionChange: (Boolean) -> Unit,
    contactPermission: Boolean,
    onContactPermissionChange: (Boolean) -> Unit,
    onSubmit: () -> Unit,
    isLoading: Boolean
) {
    Column {
        OutlinedTextField(value = name, onValueChange = onNameChange, label = { Text("İsim") })

        OutlinedTextField(value = countryCode, onValueChange = onCountryCodeChange, label = { Text("Ülke Kodu") })

        OutlinedTextField(value = phoneNumber, onValueChange = onPhoneNumberChange, label = { Text("Telefon") })

        Row {
            Checkbox(checked = locationPermission, onCheckedChange = onLocationPermissionChange)
            Text("Lokasyon izni")
        }

        Row {
            Checkbox(checked = contactPermission, onCheckedChange = onContactPermissionChange)
            Text("Rehber izni")
        }

        Button(onClick = onSubmit, enabled = !isLoading) {
            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp)) else Text("Doğrula")
        }
    }
}
