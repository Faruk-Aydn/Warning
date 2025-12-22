package com.example.warning.presentation.ui.navigation

import android.app.Activity
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import com.example.warning.presentation.ui.screens.*
import com.example.warning.presentation.ui.screens.register.SignInScreen
import com.example.warning.presentation.ui.screens.register.SignInUiState
import com.example.warning.presentation.ui.screens.register.SignUpScreen
import com.example.warning.presentation.viewModel.ContactActionsViewModel
import com.example.warning.presentation.viewModel.ContactListenerViewmodel
import com.example.warning.presentation.viewModel.EmergencyMessageViewModel
import com.example.warning.presentation.viewModel.LinkedActionsViewModel
import com.example.warning.presentation.viewModel.ProfileListenerViewModel
import com.example.warning.presentation.viewModel.RegistrationViewModel
import com.example.warning.presentation.viewModel.VerificationStep
import com.example.warning.presentation.viewModel.VerificationViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WarningNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        modifier = modifier
    ) {
        // Splash Screen
        composable(Routes.SPLASH) {
            SplashScreen(navController = navController)
        }

        // Giriş Ekranı
        composable(Routes.SIGN_IN) {
            val registrationViewModel: RegistrationViewModel = hiltViewModel()
            val verificationViewModel: VerificationViewModel = hiltViewModel()
            val profileListenerViewModel: ProfileListenerViewModel = hiltViewModel()
            val contactListenerViewModel: ContactListenerViewmodel = hiltViewModel()

            // Ekran durumunu hoist eden sade state
            var countryExpanded by remember { mutableStateOf(false) }
            var selectedCountryCode by remember { mutableStateOf("+90") }
            var phoneNumber by remember { mutableStateOf("") }
            var step by remember { mutableStateOf(VerificationStep.EnterPhone) }
            var smsCode by remember { mutableStateOf("") }
            var errorMessage by remember { mutableStateOf<String?>(null) }
            var timeLeft by remember { mutableStateOf(120) }
            val timerRunning = step == VerificationStep.EnterCode

            // SMS kodu beklenirken geri sayım – sadece nav graph seviyesinde yönetilir
            LaunchedEffect(timerRunning) {
                if (timerRunning) {
                    timeLeft = 120
                    while (timeLeft > 0 && step == VerificationStep.EnterCode) {
                        delay(1000)
                        timeLeft--
                    }
                }
            }

            SignInScreen(
                state = SignInUiState(
                    expanded = countryExpanded,
                    selectedCountryCode = selectedCountryCode,
                    phoneNumber = phoneNumber,
                    step = step,
                    smsCode = smsCode,
                    errorMessage = errorMessage,
                    timeLeft = timeLeft
                ),
                onExpandedChange = { countryExpanded = it },
                onCountrySelected = { selectedCountryCode = it },
                onPhoneNumberChange = { phoneNumber = it },
                onRequestCodeClick = {
                    if (phoneNumber.length != 10) {
                        // Basit validasyon hatası – ekran state'i üzerinden gösterilir
                        errorMessage = "Telefon numarası 10 hane olmalı"
                    } else {
                        scope.launch {
                            val exists = registrationViewModel.checkingUser(phoneNumber).await()
                            if (exists) {
                                verificationViewModel.sendVerificationCode(
                                    selectedCountryCode + phoneNumber,
                                    context as Activity,
                                    onSuccess = {
                                        step = VerificationStep.EnterCode
                                        errorMessage = null
                                    },
                                    onFailure = {
                                        errorMessage = "Kod gönderilemedi"
                                    }
                                )
                            } else {
                                errorMessage = "Kullanıcı bulunamadı"
                            }
                        }
                    }
                },
                onSmsCodeChange = { smsCode = it },
                onVerifyClick = {
                    verificationViewModel.verifyCode(smsCode)
                    if (verificationViewModel.isVerified == true) {
                        // Kullanıcı ve kişi dinleyicilerini başlat
                        profileListenerViewModel.startUserListener(phoneNumber)
                        contactListenerViewModel.startContactListener(phoneNumber)

                        // Başarılı giriş sonrası ana ekrana yönlendir
                        navController.navigate(Routes.MAIN) {
                            popUpTo(Routes.SIGN_IN) { inclusive = true }
                        }
                    } else {
                        errorMessage = verificationViewModel.errorMessage
                    }
                },
                onResendClick = {
                    verificationViewModel.sendVerificationCode(
                        selectedCountryCode + phoneNumber,
                        context as Activity,
                        onSuccess = {
                            timeLeft = 120
                            errorMessage = null
                        },
                        onFailure = { errorMessage = "Tekrar gönderilemedi" }
                    )
                },
                onErrorDismiss = { errorMessage = null },
                onSignUpClick = {
                    navController.navigate(Routes.SIGN_UP) {
                        popUpTo(Routes.SIGN_IN) { inclusive = true }
                    }
                }
            )
        }

        // Kayıt Ekranı
        composable(Routes.SIGN_UP) {
            // TODO: SignUpScreen de stateless olacak şekilde benzer biçimde hoist edilebilir
            SignUpScreen(navController = navController)
        }

        // Ana Ekran
        composable(Routes.MAIN) {
            val profileViewModel: ProfileListenerViewModel = hiltViewModel()
            val contactViewModel: ContactListenerViewmodel = hiltViewModel()
            val emergencyViewModel: EmergencyMessageViewModel = hiltViewModel()

            val profile by profileViewModel.profileState.collectAsState()
            val contacts by contactViewModel.contacts.collectAsState()
            val emergencyState by emergencyViewModel.emergencyMessageState.collectAsState()

            MainScreen(
                profile = profile,
                contactCount = contacts.size,
                emergencyState = emergencyState,
                onProfileClick = { navController.navigate(Routes.PROFILE) },
                onNotificationsClick = { navController.navigate(Routes.NOTIFICATIONS) },
                onContactsClick = { navController.navigate(Routes.CONTACTS) },
                onDrawerDestinationClick = { route -> navController.navigate(route) },
                onEmergencyClick = { emergencyViewModel.sendEmergencyMessage() },
                onEmergencyDialogDismiss = { emergencyViewModel.resetState() }
            )
        }

        // Profil Ekranı
        composable(Routes.PROFILE) {
            ProfileScreen(navController = navController)
        }

        // Ayarlar Ekranı
        composable(Routes.SETTINGS) {
            SettingsScreen()
        }

        // Bağlantılar/Kişiler Ekranı
        composable(Routes.CONTACTS) {
            val contactListenerViewModel: ContactListenerViewmodel = hiltViewModel()
            val contactActionsViewModel: ContactActionsViewModel = hiltViewModel()
            val linkedActionsViewModel: LinkedActionsViewModel = hiltViewModel()

            val contacts by contactListenerViewModel.contacts.collectAsState()
            val linked by contactListenerViewModel.linked.collectAsState()
            val actionState by contactActionsViewModel.uiState.collectAsState()
            val linkedActionState by linkedActionsViewModel.uiState.collectAsState()

            // Hata / başarı mesajlarını merkezi olarak yönet
            LaunchedEffect(actionState.lastResult) {
                when (val r = actionState.lastResult) {
                    is com.example.warning.domain.usecase.ContactActionResult.Success -> {
                        Toast.makeText(context, "İşlem başarılı", Toast.LENGTH_SHORT).show()
                        contactListenerViewModel.loadContact()
                    }
                    is com.example.warning.domain.usecase.ContactActionResult.Error -> {
                        Toast.makeText(context, r.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }

            LaunchedEffect(linkedActionState.lastResult) {
                when (val r = linkedActionState.lastResult) {
                    is com.example.warning.domain.usecase.LinkedActionResult.Success -> {
                        Toast.makeText(context, "İşlem başarılı", Toast.LENGTH_SHORT).show()
                        contactListenerViewModel.loadLinked()
                    }
                    is com.example.warning.domain.usecase.LinkedActionResult.Error -> {
                        Toast.makeText(context, r.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }

            var isRefreshing by remember { mutableStateOf(false) }

            ContactLinkedScreen(
                contacts = contacts,
                linked = linked,
                isRefreshing = isRefreshing,
                isContactLoading = { contact -> actionState.loadingContactId == contact.id },
                isLinkedLoading = { link -> linkedActionState.loadingLinkedId == link.id },
                onBack = { navController.popBackStack() },
                onAddContactClick = { navController.navigate(Routes.ADD_CONTACT) },
                onRefresh = {
                    scope.launch {
                        isRefreshing = true
                        contactListenerViewModel.loadContact()
                        contactListenerViewModel.loadLinked()
                        delay(1000)
                        isRefreshing = false
                    }
                },
                onToggleTop = { contact ->
                    contactActionsViewModel.toggleTop(contact.id, contact.isTop)
                },
                onDeleteContact = { contact ->
                    contactActionsViewModel.delete(contact.id)
                },
                onAcceptLinked = { link -> linkedActionsViewModel.accept(link.id) },
                onDeleteLinked = { link -> linkedActionsViewModel.delete(link.id) }
            )
        }

        // Kişi Ekleme Ekranı
        composable(Routes.ADD_CONTACT) {
            val contactViewModel: ContactListenerViewmodel = hiltViewModel()
            val registrationViewModel: RegistrationViewModel = hiltViewModel()

            var country by remember { mutableStateOf("+90") }
            var phone by remember { mutableStateOf("") }
            var isDropdownExpanded by remember { mutableStateOf(false) }

            // AddContactResult akışını merkezi dinle ve Toast göster
            LaunchedEffect(Unit) {
                contactViewModel.addContactState.collect { state ->
                    when (state) {
                        is com.example.warning.domain.usecase.AddContactResult.Success -> {
                            Toast.makeText(context, "Kişi eklendi", Toast.LENGTH_SHORT).show()
                        }
                        is com.example.warning.domain.usecase.AddContactResult.Error -> {
                            Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                        }
                        is com.example.warning.domain.usecase.AddContactResult.NotFound -> {
                            Toast.makeText(context, "Kullanıcı bulunamadı", Toast.LENGTH_SHORT).show()
                        }
                        else -> {}
                    }
                }
            }

            AddContactScreen(
                state = AddContactUiState(
                    country = country,
                    phone = phone,
                    isDropdownExpanded = isDropdownExpanded
                ),
                onBack = { navController.popBackStack() },
                onNavigateHome = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                },
                onCountryDropdownToggle = {
                    isDropdownExpanded = !isDropdownExpanded
                },
                onCountrySelected = { selected ->
                    country = selected
                    isDropdownExpanded = false
                },
                onPhoneChange = { newPhone -> phone = newPhone },
                onSubmitClick = {
                    scope.launch {
                        try {
                            val exists = registrationViewModel.checkingUser(phone).await()
                            if (!exists) {
                                Toast.makeText(context, "Kullanıcı bulunamadı", Toast.LENGTH_SHORT).show()
                            } else {
                                contactViewModel.addContact(phone, country)
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                e.message ?: "Hata oluştu",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            )
        }

        // Acil Durum Geçmişi
        composable(Routes.EMERGENCY_HISTORY) {
            EmergencyHistoryScreen(navController = navController)
        }
    }
}