package com.hakankuru.yanimda.presentation.ui.screens.register

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.hakankuru.yanimda.domain.model.Profile
import com.hakankuru.yanimda.presentation.ui.navigation.Routes
import com.hakankuru.yanimda.presentation.viewModel.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SignUpScreen(
    navController: NavHostController,
    verificationViewModel: VerificationViewModel = hiltViewModel(),
    registrationViewModel: RegistrationViewModel = hiltViewModel(),
    userview: ProfileListenerViewModel = hiltViewModel(),
    contactview: ContactListenerViewmodel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // State
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf("+90") }
    var countryExpanded by remember { mutableStateOf(false) }
    var locationPermission by remember { mutableStateOf(checkLocationPermission()) }
    var contactPermission by remember { mutableStateOf(false) }
    var verificationStep by remember { mutableStateOf(VerificationStep.EnterPhone) }
    var codeInput by remember { mutableStateOf("") }

    val isLoading by remember { derivedStateOf { verificationViewModel.isLoading } }
    val isVerified by remember { derivedStateOf { verificationViewModel.isVerified } }
    val state by registrationViewModel.state.collectAsState()

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        locationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        if (!locationPermission) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Konum izni verilmedi")
            }
        }
    }

    LaunchedEffect(state) {
        if (state is UserRegistrationState.RegistrationSuccess) {
            navController.navigate(Routes.MAIN) {
                launchSingleTop = true
                popUpTo(0) { inclusive = true }
            }
        }
    }

    LaunchedEffect(isVerified) {
        if (isVerified == true) {
            verificationStep = VerificationStep.Verified
        }
    }

    // Animated gradient background
    val infiniteTransition = rememberInfiniteTransition(label = "gradient")
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient offset"
    )

    val animatedBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.10f),
            MaterialTheme.colorScheme.background,
        ),
        start = Offset(gradientOffset, gradientOffset),
        end = Offset(gradientOffset + 600f, gradientOffset + 600f)
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(animatedBrush)
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // Hero Section
                PremiumSignUpHero()

                Spacer(modifier = Modifier.height(32.dp))

                // Main Content Card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    shadowElevation = 16.dp
                ) {
                    AnimatedContent(
                        targetState = verificationStep,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)) with
                                    fadeOut(animationSpec = tween(300))
                        },
                        label = "verification step"
                    ) { step ->
                        when (step) {
                            VerificationStep.EnterPhone -> {
                                EnterPhoneContent(
                                    name = name,
                                    onNameChange = { name = it },
                                    phone = phone,
                                    onPhoneChange = { phone = it },
                                    selectedCountry = selectedCountry,
                                    countryExpanded = countryExpanded,
                                    onCountryExpandedChange = { countryExpanded = it },
                                    onCountrySelected = { selectedCountry = it; countryExpanded = false },
                                    locationPermission = locationPermission,
                                    onLocationPermissionChange = {
                                        if (it) {
                                            locationPermissionLauncher.launch(
                                                arrayOf(
                                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                                )
                                            )
                                        } else {
                                            locationPermission = false
                                        }
                                    },
                                    contactPermission = contactPermission,
                                    onContactPermissionChange = { contactPermission = it },
                                    isLoading = isLoading,
                                    onSubmit = {
                                        when {
                                            name.isBlank() -> coroutineScope.launch {
                                                snackbarHostState.showSnackbar("İsim boş bırakılamaz")
                                            }
                                            phone.length != 10 -> coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Telefon 10 hane olmalı")
                                            }
                                            else -> {
                                                val fullNumber = selectedCountry + phone
                                                coroutineScope.launch {
                                                    try {
                                                        val exists = registrationViewModel.checkingUser(phone).await()
                                                        if (exists) {
                                                            snackbarHostState.showSnackbar("Bu telefon zaten kayıtlı")
                                                        } else {
                                                            if (activity != null) {
                                                                verificationViewModel.sendVerificationCode(
                                                                    phoneNumber = fullNumber,
                                                                    activity = activity,
                                                                    onSuccess = { verificationStep = VerificationStep.EnterCode },
                                                                    onFailure = {
                                                                        coroutineScope.launch {
                                                                            snackbarHostState.showSnackbar("Kod gönderilemedi")
                                                                        }
                                                                    }
                                                                )
                                                            }
                                                        }
                                                    } catch (e: Exception) {
                                                        snackbarHostState.showSnackbar("Bir hata oluştu")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                )
                            }

                            VerificationStep.EnterCode -> {
                                EnterCodeContent(
                                    codeInput = codeInput,
                                    onCodeChange = { codeInput = it },
                                    onVerify = {
                                        verificationViewModel.verifyCode(codeInput)
                                    },
                                    onResend = {
                                        val fullNumber = selectedCountry + phone
                                        if (activity != null) {
                                            verificationViewModel.sendVerificationCode(
                                                phoneNumber = fullNumber,
                                                activity = activity,
                                                onSuccess = {
                                                    coroutineScope.launch {
                                                        snackbarHostState.showSnackbar("Kod tekrar gönderildi")
                                                    }
                                                },
                                                onFailure = {
                                                    coroutineScope.launch {
                                                        snackbarHostState.showSnackbar("Tekrar gönderilemedi")
                                                    }
                                                }
                                            )
                                        }
                                    }
                                )
                            }

                            VerificationStep.Verified -> {
                                VerifiedContent()
                                LaunchedEffect(Unit) {
                                    registrationViewModel.registerUser(
                                        profile = Profile(
                                            id = null,
                                            phoneNumber = phone,
                                            country = selectedCountry,
                                            profilePhoto = "",
                                            name = name,
                                            emergencyMessage = null,
                                            locationPermission = locationPermission,
                                            fcmToken = null
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sign In Link
                PremiumSignInLink(
                    onClick = {
                        navController.navigate(Routes.SIGN_IN) {
                            launchSingleTop = true
                            popUpTo(Routes.SIGN_UP) { inclusive = true }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun PremiumSignUpHero() {
    val infiniteTransition = rememberInfiniteTransition(label = "hero rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier.size(80.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .graphicsLayer { rotationZ = rotation }
                    .background(
                        Brush.sweepGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary,
                                MaterialTheme.colorScheme.primary,
                            )
                        ),
                        CircleShape
                    )
            )

            Surface(
                modifier = Modifier.size(72.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                tonalElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.PersonAdd,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }

        Text(
            text = "Hesap Oluştur",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Acil durum sistemi için kayıt olun",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EnterPhoneContent(
    name: String,
    onNameChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    selectedCountry: String,
    countryExpanded: Boolean,
    onCountryExpandedChange: (Boolean) -> Unit,
    onCountrySelected: (String) -> Unit,
    locationPermission: Boolean,
    onLocationPermissionChange: (Boolean) -> Unit,
    contactPermission: Boolean,
    onContactPermissionChange: (Boolean) -> Unit,
    isLoading: Boolean,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier.padding(
            PaddingValues(
                start = 28.dp,
                end = 28.dp,
                top = 32.dp,
                bottom = 28.dp
            )
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Bilgileriniz",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PremiumNameInput(name = name, onNameChange = onNameChange)

            PremiumCountryDropdown(
                selectedCode = selectedCountry,
                expanded = countryExpanded,
                onExpandedChange = onCountryExpandedChange,
                onCountrySelected = onCountrySelected
            )

            PremiumPhoneInput(phone = phone, onPhoneChange = onPhoneChange)
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        Text(
            text = "İzinler",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PremiumPermissionRow(
                icon = Icons.Default.LocationOn,
                title = "Konum İzni",
                subtitle = "Acil durumda konumunuzu paylaşın",
                checked = locationPermission,
                onCheckedChange = onLocationPermissionChange
            )

            PremiumPermissionRow(
                icon = Icons.Default.Contacts,
                title = "Rehber Erişimi",
                subtitle = "Kişilerinizi senkronize edin",
                checked = contactPermission,
                onCheckedChange = onContactPermissionChange
            )

            Spacer(modifier = Modifier.height(8.dp))

            PremiumButton(
                text = "Devam Et",
                icon = Icons.Default.ArrowForward,
                onClick = onSubmit,
                enabled = !isLoading && name.isNotBlank() && phone.length == 10,
                isLoading = isLoading
            )
        }
    }
}

@Composable
private fun PremiumNameInput(name: String, onNameChange: (String) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(8.dp)
                )
            }

            TextField(
                value = name,
                onValueChange = onNameChange,
                modifier = Modifier.weight(1f),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                placeholder = { Text("Adınız Soyadınız") },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PremiumCountryDropdown(
    selectedCode: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onCountrySelected: (String) -> Unit
) {
    val countries = listOf(
        "+90" to "🇹🇷 Türkiye",
        "+1" to "🇺🇸 Amerika"
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { onExpandedChange(!expanded) }
    ) {
        Surface(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ) {
                        Icon(
                            Icons.Default.Flag,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Text(
                        text = countries.find { it.first == selectedCode }?.second ?: selectedCode,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                    )
                }
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            countries.forEach { (code, name) ->
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = { onCountrySelected(code) },
                    leadingIcon = {
                        if (code == selectedCode) {
                            Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun PremiumPhoneInput(phone: String, onPhoneChange: (String) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            ) {
                Icon(
                    Icons.Default.Phone,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(8.dp)
                )
            }

            TextField(
                value = phone,
                onValueChange = { input ->
                    val filtered = input.filter { it.isDigit() }
                    if (filtered.length <= 10) {
                        onPhoneChange(filtered)
                    }
                },
                modifier = Modifier.weight(1f),
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                placeholder = { Text("5XX XXX XX XX") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )

            AnimatedVisibility(
                visible = phone.isNotEmpty(),
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                Text(
                    text = "${phone.length}/10",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PremiumPermissionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = if (checked) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = if (checked) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
private fun EnterCodeContent(
    codeInput: String,
    onCodeChange: (String) -> Unit,
    onVerify: () -> Unit,
    onResend: () -> Unit
) {
    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.tertiaryContainer,
                tonalElevation = 4.dp
            ) {
                Icon(
                    Icons.Default.MarkEmailRead,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier
                        .padding(16.dp)
                        .size(32.dp)
                )
            }
        }

        Text(
            text = "Kod Gönderildi",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Telefonunuza gelen 6 haneli kodu girin",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        PremiumCodeInput(code = codeInput, onCodeChange = onCodeChange)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PremiumButton(
                text = "Doğrula",
                icon = Icons.Default.CheckCircle,
                onClick = onVerify,
                enabled = codeInput.length == 6,
                modifier = Modifier.weight(1f)
            )
        }

        PremiumTextButton(
            text = "Tekrar Gönder",
            icon = Icons.Default.Refresh,
            onClick = onResend
        )
    }
}

@Composable
private fun PremiumCodeInput(code: String, onCodeChange: (String) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
            ) {
                Icon(
                    Icons.Default.Password,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(8.dp)
                )
            }

            TextField(
                value = code,
                onValueChange = { input ->
                    val filtered = input.filter { it.isDigit() }
                    if (filtered.length <= 6) {
                        onCodeChange(filtered)
                    }
                },
                modifier = Modifier.weight(1f),
                textStyle = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = TextUnit(8f, TextUnitType.Sp)
                ),
                placeholder = { Text("● ● ● ● ● ●") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )
        }
    }
}

@Composable
private fun VerifiedContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.tertiaryContainer,
            tonalElevation = 4.dp
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier
                    .padding(24.dp)
                    .size(48.dp)
            )
        }

        Text(
            text = "Kayıt Başarılı!",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Hesabınız oluşturuluyor...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        CircularProgressIndicator(
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
private fun PremiumSignInLink(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Zaten hesabınız var mı? ",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = "Giriş Yap",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickableWithRipple(onClick = onClick)
                .padding(4.dp)
        )
    }
}

@Composable
private fun PremiumButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "button_scale"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale)
            .clickable(
                enabled = enabled && !isLoading,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        color = if (enabled && !isLoading) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = if (enabled && !isLoading) 8.dp else 0.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = if (enabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    strokeWidth = 3.dp
                )
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = if (enabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (enabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun PremiumTextButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Composable
private fun GlassCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(24.dp),
    shadowElevation: Dp = 8.dp,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = Color.Transparent, // Tamamen şeffaf taban
        tonalElevation = 0.dp,
        shadowElevation = 0.dp // Etrafa gölge yayılmasını önlemek için 0
    ) {
        Box(
            modifier = Modifier
                .clip(shape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.25f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                    shape = shape
                )
        ) {
            content()
        }
    }
}

@Composable
private fun Modifier.clickableWithRipple(onClick: () -> Unit): Modifier {
    return this.pointerInput(Unit) {
        detectTapGestures(onTap = { onClick() })
    }
}

@Preview(showBackground = true, name = "Kayıt Ekranı - Bilgi Girişi")
@Composable
fun SignUpScreenPreview() {
    MaterialTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            // Stateless olan içeriği preview ediyoruz
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PremiumSignUpHero()
                Spacer(modifier = Modifier.height(24.dp))

                // GlassCard ve içeriği manuel olarak simüle ediyoruz
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    shadowElevation = 8.dp
                ) {
                    EnterPhoneContent(
                        name = "Hakan Kuru",
                        onNameChange = {},
                        phone = "5551234567",
                        onPhoneChange = {},
                        selectedCountry = "+90",
                        countryExpanded = false,
                        onCountryExpandedChange = {},
                        onCountrySelected = {},
                        locationPermission = true,
                        onLocationPermissionChange = {},
                        contactPermission = false,
                        onContactPermissionChange = {},
                        isLoading = false,
                        onSubmit = {}
                    )
                }
            }
        }
    }
}
