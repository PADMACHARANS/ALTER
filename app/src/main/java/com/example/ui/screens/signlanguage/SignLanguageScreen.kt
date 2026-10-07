package com.example.ui.screens.signlanguage

import android.Manifest
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HandRequirement
import com.example.data.model.ISLSignItem
import com.example.data.model.SignProgressEntity
import com.example.data.repository.ISLSignDictionary
import com.example.ui.components.AmbientAtmosphere
import com.example.ui.components.AppBottomNavigation
import com.example.ui.components.CameraHandGuide
import com.example.ui.components.HandGestureVisualizer
import com.example.ui.theme.Amber500
import com.example.ui.theme.CoralRed
import com.example.ui.theme.Indigo600
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.Teal600
import com.example.ui.viewmodel.ReadEaseViewModel
import com.example.ui.viewmodel.ScreenRoute

/**
 * SignLanguageScreen provides an authentic, source-backed Indian Sign Language (ISL) learning experience:
 * - Sourced strictly from Indian Sign Language Research and Training Centre (ISLRTC) & NCERT CIET
 * - Real anatomical hand gesture demonstrations with step-by-step guidance & motion indicators
 * - Comprehensive categories: Greetings, Emergency, Everyday, Education, Emotions, Family, Food
 * - On-device Camera gesture practice with hand calibration guidelines
 * - Robust progress, accuracy, and mastery tracking synced with Room & Firestore
 */
