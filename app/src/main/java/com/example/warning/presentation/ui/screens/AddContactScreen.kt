package com.example.warning.presentation.ui.screens

import android.R.attr.label
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.warning.domain.usecase.AddContactResult
import com.example.warning.presentation.ui.theme.AppColorScheme
import com.example.warning.presentation.viewModel.ContactListenerViewmodel
import com.example.warning.presentation.viewModel.RegistrationViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactScreen(
    navController: NavController,
    contactVm: ContactListenerViewmodel = hiltViewModel(),
    registrationVm: RegistrationViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var country by remember { mutableStateOf("+90") }
    var phone by remember { mutableStateOf("") }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    val countryOptions = listOf("+90", "+1", "+44", "+49", "+33")

    LaunchedEffect(contactVm.addContactState) {
        contactVm.addContactState.collect { state ->
            when (state) {
                is AddContactResult.Success -> {
                    Toast.makeText(context, "Kişi eklendi", Toast.LENGTH_SHORT).show()
                }
                is AddContactResult.Error -> {
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
                is AddContactResult.NotFound -> {
                    Log.w("state", "not found")
                    Toast.makeText(context, "Kullanıcı bulunamadı", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Kişi Ekle", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("main") {
                            popUpTo(0)
                            launchSingleTop = true
                        }
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
                // Country dropdown
                Column(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = country.toString(),
                        onValueChange = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isDropdownExpanded = !isDropdownExpanded },
                        readOnly = true,
                        label = { Text("Ülke Kodu") },
                        colors = TextFieldDefaults.colors()
                    )

                    androidx.compose.material3.DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        countryOptions.forEach { option ->
                            androidx.compose.material3.DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    country = option
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Phone input
                OutlinedTextField(
                    value = phone,
                    onValueChange = { input ->
                        val filtered = input.filter { it.isDigit() }
                        phone = filtered
                    },
                    modifier = Modifier.weight(2f),
                    label = { Text("Telefon Numarası") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            Button(
                onClick = {
                    scope.launch {
                        try {
                            val exists = registrationVm.checkingUser(phone).await()
                            if (!exists) {
                                Log.w("check in screen", "${context}. kontrol ettik yok")
                                Toast.makeText(context, "Kullanıcı bulunamadı", Toast.LENGTH_SHORT).show()
                            } else {
                                contactVm.addContact(phone, country)
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, e.message ?: "Hata oluştu", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
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


