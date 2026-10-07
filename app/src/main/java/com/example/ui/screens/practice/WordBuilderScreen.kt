package com.example.ui.screens.practice

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Lightbulb
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
import com.example.ui.theme.Amber500
import com.example.ui.theme.Indigo600
import com.example.ui.theme.Teal600
import com.example.ui.viewmodel.ReadEaseViewModel

@Composable
fun WordBuilderScreen(
    viewModel: ReadEaseViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val preferences by viewModel.accessibilityPreferences.collectAsState()
    val puzzle = viewModel.currentWbPuzzle
    val formedLetters by viewModel.wbCurrentLetters.collectAsState()

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
                        modifier = Modifier.testTag("word_builder_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "Word Builder",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = { viewModel.nextWbPuzzle() },
                    modifier = Modifier.testTag("wb_skip_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Skip Puzzle",
                        tint = Indigo600
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Hint Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Amber500)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = puzzle.category,
                            style = MaterialTheme.typography.labelLarge,
                            color = Teal600,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = puzzle.hint,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    IconButton(
                        onClick = { viewModel.soundManager.speak(puzzle.hint) }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Hear Clue", tint = Indigo600)
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Word Slots (Target length)
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until puzzle.targetWord.length) {
                    val letter = formedLetters.getOrNull(i)
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (letter != null) Indigo600.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .border(
                                width = 2.dp,
                                color = if (letter != null) Indigo600 else Color.LightGray.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = letter ?: "_",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = if (letter != null) Indigo600 else Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Tap letters in order to build the word:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Scrambled Letter Tiles
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                puzzle.scrambledLetters.forEach { letter ->
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(2.dp, Indigo600, RoundedCornerShape(18.dp))
                            .clickable { viewModel.addWbLetter(letter) }
                            .testTag("wb_tile_$letter"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = letter,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Black,
                            color = Indigo600
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Actions: Backspace & Clear
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { viewModel.removeWbLastLetter() },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("wb_backspace_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = "Backspace", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Undo", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { viewModel.clearWbLetters() },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("wb_clear_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Clear", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
