package com.example.ui.screens.guidelines

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.OfflineBolt
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AmbientAtmosphere
import com.example.ui.theme.Amber500
import com.example.ui.theme.CoralRed
import com.example.ui.theme.Indigo600
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.Teal600
import com.example.ui.viewmodel.ReadEaseViewModel
import com.example.ui.viewmodel.ScreenRoute

@Composable
fun GuidelinesScreen(
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

            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier.testTag("guidelines_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ALTER Guidelines",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Friendly User Guide & Accessibility Principles",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = {
                        viewModel.soundManager.speak(
                            "Welcome to ALTER Guidelines. Here are our accessibility principles, learning flows, and offline-first tools designed to make reading and communication easy and comfortable."
                        )
                    },
                    modifier = Modifier.testTag("guidelines_speak_all_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Listen to guidelines introduction",
                        tint = Indigo600
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Welcome Hero Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Indigo600.copy(alpha = 0.08f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Indigo600),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lightbulb,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Welcome to ALTER!",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Indigo600
                                    )
                                    Text(
                                        text = "Inclusive Learning Studio",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "ALTER is built specifically for neurodiverse learners, individuals with dyslexia, and assistive communicators. Learn at your own pace without pressure or timed penalties.",
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = 22.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // 1. Accessibility Principles
                item {
                    GuidelineSectionCard(
                        title = "1. Core Accessibility Principles",
                        subtitle = "Designed for clarity, visual comfort & cognitive ease",
                        icon = Icons.Default.FormatSize,
                        accentColor = Indigo600,
                        viewModel = viewModel,
                        initiallyExpanded = true,
                        points = listOf(
                            GuidelinePoint(
                                header = "OpenDyslexic Typography",
                                description = "Letters feature gravity-weighted bottoms to prevent visual flipping, spinning, and letter inversions.",
                                icon = Icons.Default.FormatSize
                            ),
                            GuidelinePoint(
                                header = "Bionic Reading Flow",
                                description = "Artificial eye fixation points bold the opening characters of each word to guide saccadic eye movement smoothly across sentences.",
                                icon = Icons.Default.AutoAwesome
                            ),
                            GuidelinePoint(
                                header = "Scotopic Sensitivity Tints",
                                description = "6 calming background filters (Cream, Pastel Yellow, Soft Blue, Mint, Warm Peach, Dark Comfort) eliminate blinding white page glare and scotopic eye fatigue.",
                                icon = Icons.Default.Palette
                            ),
                            GuidelinePoint(
                                header = "Line-Focus Reading Ruler",
                                description = "Draggable highlight ruler isolates individual lines of text to prevent accidental line skipping and visual crowding.",
                                icon = Icons.Default.Straighten
                            ),
                            GuidelinePoint(
                                header = "Multi-Sensory Audio Support",
                                description = "Every word, question, explanation, and mnemonic includes instant tap-to-speak audio playback with customizable pitch and speech rate.",
                                icon = Icons.AutoMirrored.Filled.VolumeUp
                            )
                        )
                    )
                }

                // 2. Dyslexia Learning Flow
                item {
                    GuidelineSectionCard(
                        title = "2. Dyslexia Learning Flow",
                        subtitle = "Adaptive, self-paced progress tracking",
                        icon = Icons.Default.Psychology,
                        accentColor = Teal600,
                        viewModel = viewModel,
                        points = listOf(
                            GuidelinePoint(
                                header = "10-Question Shuffled Assessment",
                                description = "Friendly, non-diagnostic screening that assesses spatial reversal, phonological segmentation, and visual strain to produce a 0–100 Dyslexia Indicator Rating.",
                                icon = Icons.Default.Psychology
                            ),
                            GuidelinePoint(
                                header = "Daily Literacy Quiz & Streak",
                                description = "5 daily bite-sized questions reinforce letter discrimination and phonics rules. Scoring ≥60% increments your daily streak and awards XP.",
                                icon = Icons.Default.LocalFireDepartment
                            ),
                            GuidelinePoint(
                                header = "Personalized Assistive Matching",
                                description = "Assessment results automatically match you to the specific tools (e.g. Bed Rule mnemonics if reversals are frequent, Reading Ruler if glare is high).",
                                icon = Icons.Default.Extension
                            )
                        )
                    )
                }

                // 3. Interactive Practice Modules
                item {
                    GuidelineSectionCard(
                        title = "3. Practice Modules",
                        subtitle = "Engaging games & multisensory practice",
                        icon = Icons.Default.Extension,
                        accentColor = Amber500,
                        viewModel = viewModel,
                        points = listOf(
                            GuidelinePoint(
                                header = "Phonics & Letter Mnemonics",
                                description = "Master easily confused pairs ('b' vs 'd', 'p' vs 'q', 'm' vs 'w') with somatosensory hand tricks and memorable visual anchors like The Bed Rule.",
                                icon = Icons.Default.Lightbulb
                            ),
                            GuidelinePoint(
                                header = "Sound Steps (Phoneme Blending)",
                                description = "Step through individual sound chunks (e.g. /s/ + /ʌ/ + /n/) and blend them into full words with animated audio clues.",
                                icon = Icons.Default.Hearing
                            ),
                            GuidelinePoint(
                                header = "Word Builder",
                                description = "Unscramble target words using auditory hints and large tactile letter tiles with full Undo and Clear support.",
                                icon = Icons.Default.Extension
                            ),
                            GuidelinePoint(
                                header = "Read-Along Stories",
                                description = "Karaoke-synchronized word highlighting follows comforting natural speech narration so readers always know their place.",
                                icon = Icons.AutoMirrored.Filled.VolumeUp
                            ),
                            GuidelinePoint(
                                header = "Sign Language Reference",
                                description = "Explore the full A–Z American Sign Language (ASL) fingerspelling alphabet with step-by-step handshape guides and common everyday phrases.",
                                icon = Icons.Default.PanTool
                            ),
                            GuidelinePoint(
                                header = "Silent Voice (AAC Speech Board)",
                                description = "Tap assistive communication tiles across Greetings, Needs, Feelings, and Responses to build complete spoken sentences.",
                                icon = Icons.Default.RecordVoiceOver
                            )
                        )
                    )
                }

                // 4. Settings & Comfort Customizations
                item {
                    GuidelineSectionCard(
                        title = "4. Comfort & Personal Settings",
                        subtitle = "Customize the interface to suit your sensory needs",
                        icon = Icons.Default.Settings,
                        accentColor = Color(0xFF8B5CF6),
                        viewModel = viewModel,
                        points = listOf(
                            GuidelinePoint(
                                header = "Dynamic Typography Sizing",
                                description = "Scale font size from 80% to 160%, expand letter spacing (+0.5sp to +3sp), and increase line leading (+1.4× to 2.2×).",
                                icon = Icons.Default.FormatSize
                            ),
                            GuidelinePoint(
                                header = "Voice Speech Rate & Pitch",
                                description = "Slow down reading audio (0.6×) for deliberate syllable decoding or speed it up (1.4×) to match your comprehension rhythm.",
                                icon = Icons.Default.RecordVoiceOver
                            ),
                            GuidelinePoint(
                                header = "Large Touch Targets",
                                description = "Every button, card, and tile strictly satisfies Android accessibility guidelines with at least 48dp to 56dp touch targets.",
                                icon = Icons.Default.PanTool
                            )
                        )
                    )
                }

                // 5. Offline-First & Privacy Principles
                item {
                    GuidelineSectionCard(
                        title = "5. Offline-First & Privacy Guarantees",
                        subtitle = "Safe, private learning that requires no internet",
                        icon = Icons.Default.OfflineBolt,
                        accentColor = LeafGreen,
                        viewModel = viewModel,
                        points = listOf(
                            GuidelinePoint(
                                header = "100% Offline Capable",
                                description = "Every lesson, quiz, reader text, and sound synthesis works entirely without internet connection or network dependency.",
                                icon = Icons.Default.OfflineBolt
                            ),
                            GuidelinePoint(
                                header = "Zero Data Tracking",
                                description = "All your quiz scores, screener responses, custom text entries, and settings remain stored on your device in secure Room storage. We never sell your personal data.",
                                icon = Icons.Default.Security
                            ),
                            GuidelinePoint(
                                header = "Guest Mode Ready",
                                description = "No account creation or email registration required to access all learning tools. Start learning immediately.",
                                icon = Icons.Default.Psychology
                            )
                        )
                    )
                }

                // Quick Navigation Actions
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            Text(
                                text = "Ready to Begin?",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Explore our dyslexia tools or adjust your reading comfort settings anytime.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            var isConsentChecked by remember { mutableStateOf(false) }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isConsentChecked = !isConsentChecked }
                                    .padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isConsentChecked,
                                    onCheckedChange = { isConsentChecked = it },
                                    modifier = Modifier.testTag("guidelines_consent_checkbox")
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "I have read and understood the safety guidelines",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = { viewModel.acceptGuidelines() },
                                enabled = isConsentChecked,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("guidelines_continue_btn"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = if (isConsentChecked) Indigo600 else Color.Gray)
                            ) {
                                Text("Acknowledge & Continue", fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = { viewModel.navigateTo(ScreenRoute.ACCESSIBILITY_SETTINGS) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("guidelines_go_settings_btn"),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("Open Accessibility Comfort Settings")
                            }
                        }
                    }
                }
            }
        }
    }
}

data class GuidelinePoint(
    val header: String,
    val description: String,
    val icon: ImageVector
)

@Composable
fun GuidelineSectionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    viewModel: ReadEaseViewModel,
    initiallyExpanded: Boolean = false,
    points: List<GuidelinePoint>,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(initiallyExpanded) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = {
                        val textToRead = "$title. " + points.joinToString(". ") { "${it.header}: ${it.description}" }
                        viewModel.soundManager.speak(textToRead)
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Read section aloud",
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    points.forEach { point ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(accentColor.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = point.icon,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = point.header,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = point.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    lineHeight = 20.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
