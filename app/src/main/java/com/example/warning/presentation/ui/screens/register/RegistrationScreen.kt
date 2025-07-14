package com.example.warning.presentation.ui.screens.register

import android.R
import android.R.attr.name
import android.R.attr.phoneNumber
import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.warning.domain.model.Profile
import com.example.warning.presentation.viewModel.RegistrationViewModel
import com.example.warning.presentation.viewModel.VerificationStep
import com.example.warning.presentation.viewModel.VerificationViewModel

@Composable
fun RegistrationScreen(
    registrationViewModel: RegistrationViewModel = hiltViewModel(),
    verificationViewModel: VerificationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as Activity

    var currentStep by rememberSaveable { mutableStateOf(VerificationStep.EnterPhone) }

    // Kullanıcı bilgileri
    var name by rememberSaveable { mutableStateOf("") }
    var countryCode by rememberSaveable { mutableStateOf("+90") }
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var locationPermission by rememberSaveable { mutableStateOf(false) }
    var contactPermission by rememberSaveable { mutableStateOf(false) }
    var smsCode by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        when (currentStep) {
            VerificationStep.EnterPhone -> {
                UserInfoForm(
                    name = name,
                    onNameChange = { name = it },
                    countryCode = countryCode,
                    onCountryCodeChange = { countryCode = it },
                    phoneNumber = phoneNumber,
                    onPhoneNumberChange = { phoneNumber = it },
                    locationPermission = locationPermission,
                    onLocationPermissionChange = { locationPermission = it },
                    contactPermission = contactPermission,
                    onContactPermissionChange = { contactPermission = it },
                    onSubmit = {
                        val fullPhone = countryCode + phoneNumber
                        verificationViewModel.sendVerificationCode(
                            phoneNumber = fullPhone,
                            activity = activity,
                            onSuccess = { currentStep = VerificationStep.EnterCode },
                            onFailure = { /* Hata mesajı gösterilebilir */ }
                        )
                    },
                    isLoading = verificationViewModel.isLoading
                )
            }

            VerificationStep.EnterCode -> {
                SmsVerification(
                    smsCode = smsCode,
                    onCodeChange = { smsCode = it },
                    onVerify = {
                        verificationViewModel.verifyCode(smsCode)
                    },
                    isLoading = verificationViewModel.isLoading
                )

                if (verificationViewModel.isVerified == true) {
                    LaunchedEffect(verificationViewModel.isVerified) {
                        val fullPhone = countryCode + R.attr.phoneNumber
                        val isRegistered = registrationViewModel.checkingUser(fullPhone)
                        if (!isRegistered) {
                            val profile = Profile(
                                name = name,
                                phoneNumber = phoneNumber,
                                country = countryCode,
                                locationPermission = locationPermission,
                                contactPermission = contactPermission,
                                profilePhoto = "",
                                emergencyMessage = null,
                                // diğer alanlar null
                            )
                            registrationViewModel.registerUser(profile)
                        }
                        currentStep = VerificationStep.Verified
                    }
                }
            }

            VerificationStep.Verified -> {
                RegisterSuccess()
            }
        }
    }
}
