package com.example.warning.presentation.viewModel

import androidx.lifecycle.ViewModel
import com.example.warning.data.local.dao.ProfileDao
import com.example.warning.data.local.entity.ProfileEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val profileDao: ProfileDao
) : ViewModel() {

    suspend fun isLoggedIn(): Boolean {
        return profileDao.getCurrentUserOnce() != null
    }
}
