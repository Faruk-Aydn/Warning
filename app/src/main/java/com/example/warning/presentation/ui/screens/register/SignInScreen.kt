package com.example.warning.presentation.ui.screens.register

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.warning.presentation.viewModel.VerificationStep

data class SignInUiState(
    val expanded: Boolean = false,
    val selectedCountryCode: String = "+90",
    val phoneNumber: String = "",
    val step: VerificationStep = VerificationStep.EnterPhone,
    val smsCode: String = "",
    val errorMessage: String? = null,
    val timeLeft: Int = 120
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun SignInScreen(
    state: SignInUiState,
    onExpandedChange: (Boolean) -> Unit,
    onCountrySelected: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onRequestCodeClick: () -> Unit,
    onSmsCodeChange: (String) -> Unit,
    onVerifyClick: () -> Unit,
    onResendClick: () -> Unit,
    onErrorDismiss: () -> Unit,
    onSignUpClick: () -> Unit
) {
    // Animated gradient background
    val infiniteTransition = rememberInfiniteTransition()
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedBrush)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo/Hero Section
            PremiumSignInHero()

            Spacer(modifier = Modifier.height(32.dp))

            // Content Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                shadowElevation = 16.dp
            ) {
                AnimatedContent(
                    targetState = state.step,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) with
                                fadeOut(animationSpec = tween(300))
                    }
                ) { step ->
                    when (step) {
                        VerificationStep.EnterPhone -> {
                            EnterPhoneContent(
                                state = state,
                                onExpandedChange = onExpandedChange,
                                onCountrySelected = onCountrySelected,
                                onPhoneNumberChange = onPhoneNumberChange,
                                onRequestCodeClick = onRequestCodeClick
                            )
                        }

                        VerificationStep.EnterCode -> {
                            EnterCodeContent(
                                state = state,
                                onSmsCodeChange = onSmsCodeChange,
                                onVerifyClick = onVerifyClick,
                                onResendClick = onResendClick
                            )
                        }

                        VerificationStep.Verified -> {
                            VerifiedContent()
                        }
                    }
                }
            }

            // Error Message
            AnimatedVisibility(
                visible = state.errorMessage != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                state.errorMessage?.let {
                    Spacer(modifier = Modifier.height(16.dp))
                    ErrorCard(message = it)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign Up Link
            PremiumSignUpLink(onClick = onSignUpClick)
        }
    }
}

@Composable
private fun PremiumSignInHero() {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Animated Icon
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
                        Icons.Default.Login,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }

        Text(
            text = "Hoş Geldiniz",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Hesabınıza giriş yapın",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EnterPhoneContent(
    state: SignInUiState,
    onExpandedChange: (Boolean) -> Unit,
    onCountrySelected: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onRequestCodeClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Telefon Numaranız",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "SMS ile doğrulama kodu göndereceğiz",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Country Code Dropdown
        PremiumCountryDropdown(
            selectedCode = state.selectedCountryCode,
            expanded = state.expanded,
            onExpandedChange = onExpandedChange,
            onCountrySelected = onCountrySelected
        )

        // Phone Number Input
        PremiumPhoneInput(
            phone = state.phoneNumber,
            onPhoneChange = onPhoneNumberChange
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Submit Button
        PremiumButton(
            text = "Kod Gönder",
            icon = Icons.Default.Send,
            onClick = onRequestCodeClick,
            enabled = state.phoneNumber.length >= 10
        )
    }
}

@Composable
private fun EnterCodeContent(
    state: SignInUiState,
    onSmsCodeChange: (String) -> Unit,
    onVerifyClick: () -> Unit,
    onResendClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Success Icon
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
                    modifier = Modifier.padding(16.dp).size(32.dp)
                )
            }
        }

        Text(
            text = "Kod Gönderildi",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
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

        // SMS Code Input
        PremiumCodeInput(
            code = state.smsCode,
            onCodeChange = onSmsCodeChange
        )

        // Timer or Resend
        if (state.timeLeft > 0) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Kalan süre: ${state.timeLeft}s",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            PremiumTextButton(
                text = "Tekrar Gönder",
                icon = Icons.Default.Refresh,
                onClick = onResendClick
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        PremiumButton(
            text = "Doğrula",
            icon = Icons.Default.CheckCircle,
            onClick = onVerifyClick,
            enabled = state.smsCode.length == 6
        )
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
                modifier = Modifier.padding(24.dp).size(48.dp)
            )
        }

        Text(
            text = "Başarılı!",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Giriş yapılıyor...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        CircularProgressIndicator(
            modifier = Modifier.size(32.dp),
            strokeWidth = 3.dp
        )
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
    val countryCodes = listOf(
        "+90" to "🇹🇷 Türkiye",
        "+1" to "🇺🇸 Amerika",
        "+44" to "🇬🇧 İngiltere",
        "+49" to "🇩🇪 Almanya"
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
                        text = countryCodes.find { it.first == selectedCode }?.second ?: selectedCode,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            countryCodes.forEach { (code, name) ->
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = {
                        onCountrySelected(code)
                        onExpandedChange(false)
                    },
                    leadingIcon = {
                        if (code == selectedCode) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun PremiumPhoneInput(
    phone: String,
    onPhoneChange: (String) -> Unit
) {
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
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                placeholder = {
                    Text(
                        "5XX XXX XX XX",
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
private fun PremiumCodeInput(
    code: String,
    onCodeChange: (String) -> Unit
) {
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
                    letterSpacing = 8.sp
                ),
                placeholder = {
                    Text(
                        "● ● ● ● ● ●",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
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
private fun PremiumButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale)
            .pointerInput(enabled) {
                if (enabled) {
                    detectTapGestures(
                        onPress = {
                            pressed = true
                            tryAwaitRelease()
                            pressed = false
                        },
                        onTap = { onClick() }
                    )
                }
            },
        shape = RoundedCornerShape(16.dp),
        color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = if (enabled) 8.dp else 0.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = if (enabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
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

@Composable
private fun PremiumTextButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
    ) {
        TextButton(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun PremiumSignUpLink(onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Hesabınız yok mu?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "Kayıt Ol",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun ErrorCard(message: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f),
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
private fun GlassCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape,
    shadowElevation: androidx.compose.ui.unit.Dp,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f),
        tonalElevation = 0.dp,
        shadowElevation = shadowElevation,
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.08f),
                            Color.White.copy(alpha = 0.03f),
                        )
                    )
                )
        ) {
            content()
        }
    }
}