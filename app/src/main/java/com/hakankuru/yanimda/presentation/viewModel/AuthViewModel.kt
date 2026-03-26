package com.hakankuru.yanimda.presentation.viewModel

import androidx.lifecycle.ViewModel
import com.hakankuru.yanimda.data.local.dao.ProfileDao
import com.hakankuru.yanimda.data.local.entity.ProfileEntity
import com.hakankuru.yanimda.data.mapper.toDomain
import com.hakankuru.yanimda.domain.model.Profile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

import com.google.firebase.auth.FirebaseAuth

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val profileDao: ProfileDao
) : ViewModel() {

    fun getCurrentUserPhone(): String? {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val phone = user.phoneNumber ?: ""
            if (phone.length >= 10) {
                return phone.takeLast(10)
            }
            return phone
        }
        return null
    }

    suspend fun getCurrentUserId(): String? {
        return profileDao.getCurrentUserOnce()?.id
    }

    fun getCurrentUserProfile(): Flow<ProfileEntity?> {
        return profileDao.getCurrentUser()
    }
}
