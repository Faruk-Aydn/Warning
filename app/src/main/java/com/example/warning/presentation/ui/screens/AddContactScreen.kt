package com.example.warning.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.warning.presentation.ui.theme.AppColorScheme

/**
 * Kişi ekleme ekranı için sade UI state modeli.
 *
 * - Ekran, sadece bu state ve event callback'leri ile yönetilir.
 * - ViewModel, navigasyon ve side-effect (Toast vb.) mantığı üst seviyede (ör. WarningNavGraph) tutulur.
 */
data class AddContactUiState(
    val country: String = "+90",
    val phone: String = "",
    val isDropdownExpanded: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactScreen(
    state: AddContactUiState,
    onBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onCountryDropdownToggle: () -> Unit,
    onCountrySelected: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onSubmitClick: () -> Unit
) {
    val countryOptions = listOf("+90", "+1", "+44", "+49", "+33")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Kişi Ekle", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        onNavigateHome()
                    }) {
                        Icon(imageVector = Icons.Default.Home, contentDescription = "Ana Sayfa")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Ülke kodu seçimi
                Column(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = state.country,
                        onValueChange = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCountryDropdownToggle() },
                        readOnly = true,
                        label = { Text("Ülke Kodu") },
                        colors = TextFieldDefaults.colors()
                    )

                    androidx.compose.material3.DropdownMenu(
                        expanded = state.isDropdownExpanded,
                        onDismissRequest = { onCountryDropdownToggle() }
                    ) {
                        countryOptions.forEach { option ->
                            androidx.compose.material3.DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    onCountrySelected(option)
                                }
                            )
                        }
                    }
                }

                // Phone input
                OutlinedTextField(
                    value = state.phone,
                    onValueChange = { input ->
                        val filtered = input.filter { it.isDigit() }
                        onPhoneChange(filtered)
                    },
                    modifier = Modifier.weight(2f),
                    label = { Text("Telefon Numarası") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            Button(
                onClick = onSubmitClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColorScheme.primary,
                    contentColor = AppColorScheme.neutralLight
                )
            ) {
                Text(
                    text = "Ara / Ekle",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}


