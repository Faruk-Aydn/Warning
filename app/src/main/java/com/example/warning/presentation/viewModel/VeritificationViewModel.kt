package com.example.warning.presentation.viewModel

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Inject

enum class VerificationStep {
    EnterPhone,
    EnterCode,
    Verified
}
@HiltViewModel
class VerificationViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var isVerified by mutableStateOf<Boolean?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var verificationId by mutableStateOf<String?>(null)

    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    fun sendVerificationCode(
        phoneNumber: String,
        activity: Activity,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        isLoading = true
        errorMessage = null
        isVerified = null

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // "+90xxxxxxxxxx"
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Otomatik kod alınabilir ise
                    verifyWithCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.e("FirebaseAuth", "Hata: 2 ${e.message}")
                    onFailure()
                    isLoading = false
                    errorMessage = e.localizedMessage ?: "Doğrulama başarısız"
                }

                override fun onCodeSent(
                    id: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    super.onCodeSent(id, token)
                    onSuccess()
                    verificationId = id
                    resendToken = token
                    Log.e("FirebaseAuth", "Hata: yok")
                    isLoading = false
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyCode(code: String) {
        val id = verificationId
        if (id == null) {
            Log.e("FirebaseAuth", "Hata: Doğrulama kodu alınamadı")
            errorMessage = "Doğrulama kodu alınamadı"
            return
        }

        isLoading = true
        val credential = PhoneAuthProvider.getCredential(id, code)
        verifyWithCredential(credential)
    }

    private fun verifyWithCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    isVerified = true
                    errorMessage = null
                } else {
                    Log.e("FirebaseAuth", "Hata: 1 ${task.exception?.message}")
                    isVerified = false
                    errorMessage = task.exception?.localizedMessage ?: "Doğrulama başarısız"
                }
            }
    }
}
