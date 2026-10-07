package com.example.ui.screens.dashboard

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AmbientAtmosphere
import com.example.ui.components.AppBottomNavigation
import com.example.ui.components.ReadingRulerOverlay
import com.example.ui.components.StreakGauge
import com.example.ui.theme.Amber500
import com.example.ui.theme.Indigo600
import com.example.ui.theme.Teal600
import com.example.ui.viewmodel.ReadEaseViewModel
import com.example.ui.viewmodel.ScreenRoute

@Composable
fun DashboardScreen(
    viewModel: ReadEaseViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()
    val preferences by viewModel.accessibilityPreferences.collectAsState()
    val signProgressList by viewModel.allSignProgress.collectAsState()

    val completedSignsCount = signProgressList.count { it.completed }
    val masteredSignsCount = signProgressList.count { it.mastered }

    AmbientAtmosphere(tint = preferences.backgroundTint) {
        Box(modifier = modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(top = 20.dp, bottom = 85.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top Header Row
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Indigo600.copy(alpha = 0.15f))
                                    .clickable { viewModel.navigateTo(ScreenRoute.PROFILE_SETUP) }
                                    .testTag("dashboard_profile_avatar"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = profile.avatarEmoji, fontSize = 26.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Hello, ${profile.name} 👋",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Ready to learn comfortably today?",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { viewModel.navigateTo(ScreenRoute.GUIDELINES) },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .testTag("dashboard_guidelines_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Help,
                                    contentDescription = "ALTER Guidelines",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { viewModel.navigateTo(ScreenRoute.ACCESSIBILITY_SETTINGS) },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .testTag("dashboard_settings_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Accessibility Settings",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Daily Streak & Level Gauge
                item {
                    StreakGauge(profile = profile)
                }

                // Spotlight: Indian Sign Language (ISLRTC Standard)
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.navigateTo(ScreenRoute.SIGN_LANGUAGE) }
                            .testTag("dashboard_isl_spotlight_card"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Indigo600.copy(alpha = 0.08f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Indigo600.copy(alpha = 0.25f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Verified ISL",
                                        tint = Teal600,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "ISLRTC & NCERT Standard",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Teal600
                                    )
                                }

                                Text(
                                    text = "$completedSignsCount Signs Practiced",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Indigo600
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Indian Sign Language (ISL)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Learn authentic hand demonstrations, step-by-step motions, and camera gesture verification for Namaste, Greetings, Emergency, and Everyday signs.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                // Quick Accessibility Banner: Reading Ruler Toggle
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (preferences.readingRulerEnabled) Amber500.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Straighten,
                                    contentDescription = "Ruler",
                                    tint = if (preferences.readingRulerEnabled) Amber500 else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Reading Ruler Overlay",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = if (preferences.readingRulerEnabled) "Active • Drag across text" else "Tap switch to enable line guide",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Switch(
                                checked = preferences.readingRulerEnabled,
                                onCheckedChange = { viewModel.toggleReadingRuler() },
                                modifier = Modifier.testTag("reading_ruler_quick_toggle")
                            )
                        }
                    }
                }

                // Section Title: Inclusive Tools
                item {
                    Text(
                        text = "Assistive Tools & Learning",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Feature Cards
                item {
                    DashboardFeatureCard(
                        title = "Dyslexia Reader",
                        subtitle = "OpenDyslexic typography, bionic reading, text tints, and text-to-speech.",
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        accentColor = Indigo600,
                        tag = "tool_dyslexia_reader",
                        onClick = { viewModel.navigateTo(ScreenRoute.DYSLEXIA_READER) }
                    )
                }

                item {
                    DashboardFeatureCard(
                        title = "Daily Literacy Quiz",
                        subtitle = "Track real-time accuracy percentages, test phonics rules, and manage your reading streak.",
                        icon = Icons.Default.AutoAwesome,
                        accentColor = Color(0xFF8B5CF6),
                        tag = "tool_daily_quiz",
                        onClick = { viewModel.navigateTo(ScreenRoute.DAILY_QUIZ) }
                    )
                }

                item {
                    DashboardFeatureCard(
                        title = "Phonics & Letter Mnemonics",
                        subtitle = "Master 'b' vs 'd', 'p' vs 'q' with visual hand and bed memory rules.",
                        icon = Icons.Default.FormatSize,
                        accentColor = Teal600,
                        tag = "tool_phonics_mnemonics",
                        onClick = { viewModel.navigateTo(ScreenRoute.PHONICS_MNEMONICS) }
                    )
                }

                item {
                    DashboardFeatureCard(
                        title = "Text Simplifier",
                        subtitle = "Break long complex paragraphs into plain language and clear syllable chunks.",
                        icon = Icons.Default.AutoAwesome,
                        accentColor = Amber500,
                        tag = "tool_text_simplifier",
                        onClick = { viewModel.navigateTo(ScreenRoute.TEXT_SIMPLIFIER) }
                    )
                }

                item {
                    DashboardFeatureCard(
                        title = "Practice Hub",
                        subtitle = "Interactive Word Builder puzzles, Sound Steps blending, and Read-Along stories.",
                        icon = Icons.Default.Extension,
                        accentColor = Color(0xFF8B5CF6),
                        tag = "tool_practice_hub",
                        onClick = { viewModel.navigateTo(ScreenRoute.PRACTICE_HUB) }
                    )
                }

                item {
                    DashboardFeatureCard(
                        title = "Sign Language Companion",
                        subtitle = "ASL fingerspelling A-Z alphabet flashcards and everyday gestures.",
                        icon = Icons.Default.PanTool,
                        accentColor = Color(0xFF0284C7),
                        tag = "tool_sign_language",
                        onClick = { viewModel.navigateTo(ScreenRoute.SIGN_LANGUAGE) }
                    )
                }

                item {
                    DashboardFeatureCard(
                        title = "Silent Voice (AAC Speech)",
                        subtitle = "Tap-to-speak communication boards, urgent cards, and custom sentence builder.",
                        icon = Icons.Default.RecordVoiceOver,
                        accentColor = Color(0xFFE11D48),
                        tag = "tool_silent_voice",
                        onClick = { viewModel.navigateTo(ScreenRoute.SILENT_VOICE) }
                    )
                }

                item {
                    DashboardFeatureCard(
                        title = "Inclusive Screener",
                        subtitle = "Friendly 10-question self-assessment identifying reading patterns (0–100 rating).",
                        icon = Icons.AutoMirrored.Filled.Assignment,
                        accentColor = Color(0xFF10B981),
                        tag = "tool_screener",
                        onClick = {
                            viewModel.startNewShuffledScreener()
                            viewModel.navigateTo(ScreenRoute.SCREENING)
                        }
                    )
                }

                item {
                    DashboardFeatureCard(
                        title = "ALTER Learning Guidelines",
                        subtitle = "Friendly accessibility principles, dyslexia reading flow, practice modules, and offline-first guide.",
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        accentColor = Indigo600,
                        tag = "tool_guidelines",
                        onClick = { viewModel.navigateTo(ScreenRoute.GUIDELINES) }
                    )
                }
            }

            // Draggable reading ruler overlay if enabled
            ReadingRulerOverlay(
                isEnabled = preferences.readingRulerEnabled,
                heightDp = preferences.rulerHeightDp,
                rulerColorHex = preferences.rulerColorHex
            )

            // Bottom Navigation Bar
            AppBottomNavigation(
                currentRoute = ScreenRoute.DASHBOARD,
                onTabSelected = { route -> viewModel.switchTab(route) },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun DashboardFeatureCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    tag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .testTag(tag),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
