package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LetterMnemonic
import com.example.ui.theme.Amber500
import com.example.ui.theme.Indigo600
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.Teal600

/**
 * PhonicsMnemonics component displays interactive cards pairing letters with
 * visual mnemonic associations (e.g. b/d bed rule, p/q parachute, m/w mountain).
 * It features tactile tap sounds, expand-to-reveal memory rules, interactive word chips,
 * and an integrated discrimination quiz.
 */
@Composable
fun PhonicsMnemonics(
    mnemonics: List<LetterMnemonic>,
    modifier: Modifier = Modifier,
    onLetterSpeak: ((letter: Char) -> Unit)? = null,
    onWordSpeak: ((word: String) -> Unit)? = null,
    showQuizSection: Boolean = true,
    initialSelectedLetter: Char = 'b'
) {
    var selectedLetter by remember { mutableStateOf(initialSelectedLetter) }
    var expandedLetter by remember { mutableStateOf<Char?>(initialSelectedLetter) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("phonics_mnemonics_component")
    ) {
        // Quick Letter Selector Bar
        Text(
            text = "Select Letter Pair to Explore:",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(mnemonics) { item ->
                val isSelected = item.letter == selectedLetter
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        selectedLetter = item.letter
                        expandedLetter = item.letter
                        onLetterSpeak?.invoke(item.letter)
                    },
                    label = {
                        Text(
                            text = "${item.letter} ↔ ${item.counterpart}",
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    modifier = Modifier.testTag("mnemonics_filter_${item.letter}")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main Mnemonic Cards
        mnemonics.forEach { mnemonic ->
            val isFocused = mnemonic.letter == selectedLetter
            val isExpanded = mnemonic.letter == expandedLetter

            PhonicsMnemonicCard(
                mnemonic = mnemonic,
                isFocused = isFocused,
                isExpanded = isExpanded,
                onToggleExpand = {
                    expandedLetter = if (isExpanded) null else mnemonic.letter
                    selectedLetter = mnemonic.letter
                },
                onLetterSpeak = { onLetterSpeak?.invoke(mnemonic.letter) },
                onWordSpeak = onWordSpeak,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
            )
        }

        // Optional Quick Discrimination Quiz Section
        if (showQuizSection) {
            Spacer(modifier = Modifier.height(8.dp))
            PhonicsMnemonicMiniQuiz(
                mnemonics = mnemonics,
                onCorrectAnswer = { letter -> onLetterSpeak?.invoke(letter) }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PhonicsMnemonicCard(
    mnemonic: LetterMnemonic,
    isFocused: Boolean,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onLetterSpeak: () -> Unit,
    onWordSpeak: ((String) -> Unit)?,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = if (isFocused) Indigo600 else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
        label = "cardBorderColor"
    )

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onToggleExpand)
            .testTag("mnemonic_card_${mnemonic.letter}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFocused) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isFocused) 3.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = if (isFocused) 2.dp else 1.dp, color = borderColor, shape = RoundedCornerShape(20.dp))
                .padding(18.dp)
        ) {
            // Header Row: Letter badge, Title, Sound button & Expand chevron
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tactile Letter Circle Badge
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(if (isFocused) Indigo600 else MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onLetterSpeak() }
                        .testTag("mnemonic_letter_badge_${mnemonic.letter}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = mnemonic.letter.toString(),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isFocused) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = mnemonic.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = mnemonic.visualMnemonic,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Speaker Button
                IconButton(
                    onClick = onLetterSpeak,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Speak Letter",
                        tint = Indigo600
                    )
                }

                // Expand / Collapse Icon
                IconButton(
                    onClick = onToggleExpand,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Expanded Interactive Content Drawer
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(300, easing = FastOutSlowInEasing)) + fadeIn(),
                exit = shrinkVertically(animationSpec = tween(200)) + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    // Rule Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Teal600.copy(alpha = 0.10f))
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = "Rule",
                                tint = Teal600,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Rule: ${mnemonic.hintRule}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Teal600
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Memory Gesture & Trick
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Amber500.copy(alpha = 0.12f))
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PanTool,
                            contentDescription = "Hand trick",
                            tint = Amber500,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Hand Trick: ${mnemonic.memoryTrick}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Example Words with Tap-to-Speak Chips
                    Text(
                        text = "Practice Words (Tap to hear):",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val wordsList = mnemonic.exampleWord.split(",").map { it.trim() }
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        wordsList.forEach { word ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Indigo600.copy(alpha = 0.12f))
                                    .clickable { onWordSpeak?.invoke(word) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = null,
                                        tint = Indigo600,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = word,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Indigo600
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PhonicsMnemonicMiniQuiz(
    mnemonics: List<LetterMnemonic>,
    onCorrectAnswer: (Char) -> Unit,
    modifier: Modifier = Modifier
) {
    var quizIndex by remember { mutableIntStateOf(0) }
    var feedbackText by remember { mutableStateOf<String?>(null) }
    var score by remember { mutableIntStateOf(0) }

    val quizItems = remember(mnemonics) {
        listOf(
            Pair("Which letter has the bat first, then the ball?", 'b'),
            Pair("Which letter has a parachute popping to the right?", 'p'),
            Pair("Which letter forms the footboard on the right in 'bed'?", 'd'),
            Pair("Which letter bows to the Queen on the left?", 'q'),
            Pair("Which letter looks like two mountain peaks?", 'm')
        )
    }

    val currentQuiz = quizItems[quizIndex % quizItems.size]

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .testTag("phonics_quiz_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Indigo600,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Letter Direction Challenge",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Text(
                    text = "Score: $score",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Indigo600
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = currentQuiz.first,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Choice options
            val options = listOf('b', 'd', 'p', 'q')
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                options.forEach { option ->
                    Button(
                        onClick = {
                            if (option == currentQuiz.second) {
                                score += 10
                                feedbackText = "🎉 Super! '${option}' is the correct match!"
                                onCorrectAnswer(option)
                                quizIndex++
                            } else {
                                feedbackText = "Look closely at the direction and try again!"
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("quiz_letter_btn_$option"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = option.toString(),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Indigo600
                        )
                    }
                }
            }

            if (feedbackText != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = feedbackText!!,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (feedbackText!!.startsWith("🎉")) LeafGreen else Amber500
                )
            }
        }
    }
}
