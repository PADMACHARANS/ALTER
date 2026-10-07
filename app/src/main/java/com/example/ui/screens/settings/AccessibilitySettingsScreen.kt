package com.example.ui.screens.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BackgroundTint
import com.example.ui.components.AmbientAtmosphere
import com.example.ui.theme.Amber500
import com.example.ui.theme.Indigo600
import com.example.ui.theme.Teal600
import com.example.ui.viewmodel.ReadEaseViewModel

@Composable
fun AccessibilitySettingsScreen(
    viewModel: ReadEaseViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val preferences by viewModel.accessibilityPreferences.collectAsState()

    AmbientAtmosphere(tint = preferences.backgroundTint) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier.testTag("settings_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "Accessibility & Comfort",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Customize reading rulers, typography, tints, and speech",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 36.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 1: Reading Ruler & Focus Guide
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Straighten, contentDescription = null, tint = Amber500)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("Reading Ruler Overlay", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Text("Draggable tinted horizontal guide", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Switch(
                                    checked = preferences.readingRulerEnabled,
                                    onCheckedChange = { isChecked ->
                                        viewModel.updatePreferences(preferences.copy(readingRulerEnabled = isChecked))
                                    },
                                    modifier = Modifier.testTag("settings_ruler_switch")
                                )
                            }

                            if (preferences.readingRulerEnabled) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Text("Ruler Band Height: ${preferences.rulerHeightDp} dp", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                Slider(
                                    value = preferences.rulerHeightDp.toFloat(),
                                    onValueChange = {
                                        viewModel.updatePreferences(preferences.copy(rulerHeightDp = it.toInt()))
                                    },
                                    valueRange = 30f..84f
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Ruler Color Tint:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(6.dp))
                                val rulerColors = listOf(
                                    Pair("Soft Yellow", "#FEF08A"),
                                    Pair("Calm Cyan", "#BAE6FD"),
                                    Pair("Gentle Mint", "#BBF7D0"),
                                    Pair("Soft Rose", "#FECDD3")
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    rulerColors.forEach { (name, hex) ->
                                        val isSelected = preferences.rulerColorHex == hex
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = {
                                                viewModel.updatePreferences(preferences.copy(rulerColorHex = hex))
                                            },
                                            label = { Text(name) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Section 2: Scotopic Sensitivity & Background Tints
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Palette, contentDescription = null, tint = Indigo600)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Color Tints (Scotopic Relief)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Softer background tints reduce visual glare and letter jumping on white pages.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(BackgroundTint.values()) { tint ->
                                    val isSelected = preferences.backgroundTint == tint
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.setBackgroundTint(tint) },
                                        label = { Text(tint.displayName) },
                                        leadingIcon = {
                                            Box(
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (tint == BackgroundTint.NONE) Color.LightGray else Color(tint.hexColor)
                                                    )
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Section 3: Typography, Line Spacing & Bionic Reading
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FormatSize, contentDescription = null, tint = Teal600)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Typography & Line Spacing", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text("Font Size: ${(preferences.fontScale * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            Slider(
                                value = preferences.fontScale,
                                onValueChange = { viewModel.updatePreferences(preferences.copy(fontScale = it)) },
                                valueRange = 0.8f..1.6f
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text("Letter Spacing: ${(preferences.letterSpacingSp * 10).toInt() / 10f} sp", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            Slider(
                                value = preferences.letterSpacingSp,
                                onValueChange = { viewModel.updatePreferences(preferences.copy(letterSpacingSp = it)) },
                                valueRange = 0.1f..3.0f
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text("Line Spacing: ${(preferences.lineSpacingMultiplier * 10).toInt() / 10f}x", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            Slider(
                                value = preferences.lineSpacingMultiplier,
                                onValueChange = { viewModel.updatePreferences(preferences.copy(lineSpacingMultiplier = it)) },
                                valueRange = 1.2f..2.2f
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Bionic Reading Highlights", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text("Bolds initial word letters to guide eye jumps", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Switch(
                                    checked = preferences.bionicReadingEnabled,
                                    onCheckedChange = {
                                        viewModel.updatePreferences(preferences.copy(bionicReadingEnabled = it))
                                    }
                                )
                            }
                        }
                    }
                }

                // Section 4: Voice & Speech Engine
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = Indigo600)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Speech & Audio Pacing", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text("Speech Rate (Speed): ${(preferences.speechRate * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            Slider(
                                value = preferences.speechRate,
                                onValueChange = { viewModel.updatePreferences(preferences.copy(speechRate = it)) },
                                valueRange = 0.5f..1.5f
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text("Speech Pitch: ${(preferences.speechPitch * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            Slider(
                                value = preferences.speechPitch,
                                onValueChange = { viewModel.updatePreferences(preferences.copy(speechPitch = it)) },
                                valueRange = 0.8f..1.4f
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    viewModel.soundManager.speak(
                                        "Hello! This is your customized natural speech pace on ReadEase.",
                                        rate = preferences.speechRate,
                                        pitch = preferences.speechPitch
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Test Natural Speech Voice", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Section 4: Account & Session
                item {
                    val firebaseUser = com.google.firebase.Firebase.auth.currentUser
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "Account & Authentication",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            if (firebaseUser != null) {
                                Text(
                                    text = "Signed in as: ${firebaseUser.displayName ?: firebaseUser.email ?: "Learner"}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Firebase UID: ${firebaseUser.uid}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            } else {
                                Text(
                                    text = "Status: Guest Mode / Local Profile",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            OutlinedButton(
                                onClick = {
                                    viewModel.soundManager.playToneClick()
                                    viewModel.signOut()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("settings_sign_out_btn"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Sign Out of Account")
                            }
                        }
                    }
                }
            }
        }
    }
}
