package com.example.ui.screens.auth

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AmbientAtmosphere
import com.example.ui.components.AppBottomNavigation
import com.example.ui.theme.CoralRed
import com.example.ui.theme.Indigo600
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.Teal600
import com.example.ui.viewmodel.ReadEaseViewModel
import com.example.ui.viewmodel.ScreenRoute

@Composable
fun ProfileSetupScreen(
    viewModel: ReadEaseViewModel,
    modifier: Modifier = Modifier
) {
    val preferences by viewModel.accessibilityPreferences.collectAsState()
    val profile by viewModel.userProfile.collectAsState()

    BackHandler {
        if (profile.onboardingCompleted) {
            viewModel.navigateBack()
        }
    }

    var fullNameInput by remember(profile.fullName) { mutableStateOf(profile.fullName.ifBlank { profile.name }) }
    var displayNameInput by remember(profile.name) { mutableStateOf(profile.name) }
    var selectedLanguage by remember(profile.preferredLanguage) { mutableStateOf(profile.preferredLanguage) }
    var selectedLevel by remember(profile.learningLevel) { mutableStateOf(profile.learningLevel) }
    var selectedEmoji by remember(profile.avatarEmoji) { mutableStateOf(profile.avatarEmoji) }

    val languages = listOf("English", "Spanish", "French", "German", "Signed English")
    val levels = listOf("Beginner", "Intermediate", "Advanced")
    val emojis = listOf("🦊", "🌟", "🦉", "🚀", "🐬", "🎨", "🦁", "🌈")

    var nameError by remember { mutableStateOf<String?>(null) }

    AmbientAtmosphere(tint = preferences.backgroundTint) {
        Box(modifier = modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 24.dp, bottom = if (profile.onboardingCompleted) 85.dp else 32.dp)
            ) {
            // Header Title
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Complete Your Profile",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Let's personalize your learning experience.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    IconButton(
                        onClick = {
                            viewModel.soundManager.speak(
                                "Complete Your Profile. Let's personalize your learning experience. Please fill in your name, choose your language, select your sign language level, and choose a friendly avatar."
                            )
                        },
                        modifier = Modifier.testTag("speak_onboarding_intro_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Read instructions aloud",
                            tint = Indigo600
                        )
                    }
                }
            }

            // Full Name Input
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "What should we call you?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = fullNameInput,
                            onValueChange = {
                                fullNameInput = it
                                nameError = null
                            },
                            singleLine = true,
                            label = { Text("Full Name") },
                            placeholder = { Text("e.g. Alex Smith") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("onboarding_full_name"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = displayNameInput,
                            onValueChange = {
                                displayNameInput = it
                                nameError = null
                            },
                            singleLine = true,
                            label = { Text("Display Name / Nickname") },
                            placeholder = { Text("e.g. ExplorerAlex") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("onboarding_display_name"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        if (nameError != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = nameError!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Choose Avatar
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Choose your friendly avatar",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(emojis) { emoji ->
                                val isSelected = emoji == selectedEmoji
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) Indigo600.copy(alpha = 0.16f)
                                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                        )
                                        .border(
                                            width = if (isSelected) 2.5.dp else 1.dp,
                                            color = if (isSelected) Indigo600 else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            selectedEmoji = emoji
                                            viewModel.soundManager.playToneClick()
                                        }
                                        .testTag("avatar_$emoji"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = emoji, fontSize = 26.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Preferred Language
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Language, contentDescription = null, tint = Teal600)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Preferred Language",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(languages) { lang ->
                                val isSelected = lang == selectedLanguage
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(
                                            if (isSelected) Teal600 else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                        )
                                        .clickable {
                                            selectedLanguage = lang
                                            viewModel.soundManager.playToneClick()
                                        }
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                        .testTag("lang_$lang"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = lang,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Learning Level
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Learning Level",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            levels.forEach { lvl ->
                                val isSelected = lvl == selectedLevel
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isSelected) Indigo600.copy(alpha = 0.08f)
                                            else Color.Transparent
                                        )
                                        .clickable {
                                            selectedLevel = lvl
                                            viewModel.soundManager.playToneClick()
                                        }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = {
                                            selectedLevel = lvl
                                            viewModel.soundManager.playToneClick()
                                        },
                                        colors = RadioButtonDefaults.colors(selectedColor = Indigo600)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = lvl,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = when (lvl) {
                                                "Beginner" -> "Starting with fingerspelling A–Z and simple greetings"
                                                "Intermediate" -> "Learning common everyday signs and response tiles"
                                                "Advanced" -> "Combining full sentence signs and fluent communication"
                                                else -> ""
                                            },
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Submit Button
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        if (fullNameInput.isBlank()) {
                            nameError = "Please enter your full name."
                            viewModel.soundManager.playToneClick()
                            return@Button
                        }
                        if (displayNameInput.isBlank()) {
                            nameError = "Please enter a display name."
                            viewModel.soundManager.playToneClick()
                            return@Button
                        }

                        viewModel.completeOnboarding(
                            fullName = fullNameInput.trim(),
                            displayName = displayNameInput.trim(),
                            preferredLanguage = selectedLanguage,
                            learningLevel = selectedLevel
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("onboarding_continue_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
                ) {
                    Text(
                        text = "Continue to Dashboard",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))

                // Sign Out Option if already onboarded
                if (profile.onboardingCompleted) {
                    OutlinedButton(
                        onClick = {
                            viewModel.signOut()
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CoralRed),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("profile_sign_out_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = CoralRed, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sign Out of ReadEase", fontWeight = FontWeight.Bold, color = CoralRed)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        if (profile.onboardingCompleted) {
            AppBottomNavigation(
                currentRoute = ScreenRoute.PROFILE_SETUP,
                onTabSelected = { route -> viewModel.switchTab(route) },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
}
