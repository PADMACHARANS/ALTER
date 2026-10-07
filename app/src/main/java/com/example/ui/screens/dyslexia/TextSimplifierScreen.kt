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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SyllableWord
import com.example.ui.components.AmbientAtmosphere
import com.example.ui.theme.Amber500
import com.example.ui.theme.Indigo600
import com.example.ui.theme.Teal600
import com.example.ui.viewmodel.ReadEaseViewModel

@Composable
fun TextSimplifierScreen(
    viewModel: ReadEaseViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.soundManager.stopSpeaking()
        viewModel.navigateBack()
    }

    val preferences by viewModel.accessibilityPreferences.collectAsState()
    val syllableWords = viewModel.syllableWords

    var inputText by remember {
        mutableStateOf(
            "Photosynthesis is the fantastic biological process by which green plants utilize solar energy and atmospheric carbon dioxide to synthesize nutrients and oxygen."
        )
    }

    var simplifiedOutput by remember {
        mutableStateOf<String?>(
            "Plants use sunlight and air to make their own food.\n\nThey take in solar light and carbon.\nThey produce fresh oxygen for animals to breathe."
        )
    }

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
                    onClick = {
                        viewModel.soundManager.stopSpeaking()
                        viewModel.navigateBack()
                    },
                    modifier = Modifier.testTag("simplifier_back_button")
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
                        text = "Text & Syllable Simplifier",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Plain language and phonetic syllable breaks",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Input card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Original Complex Text",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = inputText,
                                onValueChange = { inputText = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .testTag("simplifier_input"),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    viewModel.soundManager.playToneClick()
                                    // Generate simplified version
                                    simplifiedOutput = "• " + inputText
                                        .replace("biological process", "natural way")
                                        .replace("utilize solar energy", "use warm sunlight")
                                        .replace("atmospheric carbon dioxide", "fresh air")
                                        .replace("synthesize nutrients", "create sweet food")
                                        .split(". ")
                                        .joinToString("\n\n• ")
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("simplify_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Simplify into Plain Sentences", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Simplified Result Card
                if (simplifiedOutput != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Teal600.copy(alpha = 0.08f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Teal600)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Plain Language Version",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Teal600
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            viewModel.soundManager.speak(simplifiedOutput ?: "")
                                        }
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Read Aloud", tint = Teal600)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = simplifiedOutput!!,
                                    style = MaterialTheme.typography.bodyLarge,
                                    lineHeight = 26.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // Syllable Explorer Section
                item {
                    Text(
                        text = "Syllable Breakdown Explorer",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap any syllable chunk to hear its phonetic sound:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                items(syllableWords) { item ->
                    SyllableWordCard(
                        syllableWord = item,
                        onSyllableClick = { chunk ->
                            viewModel.soundManager.speak(chunk, rate = 0.75f)
                        },
                        onFullWordClick = {
                            viewModel.soundManager.speak(item.word, rate = 0.85f)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SyllableWordCard(
    syllableWord: SyllableWord,
    onSyllableClick: (String) -> Unit,
    onFullWordClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = syllableWord.word.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = syllableWord.phoneticSpelling,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = onFullWordClick,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Listen", tint = Indigo600, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Syllable chunks chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                syllableWord.syllables.forEachIndexed { index, syllable ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Indigo600.copy(alpha = 0.12f))
                            .clickable { onSyllableClick(syllable) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = syllable,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Indigo600
                        )
                    }
                    if (index < syllableWord.syllables.size - 1) {
                        Text(text = "•", color = Color.Gray, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = syllableWord.definition,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
