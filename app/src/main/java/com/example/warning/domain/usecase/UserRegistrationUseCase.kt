package com.example.warning.domain.usecase

import android.util.Log.e
import com.example.warning.data.repository.FirebaseRepositoryImpl
import com.example.warning.domain.model.Profile
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import android.util.Log
import com.example.warning.domain.repository.ProfileRepository
import com.example.warning.presentation.viewModel.UserRegistrationState

class UserRegistrationUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepositoryImpl
) {

    // Flow ile UI'a durum göndermek için
    private val _state = MutableStateFlow<UserRegistrationState>(UserRegistrationState.Idle)
    val state: StateFlow<UserRegistrationState> = _state


    suspend fun checkUser(phone: String): Boolean{
        return firebaseRepository.isRegistered(phone)
    }

    suspend fun getUser(phone: String){

        val isRegistered = firebaseRepository.isRegistered(phone)

        if (isRegistered) {
            _state.value = UserRegistrationState.RegistrationConfirmed

            Log.wtf("Check", "checkAndGetUser() başladı")
            // Firebase kontrolü, şimdi Room'a yaz
            val localAddResult = firebaseRepository.getUser(phone)

            Log.wtf("Check", "  wtf  çekd,k başladı")
            if (localAddResult != null) {
                Log.wtf("Check", "  nul değil başladı")
                _state.value = UserRegistrationState.LoadingFromRoom
                // dönüşümler ve diğer local tablolarını yapacam
                firebaseRepository.startUserListener(phone)
                Log.wtf("Check", "  dinledi başladı")

                firebaseRepository.startContactListener(phone)
                Log.wtf("Listener", "startContactListener")

                firebaseRepository.startLinkedListener(phone)
                Log.w("Listener", "startLinkedListener")

                delay(2000)

                // Burada UI Room'dan veriyi observe etmeli (örn. LiveData veya Flow)
                // Domain katmanı burada Room verisini direkt almaz, UI veya ViewModel gözlem yapar
            } else {
                _state.value = UserRegistrationState.Error("Local database yazma başarısız")
            }
        } else {
            _state.value = UserRegistrationState.Error("Kullanıcı zaten kayıtlı. giriş ysp")
        }
    }

    suspend fun checkAndRegisterUser(user: Profile): Boolean {

        Log.wtf("Check", "  try dışı")
        _state.value = UserRegistrationState.CheckingRegistration
        try {

            Log.wtf("Check", "  try içi")
            val isRegistered = firebaseRepository.isRegistered(user.phoneNumber)
            if (isRegistered == false) {
                _state.value = UserRegistrationState.RegistrationConfirmed

                Log.i("addResult","başlatılıyorr")
                val addResult = firebaseRepository.addUser(user)
                if (addResult) {
                    _state.value = UserRegistrationState.RegistrationSuccess

                    Log.i("addResult", " checkAndRegisterUser() başladı, addResult = true")
                    // Firebase kaydı başarılı, şimdi Room'a yaz
                    val localAddResult = firebaseRepository.getUser(user.phoneNumber)

                    if (localAddResult != null) {
                        Log.i("Firestore", "  get işlemi tamamlandı ve nul değil")
                        _state.value = UserRegistrationState.LoadingFromRoom
                        // dönüşümler ve diğer local tablolarını yapacam
                        firebaseRepository.startUserListener(user.phoneNumber)
                        Log.wtf("Check", "  dinledi başladı")

                        firebaseRepository.startContactListener(user.phoneNumber)
                        Log.wtf("Listener", "startContactListener")

                        firebaseRepository.startLinkedListener(user.phoneNumber)
                        Log.w("Listener", "startLinkedListener")

                        _state.value = UserRegistrationState.RegistrationSuccess
                        return true
                        // Burada UI Room'dan veriyi observe etmeli (örn. LiveData veya Flow)
                        // Domain katmanı burada Room verisini direkt almaz, UI veya ViewModel gözlem yapar
                    } else {
                        Log.w("get local",  "addResult = null")
                        _state.value = UserRegistrationState.Error("Local database yazma başarısız")
                    }

                } else {
                    Log.w("Firebase ","Kullanıcı Firebase'e eklenemedi")
                    _state.value = UserRegistrationState.Error("Kullanıcı Firebase'e eklenemedi")
                }

            } else {
                Log.w("firestore","Kullanıcı zaten kayıtlı. giriş ysp")
                _state.value = UserRegistrationState.Error("Kullanıcı zaten kayıtlı. giriş ysp")
            }
        } catch (e: Exception) {
            _state.value = UserRegistrationState.Error(e.localizedMessage ?: "Bilinmeyen hata")
        }
        return false
    }
}
