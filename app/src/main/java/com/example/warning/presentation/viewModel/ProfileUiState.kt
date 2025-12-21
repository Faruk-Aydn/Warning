package com.example.warning.presentation.viewModel

data class ProfileUiState(
    val name: String = "",
    val phone: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)