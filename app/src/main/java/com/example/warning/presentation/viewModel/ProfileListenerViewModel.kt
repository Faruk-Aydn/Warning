package com.example.warning.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warning.domain.model.Profile
import com.example.warning.domain.usecase.ProfileUseCases
import com.example.warning.domain.usecase.UpdateFCMTokenUseCase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


@HiltViewModel
class ProfileListenerViewModel @Inject constructor(
    private val profileUseCases: ProfileUseCases,
    private val updateFCMTokenUseCase: UpdateFCMTokenUseCase
) : ViewModel() {

    private val _profileState = MutableStateFlow<Profile?>(null)
    val profileState: StateFlow<Profile?> = _profileState

    init {
        // Eğer uygulama açıldığında phoneNumber mevcutsa listener başlat
        viewModelScope.launch {
            profileUseCases.getProfile()
                .filterNotNull()
                .collectLatest { profile ->
                    if (profile == null) {
                        Log.w("Listener", "profile null : ${profile}. listener başlatılamadı. localden çekemedk")
                    }else{
                        startUserListener(profile.phoneNumber)
                    }
            }
        }
    }
    fun loadProfile() {
        viewModelScope.launch {
            profileUseCases.getProfile().collectLatest { _profileState.value = it }
        }
    }

    fun startUserListener(phone: String){
        viewModelScope.launch {
            profileUseCases.startUserListener(phone)
        }
    }
    fun stopUserListener(){
         viewModelScope.launch {
             profileUseCases.stopUserListener()
         }
    }

    // --- YENİ EKLENEN FONKSİYON: Token Kontrolü ---
    fun checkAndRefreshFCMToken() {
        viewModelScope.launch {
            try {
                // 1. Cihazın şu anki güncel token'ını Firebase SDK'dan al
                val currentToken = FirebaseMessaging.getInstance().token.await()
                Log.i("TokenCheck", "Cihaz token'ı alındı: $currentToken")

                // 2. UseCase'i çağır.
                // Not: UseCase içinde zaten "Eski token ile yeni token aynı mı?" kontrolü var.
                // Eğer farklıysa sunucuyu günceller, aynıysa işlem yapmaz.
                updateFCMTokenUseCase.execute(currentToken)

            } catch (e: Exception) {
                Log.e("TokenCheck", "Token kontrolü sırasında hata oluştu", e)
            }
        }
    }
}