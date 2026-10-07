package com.example.ui.screens.dyslexia

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BackgroundTint
import com.example.ui.components.AmbientAtmosphere
import com.example.ui.components.ReadingRulerOverlay
import com.example.ui.components.WaveformVisualizer
import com.example.ui.theme.Amber500
import com.example.ui.theme.Indigo600
import com.example.ui.theme.Teal600
import com.example.ui.viewmodel.ReadEaseViewModel

@Composable
fun DyslexiaReaderScreen(
    viewModel: ReadEaseViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.soundManager.stopSpeaking()
        viewModel.navigateBack()
    }

    val preferences by viewModel.accessibilityPreferences.collectAsState()
    val stories = viewModel.dyslexiaStories
    val selectedStory by viewModel.selectedStory.collectAsState()

    var isCustomTextMode by remember { mutableStateOf(false) }
    var customTextInput by remember { mutableStateOf("Paste or type your own text here to read with our dyslexia-friendly font, reading ruler, and bionic reading highlights.") }
    var isSpeaking by remember { mutableStateOf(false) }

    val activeText = if (isCustomTextMode) customTextInput else selectedStory.content

    AmbientAtmosphere(tint = preferences.backgroundTint) {
        Box(modifier = modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp)
            ) {
                // Top Action Bar
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                viewModel.soundManager.stopSpeaking()
                                viewModel.navigateBack()
                            },
                            modifier = Modifier.testTag("reader_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "Dyslexia Reader",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Ruler Toggle
                        IconButton(
                            onClick = { viewModel.toggleReadingRuler() },
                            modifier = Modifier.testTag("reader_ruler_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Straighten,
                                contentDescription = "Toggle Ruler",
                                tint = if (preferences.readingRulerEnabled) Amber500 else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Bionic Reading Toggle
                        IconButton(
                            onClick = {
                                viewModel.updatePreferences(
                                    preferences.copy(bionicReadingEnabled = !preferences.bionicReadingEnabled)
                                )
                                viewModel.soundManager.playToneClick()
                            },
                            modifier = Modifier.testTag("reader_bionic_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatBold,
                                contentDescription = "Toggle Bionic Reading",
                                tint = if (preferences.bionicReadingEnabled) Indigo600 else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Speech Audio
                        IconButton(
                            onClick = {
                                if (isSpeaking) {
                                    viewModel.soundManager.stopSpeaking()
                                    isSpeaking = false
                                } else {
                                    viewModel.soundManager.speak(activeText, rate = preferences.speechRate)
                                    isSpeaking = true
                                }
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Indigo600.copy(alpha = 0.15f))
                                .testTag("reader_tts_button")
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Read Aloud",
                                tint = Indigo600
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Story Selector Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(stories) { story ->
                        FilterChip(
                            selected = (!isCustomTextMode && story.id == selectedStory.id),
                            onClick = {
                                isCustomTextMode = false
                                viewModel.selectStory(story)
                            },
                            label = { Text(story.title) },
                            modifier = Modifier.testTag("story_chip_${story.id}")
                        )
                    }
                    item {
                        FilterChip(
                            selected = isCustomTextMode,
                            onClick = { isCustomTextMode = true },
                            label = { Text("Custom Text") },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            modifier = Modifier.testTag("story_chip_custom")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Format Controls Bar
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Font Size buttons
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Size: ", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            IconButton(
                                onClick = {
                                    val newScale = (preferences.fontScale - 0.1f).coerceAtLeast(0.8f)
                                    viewModel.updatePreferences(preferences.copy(fontScale = newScale))
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Smaller", modifier = Modifier.size(16.dp))
                            }
                            Text(
                                text = "${(preferences.fontScale * 100).toInt()}%",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(
                                onClick = {
                                    val newScale = (preferences.fontScale + 0.1f).coerceAtMost(1.6f)
                                    viewModel.updatePreferences(preferences.copy(fontScale = newScale))
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Larger", modifier = Modifier.size(16.dp))
                            }
                        }

                        // Background Tint Selector Icon Button
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Tint: ", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            val tints = listOf(BackgroundTint.NONE, BackgroundTint.CREAM, BackgroundTint.PASTEL_YELLOW, BackgroundTint.SOFT_BLUE, BackgroundTint.MINT)
                            tints.forEach { tint ->
                                val isSelected = preferences.backgroundTint == tint
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 3.dp)
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (tint == BackgroundTint.NONE) Color.LightGray else Color(tint.hexColor)
                                        )
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) Indigo600 else Color.Gray.copy(alpha = 0.5f),
                                            shape = CircleShape
                                        )
                                        .clickable { viewModel.setBackgroundTint(tint) }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (isSpeaking) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        WaveformVisualizer(isPlaying = true)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Reading Aloud...",
                            style = MaterialTheme.typography.bodySmall,
                            color = Indigo600,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                if (isCustomTextMode) {
                    OutlinedTextField(
                        value = customTextInput,
                        onValueChange = { customTextInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        label = { Text("Your text to read") },
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                } else {
                    Text(
                        text = selectedStory.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Level: ${selectedStory.readingLevel} • ${selectedStory.category}",
                        style = MaterialTheme.typography.labelLarge,
                        color = Teal600
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Dedicated DyslexiaReadingView Component
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    com.example.ui.components.DyslexiaReadingView(
                        text = activeText,
                        preferences = preferences,
                        enableRuler = preferences.readingRulerEnabled,
                        rulerHeightDp = preferences.rulerHeightDp,
                        rulerColorHex = preferences.rulerColorHex,
                        onWordClick = { word, _ ->
                            viewModel.soundManager.speak(word, rate = preferences.speechRate)
                        },
                        showFilterControls = false,
                        onPreferencesChange = { newPrefs ->
                            viewModel.updatePreferences(newPrefs)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}
