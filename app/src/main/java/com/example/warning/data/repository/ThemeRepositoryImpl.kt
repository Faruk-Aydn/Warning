package com.example.warning.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.warning.domain.repository.ThemeRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class ThemeRepositoryImpl @Inject constructor(
    private val context: Context
) : ThemeRepository {

    private val prefs: SharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    private val KEY_IS_DARK_THEME = "is_dark_theme"

    override val isDarkTheme: Flow<Boolean> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == KEY_IS_DARK_THEME) {
                trySend(sharedPreferences.getBoolean(key, false))
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }.onStart {
        emit(prefs.getBoolean(KEY_IS_DARK_THEME, false))
    }

    override suspend fun setDarkTheme(isDark: Boolean) {
        prefs.edit().putBoolean(KEY_IS_DARK_THEME, isDark).apply()
    }
}
