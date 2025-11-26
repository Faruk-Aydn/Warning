package com.example.warning.domain.usecase

sealed class EmergencyState {
    object Idle : EmergencyState()
    object Loading : EmergencyState()
    data class Success(
        val successCount: Int,
        val failureCount: Int
    ) : EmergencyState()

    data class Error(val message: String) : EmergencyState()
}