package com.example.warning.presentation.viewModel


sealed class UserRegistrationState {
    object CheckingRegistration : UserRegistrationState()          // Kayıt kontrolü yapılıyor (loading)
    object RegistrationConfirmed : UserRegistrationState()          // Kayıt var, Firebase kayıt bekleniyor
    object RegistrationSuccess : UserRegistrationState()            // Firebase kayıt başarılı, Room yazılıyor
    object LoadingFromRoom : UserRegistrationState()                 // Room'dan veri çekiliyor
    data class Error(val message: String) : UserRegistrationState()  // Hata durumu
    object Idle : UserRegistrationState()                            // Boş / bekleme durumu
}
