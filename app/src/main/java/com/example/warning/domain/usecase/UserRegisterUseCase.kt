package com.example.warning.domain.usecase

import com.example.warning.data.repository.ProfileRepositoryImpl
import com.example.warning.domain.model.Profile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class UserRegistrationState {
    object CheckingRegistration : UserRegistrationState()          // Kayıt kontrolü yapılıyor (loading)
    object RegistrationConfirmed : UserRegistrationState()          // Kayıt var, Firebase kayıt bekleniyor
    object RegistrationSuccess : UserRegistrationState()            // Firebase kayıt başarılı, Room yazılıyor
    object LoadingFromRoom : UserRegistrationState()                 // Room'dan veri çekiliyor
    data class Error(val message: String) : UserRegistrationState()  // Hata durumu
    object Idle : UserRegistrationState()                            // Boş / bekleme durumu
}

class UserRegistrationUseCase(
    private val repository: ProfileRepositoryImpl // senin interface'in burada olmalı
) {

    // Flow ile UI'a durum göndermek için
    private val _state = MutableStateFlow<UserRegistrationState>(UserRegistrationState.Idle)
    val state: StateFlow<UserRegistrationState> = _state

    suspend fun checkAndRegisterUser(user: Profile) {
        _state.value = UserRegistrationState.CheckingRegistration
        try {
            val isRegistered = repository.isRegistered(user.phoneNumber)
            if (isRegistered) {
                _state.value = UserRegistrationState.RegistrationConfirmed

                val addResult = repository.addUser(user)
                if (addResult) {
                    _state.value = UserRegistrationState.RegistrationSuccess

                    // Firebase kaydı başarılı, şimdi Room'a yaz
                    val localAddResult = repository.addLocal(user.phoneNumber)
                    if (localAddResult) {
                        _state.value = UserRegistrationState.LoadingFromRoom
                        // dönüşümler ve diğer local tablolarını yapacam
                        repository.startAllListener(user.phoneNumber)
                        // Burada UI Room'dan veriyi observe etmeli (örn. LiveData veya Flow)
                        // Domain katmanı burada Room verisini direkt almaz, UI veya ViewModel gözlem yapar
                    } else {
                        _state.value = UserRegistrationState.Error("Local database yazma başarısız")
                    }

                } else {
                    _state.value = UserRegistrationState.Error("Kullanıcı Firebase'e eklenemedi")
                }

            } else {
                _state.value = UserRegistrationState.Error("Kullanıcı kayıtlı değil")
            }
        } catch (e: Exception) {
            _state.value = UserRegistrationState.Error(e.localizedMessage ?: "Bilinmeyen hata")
        }
    }
}
