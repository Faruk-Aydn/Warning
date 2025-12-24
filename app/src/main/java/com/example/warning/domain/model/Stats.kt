package com.example.warning.domain.model

data class Stats(
    val sentCount: Int = 0,
    val receivedCount: Int = 0,
    val lastMessageTime: String = "-"
)