@Composable
fun SignLanguageScreen(
    viewModel: ReadEaseViewModel,
    modifier: Modifier = Modifier
) {
    val preferences by viewModel.accessibilityPreferences.collectAsState()
    val allSigns = viewModel.islSigns
    val signProgressList by viewModel.allSignProgress.collectAsState()

    var selectedCategory by remember { mutableStateOf("All Signs") }
    var searchQuery by remember { mutableStateOf("") }

    // Navigation and detail states
    var activeSignDetail by remember { mutableStateOf<ISLSignItem?>(null) }
    var currentStepIndex by remember { mutableIntStateOf(0) }
    var isPracticeMode by remember { mutableStateOf(false) }

    val categories = ISLSignDictionary.availableCategories

    // Filter signs based on category and search query
    val filteredSigns = remember(selectedCategory, searchQuery, allSigns) {
        allSigns.filter { sign ->
            val matchesCategory = selectedCategory == "All Signs" || sign.category.equals(selectedCategory, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    sign.term.contains(searchQuery, ignoreCase = true) ||
                    sign.hindiEquivalent.contains(searchQuery, ignoreCase = true) ||
                    sign.englishMeaning.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    // Resume learning recommendation: finding a sign in progress
    val resumeSign = remember(signProgressList, allSigns) {
        val inProgress = signProgressList.find { it.currentStep in 2..3 && !it.completed }
        inProgress?.let { prog -> allSigns.find { it.id == prog.signId } }
    }

    // BackHandler to handle sub-navigation properly
    BackHandler {
        if (isPracticeMode) {
            isPracticeMode = false
        } else if (activeSignDetail != null) {
            activeSignDetail = null
            currentStepIndex = 0
        } else {
            viewModel.soundManager.stopSpeaking()
            viewModel.navigateBack()
        }
    }

    AmbientAtmosphere(tint = preferences.backgroundTint) {
        Box(modifier = modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(14.dp))

                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (isPracticeMode) {
                                isPracticeMode = false
                            } else if (activeSignDetail != null) {
                                activeSignDetail = null
                                currentStepIndex = 0
                            } else {
                                viewModel.soundManager.stopSpeaking()
                                viewModel.navigateBack()
                            }
                        },
                        modifier = Modifier.testTag("isl_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (activeSignDetail != null) activeSignDetail!!.term else "Indian Sign Language (ISL)",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (activeSignDetail != null) "ISLRTC Standard Demonstration" else "ISLRTC & NCERT Standard (Govt. of India)",
                            style = MaterialTheme.typography.bodySmall,
                            color = Teal600,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    IconButton(
                        onClick = {
                            val textToSpeak = if (activeSignDetail != null) {
                                "${activeSignDetail!!.term}. ${activeSignDetail!!.englishMeaning}. ${activeSignDetail!!.description}"
                            } else {
                                "Indian Sign Language dictionary. Learn authentic hand gestures sourced from the Indian Sign Language Research and Training Centre."
                            }
                            viewModel.soundManager.speak(textToSpeak)
                        },
                        modifier = Modifier.testTag("isl_speak_header_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Read Aloud",
                            tint = Indigo600
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (isPracticeMode && activeSignDetail != null) {
                    // CAMERA GESTURE PRACTICE MODE
                    val sign = activeSignDetail!!
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 85.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            CameraHandGuide(
                                sign = sign,
                                onAttemptCompleted = { score, confidence, isSuccess, feedback ->
                                    viewModel.recordISLPracticeAttempt(
                                        signId = sign.id,
                                        score = score,
                                        confidence = confidence,
                                        feedback = feedback,
                                        currentStep = 4
                                    )
                                }
                            )
                        }

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "Target Gesture Reference",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = sign.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Hand Requirement: ${sign.handsRequired.displayName}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Indigo600
                                    )
                                }
                            }
                        }

                        item {
                            Button(
                                onClick = { isPracticeMode = false },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("finish_practice_button")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Back to Sign Tutorial", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else if (activeSignDetail != null) {
                    // SIGN DETAIL SCREEN (Step-by-Step Learning & Visuals)
                    val sign = activeSignDetail!!
                    val progress = signProgressList.find { it.signId == sign.id }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 85.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Hand Gesture Visualizer Component
                        item {
                            HandGestureVisualizer(
                                sign = sign,
                                currentStepIndex = currentStepIndex,
                                onStepChange = { nextStep ->
                                    currentStepIndex = nextStep
                                    viewModel.saveSignProgress(
                                        (progress ?: SignProgressEntity(signId = sign.id)).copy(
                                            currentStep = nextStep + 1
                                        )
                                    )
                                }
                            )
                        }

                        // Practice with Camera Button
                        item {
                            Button(
                                onClick = { isPracticeMode = true },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Teal600),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("start_camera_practice_button")
                            ) {
                                Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Practice Gesture with Camera", fontWeight = FontWeight.Bold)
                            }
                        }

                        // What You'll Learn & Anatomy Breakdown
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "What You'll Learn (ISL Mechanics)",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))

                                    AnatomyInfoRow(
                                        title = "Palm Orientation",
                                        description = sign.palmOrientation,
                                        icon = Icons.Default.PanTool
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    AnatomyInfoRow(
                                        title = "Wrist Orientation",
                                        description = sign.wristOrientation,
                                        icon = Icons.Default.Adjust
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    AnatomyInfoRow(
                                        title = "Movement Flow",
                                        description = sign.movementDirection,
                                        icon = Icons.Default.Sync
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    AnatomyInfoRow(
                                        title = "Context of Use",
                                        description = sign.contextOfUse,
                                        icon = Icons.Default.Info
                                    )
                                }
                            }
                        }

                        // Verified Source Attribution Card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Indigo600.copy(alpha = 0.08f)
                                ),
                                border = BorderStroke(1.dp, Indigo600.copy(alpha = 0.2f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = "Verified",
                                            tint = Teal600,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Verified Source Metadata",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Teal600
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Primary Reference: ${sign.sourceName}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Dictionary Archive: ${sign.sourceUrl}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Indigo600
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Educational Reference: ${sign.secondarySource}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // MAIN ISL CURRICULUM LIST & CATEGORIES
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 85.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Resume Learning Alert if available
                        if (resumeSign != null) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Amber500.copy(alpha = 0.12f)
                                    ),
                                    border = BorderStroke(1.dp, Amber500.copy(alpha = 0.3f))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Continue where you left off?",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Amber500
                                            )
                                            Text(
                                                text = "${resumeSign.term} • Step ${(signProgressList.find { it.signId == resumeSign.id }?.currentStep ?: 1)} of ${resumeSign.steps.size}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }

                                        Button(
                                            onClick = {
                                                activeSignDetail = resumeSign
                                                currentStepIndex = (signProgressList.find { it.signId == resumeSign.id }?.currentStep ?: 1) - 1
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Amber500),
                                            modifier = Modifier.testTag("resume_learning_button")
                                        ) {
                                            Text("Resume", color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        // Category Chips Row
                        item {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(categories) { cat ->
                                    val isSelected = selectedCategory == cat
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedCategory = cat },
                                        label = {
                                            Text(
                                                text = cat,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Indigo600,
                                            selectedLabelColor = Color.White
                                        ),
                                        modifier = Modifier.testTag("isl_category_${cat.lowercase().replace(" ", "_")}")
                                    )
                                }
                            }
                        }

                        // List of Signs in selected category
                        items(filteredSigns, key = { it.id }) { sign ->
                            val progress = signProgressList.find { it.signId == sign.id }
                            ISLSignCard(
                                sign = sign,
                                progress = progress,
                                onClick = {
                                    activeSignDetail = sign
                                    currentStepIndex = 0
                                }
                            )
                        }

                        if (filteredSigns.isEmpty()) {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(40.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "No verified ISL entry found",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Signs are strictly vetted through ISLRTC dictionary records.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Navigation Bar
            AppBottomNavigation(
                currentRoute = ScreenRoute.SIGN_LANGUAGE,
                onTabSelected = { route -> viewModel.switchTab(route) },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun ISLSignCard(
    sign: ISLSignItem,
    progress: SignProgressEntity?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag("sign_card_${sign.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hand Gesture Badge Avatar
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (progress?.mastered == true) LeafGreen.copy(alpha = 0.15f)
                        else if (progress?.completed == true) Teal600.copy(alpha = 0.15f)
                        else Indigo600.copy(alpha = 0.12f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (progress?.mastered == true) Icons.Default.CheckCircle
                    else if (sign.handsRequired == HandRequirement.TWO_HANDS) Icons.Default.PanTool
                    else Icons.Default.WavingHand,
                    contentDescription = sign.term,
                    tint = if (progress?.mastered == true) LeafGreen
                    else if (progress?.completed == true) Teal600
                    else Indigo600,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = sign.term,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = if (progress?.mastered == true) "Mastered"
                        else if (progress?.completed == true) "Completed"
                        else sign.difficulty,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (progress?.mastered == true) LeafGreen
                        else if (progress?.completed == true) Teal600
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = sign.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = sign.handsRequired.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = Indigo600,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "• ${sign.steps.size} Steps",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (progress != null && progress.attempts > 0) {
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "• ${progress.masteryScore}% Mastery",
                            style = MaterialTheme.typography.labelSmall,
                            color = Teal600,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnatomyInfoRow(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Indigo600.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Indigo600,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
