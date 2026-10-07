package com.example.ui.screens.silentvoice

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
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
import com.example.data.model.AACCard
import com.example.data.model.AACCategory
import com.example.ui.components.AmbientAtmosphere
import com.example.ui.theme.CoralRed
import com.example.ui.theme.Indigo600
import com.example.ui.theme.Teal600
import com.example.ui.viewmodel.ReadEaseViewModel

@Composable
fun SilentVoiceScreen(
    viewModel: ReadEaseViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.soundManager.stopSpeaking()
        viewModel.navigateBack()
    }

    val preferences by viewModel.accessibilityPreferences.collectAsState()
    val allCards = viewModel.aacCards
    val sentenceTokens by viewModel.aacSentence.collectAsState()

    var selectedCategory by remember { mutableStateOf<AACCategory?>(null) }
    var customQuickType by remember { mutableStateOf("") }

    val filteredCards = if (selectedCategory == null) {
        allCards
    } else {
        allCards.filter { it.category == selectedCategory }
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
                    modifier = Modifier.testTag("silent_voice_back_button")
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
                        text = "Silent Voice (AAC Speech)",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Augmentative Communication & Tap-to-Speak Boards",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sentence Builder Tray
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sentence Builder Tray",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Teal600
                        )

                        if (sentenceTokens.isNotEmpty()) {
                            IconButton(
                                onClick = { viewModel.clearAacSentence() },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (sentenceTokens.isEmpty()) {
                        Text(
                            text = "Tap any card below to speak instantly, or build a sentence.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(sentenceTokens) { token ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Indigo600.copy(alpha = 0.12f))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = token.emoji, fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = token.text,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Indigo600
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { viewModel.speakAacSentence() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("speak_sentence_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Speak Sentence", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Category Filter Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("All Cards") }
                    )
                }
                items(AACCategory.values()) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat.title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // AAC Cards Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredCards) { card ->
                    AACCardTile(
                        card = card,
                        onSpeakCard = {
                            viewModel.speakAacCard(card)
                        },
                        onAddToSentence = {
                            viewModel.addAacCardToSentence(card)
                        }
                    )
                }
            }

            // Quick Type & Speak Input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = customQuickType,
                    onValueChange = { customQuickType = it },
                    placeholder = { Text("Type custom words...") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (customQuickType.isNotBlank()) {
                            viewModel.soundManager.speak(customQuickType)
                            customQuickType = ""
                        }
                    },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Indigo600)
                ) {
                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Speak", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun AACCardTile(
    card: AACCard,
    onSpeakCard: () -> Unit,
    onAddToSentence: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable { onSpeakCard() }
            .testTag("aac_card_${card.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (card.priority) CoralRed.copy(alpha = 0.12f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(text = card.symbolEmoji, fontSize = 32.sp)

                IconButton(
                    onClick = onAddToSentence,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add to sentence",
                        tint = Indigo600,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column {
                Text(
                    text = card.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (card.priority) CoralRed else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = card.speechPhrase,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}
