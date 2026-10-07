package com.example.ui.screens.auth

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AmbientAtmosphere
import com.example.ui.theme.CoralRed
import com.example.ui.theme.Indigo600
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.Teal600
import com.example.ui.viewmodel.DailyQuizViewModel
import com.example.ui.viewmodel.ReadEaseViewModel
import com.example.ui.viewmodel.ScreenRoute
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import android.util.Log
import kotlinx.coroutines.launch

enum class AuthMode {
    MAIN_CHOICE,
    EMAIL_LOGIN,
    CREATE_ACCOUNT
}

@Composable
fun AuthScreen(
    viewModel: ReadEaseViewModel,
    dailyQuizViewModel: DailyQuizViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        // Stay on login if top level
    }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val preferences by viewModel.accessibilityPreferences.collectAsState()
    val profile by viewModel.userProfile.collectAsState()

    var currentAuthMode by remember { mutableStateOf(AuthMode.MAIN_CHOICE) }

    // Input States
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf(profile.name) }
    var selectedEmoji by remember { mutableStateOf(profile.avatarEmoji) }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Error State
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var infoMessage by remember { mutableStateOf<String?>(null) }

    // Dialog States
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var forgotPasswordEmail by remember { mutableStateOf("") }
    var forgotPasswordSuccessMessage by remember { mutableStateOf<String?>(null) }

    var showPrivacyPolicyDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showSupportDialog by remember { mutableStateOf(false) }

    val avatarOptions = listOf("🌟", "🦊", "🦉", "🚀", "🐬", "🎨", "🦁", "🌈")

    AmbientAtmosphere(tint = preferences.backgroundTint) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(20.dp))

                // Top Accessibility Shortcut
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = { viewModel.navigateTo(ScreenRoute.ACCESSIBILITY_SETTINGS) },
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("auth_accessibility_settings_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Accessibility,
                            contentDescription = "Accessibility Comfort Settings",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Accessibility", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            // 1. App Branding Header
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // ALTER Geometric Logo Badge
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(Indigo600),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = "ALTER Logo",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "ALTER",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "Inclusive Learning Studio",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Indigo600
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Learn at your own pace with accessible reading, communication and sign-language tools.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }

            // Error Message Banner (if any)
            if (errorMessage != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_error_banner"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CoralRed.copy(alpha = 0.12f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = "Error alert",
                                tint = CoralRed,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = errorMessage!!,
                                style = MaterialTheme.typography.bodyMedium,
                                color = CoralRed,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { errorMessage = null },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss error",
                                    tint = CoralRed,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Info Message Banner (if any)
            if (infoMessage != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = LeafGreen.copy(alpha = 0.12f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Success",
                                tint = LeafGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = infoMessage!!,
                                style = MaterialTheme.typography.bodyMedium,
                                color = LeafGreen,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // 2. Authentication Methods & Modes
            when (currentAuthMode) {
                AuthMode.MAIN_CHOICE -> {
                    // Continue with Google (Primary Button)
                    item {
                        Button(
                            onClick = {
                                errorMessage = null
                                coroutineScope.launch {
                                    try {
                                        val credentialManager = CredentialManager.create(context)
                                        val webClientId = context.getString(com.example.R.string.default_web_client_id)
                                        
                                        val googleIdOption = GetSignInWithGoogleOption.Builder(webClientId)
                                            .build()
                                        
                                        val request = GetCredentialRequest.Builder()
                                            .addCredentialOption(googleIdOption)
                                            .build()
                                        
                                        val result = credentialManager.getCredential(
                                            context = context,
                                            request = request
                                        )
                                        val credential = result.credential
                                        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                            val idToken = googleIdTokenCredential.idToken
                                            
                                            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                                            com.google.firebase.Firebase.auth.signInWithCredential(firebaseCredential)
                                                .addOnSuccessListener { authResult ->
                                                    val firebaseUser = authResult.user
                                                    if (firebaseUser != null) {
                                                        viewModel.updateUserProfile(
                                                            name = firebaseUser.displayName ?: "Learner",
                                                            emoji = profile.avatarEmoji
                                                        )
                                                        dailyQuizViewModel.resetReadingStreakUponLogin(forceReset = false)
                                                        viewModel.startNewShuffledScreener()
                                                        viewModel.evaluateRouting()
                                                    }
                                                }
                                                .addOnFailureListener { e ->
                                                    Log.e("AuthScreen", "Firebase Auth sign-in failed", e)
                                                    errorMessage = "Authentication failed: ${e.localizedMessage ?: e.toString()}"
                                                }
                                        } else {
                                            errorMessage = "Unexpected credential type returned."
                                        }
                                    } catch (e: GetCredentialCancellationException) {
                                        // User dismissed or cancelled the Google credential picker
                                        Log.d("AuthScreen", "Google Sign-In prompt dismissed by user.")
                                        errorMessage = null
                                    } catch (e: GetCredentialException) {
                                        Log.w("AuthScreen", "Credential Manager sign-in failed: ${e.message}")
                                        errorMessage = "Unable to complete Google Sign-In (${e.message ?: "Please try again"})."
                                    } catch (e: Exception) {
                                        Log.e("AuthScreen", "Google sign-in exception", e)
                                        errorMessage = "Google sign-in failed: ${e.localizedMessage ?: e.toString()}"
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("continue_with_google_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "G",
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Indigo600,
                                        fontSize = 15.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Continue with Google",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // Continue with Email
                    item {
                        OutlinedButton(
                            onClick = {
                                errorMessage = null
                                currentAuthMode = AuthMode.EMAIL_LOGIN
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("continue_with_email_button"),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Continue with Email",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // Forgot Password Link
                    item {
                        TextButton(
                            onClick = {
                                errorMessage = null
                                forgotPasswordSuccessMessage = null
                                showForgotPasswordDialog = true
                            },
                            modifier = Modifier.testTag("forgot_password_button")
                        ) {
                            Text(
                                text = "Forgot Password?",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Indigo600
                            )
                        }
                    }

                    // Divider: OR
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(modifier = Modifier.weight(1f))
                            Text(
                                text = "  OR  ",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                            HorizontalDivider(modifier = Modifier.weight(1f))
                        }
                    }

                    // Create Account Button
                    item {
                        OutlinedButton(
                            onClick = {
                                errorMessage = null
                                currentAuthMode = AuthMode.CREATE_ACCOUNT
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("create_account_button"),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "Create Account",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Indigo600
                            )
                        }
                    }

                    // Continue as Guest (Recommended Play Store & Offline Safe)
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    errorMessage = null
                                    dailyQuizViewModel.resetReadingStreakUponLogin(forceReset = false)
                                    viewModel.startNewShuffledScreener()
                                    viewModel.continueAsGuest()
                                }
                                .testTag("continue_as_guest_card"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Continue as Guest",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "No account required. Progress stays on this device.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                AuthMode.EMAIL_LOGIN -> {
                    // Email & Password Fields
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            ) {
                                Text(
                                    text = "Sign in with Email",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                OutlinedTextField(
                                    value = emailInput,
                                    onValueChange = {
                                        emailInput = it
                                        errorMessage = null
                                    },
                                    singleLine = true,
                                    label = { Text("Email address") },
                                    placeholder = { Text("name@example.com") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Email, contentDescription = "Email")
                                    },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Email,
                                        imeAction = ImeAction.Next
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("auth_email_input"),
                                    shape = RoundedCornerShape(14.dp)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = passwordInput,
                                    onValueChange = {
                                        passwordInput = it
                                        errorMessage = null
                                    },
                                    singleLine = true,
                                    label = { Text("Password") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Lock, contentDescription = "Password")
                                    },
                                    trailingIcon = {
                                        IconButton(
                                            onClick = { isPasswordVisible = !isPasswordVisible },
                                            modifier = Modifier.testTag("auth_password_visibility_toggle")
                                        ) {
                                            Icon(
                                                imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
                                            )
                                        }
                                    },
                                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Password,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(onDone = {
                                        validateAndLogin(
                                            email = emailInput,
                                            password = passwordInput,
                                            onError = { errorMessage = it },
                                            onSuccess = {
                                                viewModel.updateUserProfile(
                                                    name = emailInput.substringBefore("@").replaceFirstChar { it.uppercase() },
                                                    emoji = profile.avatarEmoji
                                                )
                                                dailyQuizViewModel.resetReadingStreakUponLogin(forceReset = false)
                                                viewModel.startNewShuffledScreener()
                                                viewModel.continueAsGuest()
                                            }
                                        )
                                    }),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("auth_password_input"),
                                    shape = RoundedCornerShape(14.dp)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(
                                        onClick = {
                                            forgotPasswordEmail = emailInput
                                            showForgotPasswordDialog = true
                                        }
                                    ) {
                                        Text("Forgot Password?", color = Indigo600)
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                        onClick = {
                                            validateAndLogin(
                                                email = emailInput,
                                                password = passwordInput,
                                                onError = { errorMessage = it },
                                                onSuccess = {
                                                    viewModel.updateUserProfile(
                                                        name = emailInput.substringBefore("@").replaceFirstChar { it.uppercase() },
                                                        emoji = profile.avatarEmoji
                                                    )
                                                    dailyQuizViewModel.resetReadingStreakUponLogin(forceReset = false)
                                                    viewModel.startNewShuffledScreener()
                                                    viewModel.continueAsGuest()
                                                }
                                            )
                                        },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("auth_submit_login_button"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
                                ) {
                                    Text("Sign In", fontWeight = FontWeight.Bold, color = Color.White)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedButton(
                                    onClick = {
                                        currentAuthMode = AuthMode.MAIN_CHOICE
                                        errorMessage = null
                                    },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Text("Back to Login Options")
                                }
                            }
                        }
                    }
                }

                AuthMode.CREATE_ACCOUNT -> {
                    // Create Account Form
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            ) {
                                Text(
                                    text = "Create Your ALTER Profile",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                OutlinedTextField(
                                    value = nameInput,
                                    onValueChange = {
                                        nameInput = it
                                        errorMessage = null
                                    },
                                    singleLine = true,
                                    label = { Text("Learner Name") },
                                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name") },
                                    modifier = Modifier.fillMaxWidth().testTag("create_account_name_input"),
                                    shape = RoundedCornerShape(14.dp)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = emailInput,
                                    onValueChange = {
                                        emailInput = it
                                        errorMessage = null
                                    },
                                    singleLine = true,
                                    label = { Text("Email address") },
                                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    modifier = Modifier.fillMaxWidth().testTag("create_account_email_input"),
                                    shape = RoundedCornerShape(14.dp)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = passwordInput,
                                    onValueChange = {
                                        passwordInput = it
                                        errorMessage = null
                                    },
                                    singleLine = true,
                                    label = { Text("Create Password (min 6 characters)") },
                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
                                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    trailingIcon = {
                                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                            Icon(
                                                imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
                                            )
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().testTag("create_account_password_input"),
                                    shape = RoundedCornerShape(14.dp)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Choose your friendly avatar",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(avatarOptions) { emoji ->
                                        val isSelected = emoji == selectedEmoji
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isSelected) Indigo600.copy(alpha = 0.2f)
                                                    else MaterialTheme.colorScheme.surfaceVariant
                                                )
                                                .border(
                                                    width = if (isSelected) 2.dp else 1.dp,
                                                    color = if (isSelected) Indigo600 else Color.Transparent,
                                                    shape = CircleShape
                                                )
                                                .clickable { selectedEmoji = emoji },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = emoji, fontSize = 22.sp)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                Button(
                                    onClick = {
                                        if (nameInput.isBlank()) {
                                            errorMessage = "Please enter your learner name."
                                            return@Button
                                        }
                                        if (emailInput.isBlank() || !emailInput.contains("@") || !emailInput.contains(".")) {
                                            errorMessage = "Please enter a valid email address."
                                            return@Button
                                        }
                                        if (passwordInput.length < 6) {
                                            errorMessage = "Password must be at least 6 characters."
                                            return@Button
                                        }

                                        viewModel.updateUserProfile(
                                            name = nameInput.trim(),
                                            emoji = selectedEmoji
                                        )
                                        dailyQuizViewModel.resetReadingStreakUponLogin(forceReset = false)
                                        viewModel.startNewShuffledScreener()
                                        viewModel.continueAsGuest()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("create_account_submit_button"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
                                ) {
                                    Text("Create Account & Start Learning", fontWeight = FontWeight.Bold, color = Color.White)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedButton(
                                    onClick = {
                                        currentAuthMode = AuthMode.MAIN_CHOICE
                                        errorMessage = null
                                    },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Text("Back to Login Options")
                                }
                            }
                        }
                    }
                }
            }

            // 5. Privacy & Legal Footer
            item {
                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "By continuing, you agree to our",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Guidelines",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Indigo600,
                            modifier = Modifier
                                .clickable { viewModel.navigateTo(ScreenRoute.GUIDELINES) }
                                .padding(vertical = 4.dp, horizontal = 4.dp)
                                .testTag("auth_guidelines_link")
                        )
                        Text(text = " • ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Text(
                            text = "Privacy Policy",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Indigo600,
                            modifier = Modifier
                                .clickable { showPrivacyPolicyDialog = true }
                                .padding(vertical = 4.dp, horizontal = 4.dp)
                        )
                        Text(text = " • ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Text(
                            text = "Terms of Service",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Indigo600,
                            modifier = Modifier
                                .clickable { showTermsDialog = true }
                                .padding(vertical = 4.dp, horizontal = 4.dp)
                        )
                        Text(text = " • ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Text(
                            text = "Contact / Support",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Indigo600,
                            modifier = Modifier
                                .clickable { showSupportDialog = true }
                                .padding(vertical = 4.dp, horizontal = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    // Forgot Password Dialog
    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = {
                Text("Password Recovery", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        text = "Enter your account email. We will send you secure instructions to reset your password.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = forgotPasswordEmail,
                        onValueChange = { forgotPasswordEmail = it },
                        singleLine = true,
                        label = { Text("Email Address") },
                        placeholder = { Text("name@example.com") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("forgot_password_email_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (forgotPasswordSuccessMessage != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = forgotPasswordSuccessMessage!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = LeafGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (forgotPasswordEmail.isBlank() || !forgotPasswordEmail.contains("@")) {
                            forgotPasswordSuccessMessage = "Please enter a valid email address."
                        } else {
                            forgotPasswordSuccessMessage = "Password reset instructions sent to $forgotPasswordEmail"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                    modifier = Modifier.testTag("send_password_reset_btn")
                ) {
                    Text("Send Reset Link", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPasswordDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Privacy Policy Dialog
    if (showPrivacyPolicyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyPolicyDialog = false },
            title = { Text("Privacy Policy", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "ALTER respects learner privacy. All personal learning telemetry, screener assessments, speech data, and reading ruler customizations remain locally on your device or in your secure account. We never sell personal information or show commercial ads.",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showPrivacyPolicyDialog = false }) {
                    Text("Got It")
                }
            }
        )
    }

    // Terms of Service Dialog
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = { Text("Terms of Service", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "By using ALTER: Inclusive Learning Studio, you agree to utilize the assistive reading tools, phonics mnemonics, sign-language references, and speech synthesis features respectfully for personal learning and educational growth.",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showTermsDialog = false }) {
                    Text("I Agree")
                }
            }
        )
    }

    // Contact / Support Dialog
    if (showSupportDialog) {
        AlertDialog(
            onDismissRequest = { showSupportDialog = false },
            title = { Text("Contact & Support", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Need help or have accessibility feedback?\n\n• Email: support@alterlearning.org\n• Response time: Within 24-48 hours\n• Community: Accessible Learning Alliance",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showSupportDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

/**
 * Validates email/password credentials with friendly educational error feedback.
 */
private fun validateAndLogin(
    email: String,
    password: String,
    onError: (String) -> Unit,
    onSuccess: () -> Unit
) {
    val trimmedEmail = email.trim()
    if (trimmedEmail.isEmpty()) {
        onError("Please enter your email address.")
        return
    }
    if (!trimmedEmail.contains("@") || !trimmedEmail.contains(".")) {
        onError("Please enter a valid email address.")
        return
    }
    if (password.isEmpty()) {
        onError("Please enter your password.")
        return
    }
    if (password.length < 6) {
        onError("Incorrect email or password.")
        return
    }

    // Successful authentication
    onSuccess()
}
