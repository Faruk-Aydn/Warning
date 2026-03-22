package com.hakankuru.yanimda.presentation.viewModel

import androidx.lifecycle.ViewModel
import com.hakankuru.yanimda.data.local.dao.ProfileDao
import com.hakankuru.yanimda.data.mapper.toDomain
import com.hakankuru.yanimda.domain.model.Profile
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val profileDao: ProfileDao
) : ViewModel() {

    suspend fun isLoggedIn(): Profile? {
        return profileDao.getCurrentUserOnce()?.toDomain()
    }
}
