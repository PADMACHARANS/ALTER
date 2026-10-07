package com.example.ui.screens.practice

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.example.ui.components.AmbientAtmosphere
import com.example.ui.components.WaveformVisualizer
import com.example.ui.theme.Amber500
import com.example.ui.theme.Indigo600
import com.example.ui.theme.Teal600
import com.example.ui.viewmodel.ReadEaseViewModel

@Composable
fun SoundStepsScreen(
    viewModel: ReadEaseViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val preferences by viewModel.accessibilityPreferences.collectAsState()
    val exercise = viewModel.currentSoundStep
    val activeSegment by viewModel.activeSoundSegment.collectAsState()

    AmbientAtmosphere(tint = preferences.backgroundTint) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("sound_steps_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "Sound Steps",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = { viewModel.nextSoundStep() },
                    modifier = Modifier.testTag("sound_steps_next_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next Exercise",
                        tint = Indigo600
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Clue Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = exercise.visualClue,
                        fontSize = 54.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Blend the sounds together to reveal the word",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Step 1: Tap each sound segment in order",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Sound Segment Cards
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                exercise.soundSegments.forEachIndexed { index, segment ->
                    val isActive = index == activeSegment
                    val bgColor by animateColorAsState(
                        targetValue = if (isActive) Indigo600 else MaterialTheme.colorScheme.surface,
                        label = "segmentBg"
                    )
                    val textColor by animateColorAsState(
                        targetValue = if (isActive) Color.White else Indigo600,
                        label = "segmentText"
                    )

                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(bgColor)
                            .border(2.dp, Indigo600, RoundedCornerShape(20.dp))
                            .clickable { viewModel.playSoundSegment(index) }
                            .testTag("sound_segment_$index"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = segment,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                color = textColor
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Sound ${index + 1}",
                                fontSize = 10.sp,
                                color = if (isActive) Color.White.copy(alpha = 0.8f) else Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Active step description
            val currentDescription = exercise.stepDescriptions.getOrNull(activeSegment) ?: ""
            if (currentDescription.isNotBlank()) {
                Text(
                    text = "🔊 $currentDescription",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Teal600
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Step 2: Blend Button
            Button(
                onClick = { viewModel.blendSoundStep() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("sound_blend_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
            ) {
                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Blend into '${exercise.fullWord}'! (+25 XP)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
