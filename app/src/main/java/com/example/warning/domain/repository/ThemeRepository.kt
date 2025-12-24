package com.example.warning.domain.repository

import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    val isDarkTheme: Flow<Boolean>
    suspend fun setDarkTheme(isDark: Boolean)
}
