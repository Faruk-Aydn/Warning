package com.hakankuru.yanimda.data.remote.Dto

/**
 * Backend'den dönen cevap.
 * successCount / failureCount, senin EmergencyState.Success ile uyumlu.
 */
data class EmergencyResponseDto(
    val successCount: Int,
    val failureCount: Int
)