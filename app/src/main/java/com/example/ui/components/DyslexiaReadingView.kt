package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AccessibilityPreferences
import com.example.data.model.BackgroundTint
import com.example.ui.theme.Amber500
import com.example.ui.theme.Indigo600
import com.example.ui.theme.Teal600
import kotlin.math.roundToInt

/**
 * DyslexiaReadingView is an accessible typography and visual reader component.
 * It applies tailored typography (font scaling, letter spacing, generous line-heights,
 * bionic reading saccade fixation), scotopic background tint filters (cream, pastel yellow,
 * soft blue, mint, dark velvet), word-level tap interactions, and an interactive draggable reading ruler.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DyslexiaReadingView(
    text: String,
    modifier: Modifier = Modifier,
    preferences: AccessibilityPreferences = AccessibilityPreferences(),
    highlightedWordIndex: Int? = null,
    onWordClick: ((word: String, index: Int) -> Unit)? = null,
    enableRuler: Boolean = preferences.readingRulerEnabled,
    rulerHeightDp: Int = preferences.rulerHeightDp,
    rulerColorHex: String = preferences.rulerColorHex,
    showFilterControls: Boolean = false,
    onPreferencesChange: ((AccessibilityPreferences) -> Unit)? = null
) {
    // Dynamic calculations for dyslexia accessibility metrics
    val baseFontSize = 18f * preferences.fontScale
    val fontSize = baseFontSize.sp
    // Generous line-height (1.4x to 2.2x base font size) to eliminate line collisions
    val calculatedLineHeight = (baseFontSize * 1.55f * (preferences.lineSpacingMultiplier / 1.5f)).sp
    val letterSpacing = preferences.letterSpacingSp.sp

    val fontFamily = when (preferences.selectedFont) {
        "Monospace" -> FontFamily.Monospace
        "Serif" -> FontFamily.Serif
        else -> FontFamily.SansSerif
    }

    // Background color filter calculation
    val backgroundColor = when (preferences.backgroundTint) {
        BackgroundTint.NONE -> MaterialTheme.colorScheme.surface
        else -> Color(preferences.backgroundTint.hexColor)
    }

    val contentTextColor = if (preferences.backgroundTint == BackgroundTint.DARK_COMFORT) {
        Color(0xFFF1F5F9)
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    // Ruler drag state
    var rulerOffsetY by remember { mutableFloatStateOf(160f) }
    val rulerColor = try {
        Color(android.graphics.Color.parseColor(rulerColorHex))
    } catch (e: Exception) {
        Color(0xFFFEF08A)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("dyslexia_reading_view")
    ) {
        // Optional quick accessibility controls toolbar
        if (showFilterControls && onPreferencesChange != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Font Scale buttons
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Size",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = {
                                val newScale = (preferences.fontScale - 0.1f).coerceAtLeast(0.8f)
                                onPreferencesChange(preferences.copy(fontScale = newScale))
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Smaller", modifier = Modifier.size(16.dp))
                        }
                        Text(
                            text = "${(preferences.fontScale * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = {
                                val newScale = (preferences.fontScale + 0.1f).coerceAtMost(1.6f)
                                onPreferencesChange(preferences.copy(fontScale = newScale))
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Larger", modifier = Modifier.size(16.dp))
                        }
                    }

                    // Reading Ruler Toggle
                    IconButton(
                        onClick = {
                            onPreferencesChange(preferences.copy(readingRulerEnabled = !preferences.readingRulerEnabled))
                        },
                        modifier = Modifier.size(32.dp)
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
                            onPreferencesChange(preferences.copy(bionicReadingEnabled = !preferences.bionicReadingEnabled))
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatBold,
                            contentDescription = "Toggle Bionic Reading",
                            tint = if (preferences.bionicReadingEnabled) Indigo600 else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Background Tint Palette
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        val availableTints = listOf(
                            BackgroundTint.NONE,
                            BackgroundTint.CREAM,
                            BackgroundTint.PASTEL_YELLOW,
                            BackgroundTint.SOFT_BLUE,
                            BackgroundTint.MINT
                        )
                        availableTints.forEach { tint ->
                            val isSelected = preferences.backgroundTint == tint
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (tint == BackgroundTint.NONE) Color.LightGray else Color(tint.hexColor)
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) Indigo600 else Color.Gray.copy(alpha = 0.5f),
                                        shape = CircleShape
                                    )
                                    .clickable { onPreferencesChange(preferences.copy(backgroundTint = tint)) }
                            )
                        }
                    }
                }
            }
        }

        // Reading View Surface with Applied Background Color Filter
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(backgroundColor)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            val words = remember(text) { text.split(Regex("\\s+")).filter { it.isNotBlank() } }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Interactive Accessible Words Layout
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    words.forEachIndexed { index, word ->
                        val isHighlighted = index == highlightedWordIndex
                        val isClickable = onWordClick != null

                        val wordBackgroundColor by animateColorAsState(
                            targetValue = when {
                                isHighlighted -> Amber500.copy(alpha = 0.38f)
                                else -> Color.Transparent
                            },
                            label = "wordHighlight"
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(wordBackgroundColor)
                                .then(
                                    if (isClickable) {
                                        Modifier.clickable { onWordClick?.invoke(word.trim('.', ',', '!', '?', ';', ':'), index) }
                                    } else Modifier
                                )
                                .padding(horizontal = 3.dp, vertical = 2.dp)
                                .testTag("reading_word_$index")
                        ) {
                            val annotatedWord = buildAnnotatedString {
                                if (preferences.bionicReadingEnabled && word.length > 1) {
                                    // Saccade Bionic Reading: Bold the initial anchor letters
                                    val boldCount = if (word.length <= 3) 1 else (word.length + 1) / 2
                                    withStyle(
                                        style = SpanStyle(
                                            fontWeight = FontWeight.Black,
                                            color = if (isHighlighted) Indigo600 else contentTextColor
                                        )
                                    ) {
                                        append(word.take(boldCount))
                                    }
                                    withStyle(
                                        style = SpanStyle(
                                            fontWeight = FontWeight.Normal,
                                            color = contentTextColor.copy(alpha = 0.88f)
                                        )
                                    ) {
                                        append(word.drop(boldCount))
                                    }
                                } else {
                                    withStyle(
                                        style = SpanStyle(
                                            fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal,
                                            color = contentTextColor
                                        )
                                    ) {
                                        append(word)
                                    }
                                }
                            }

                            Text(
                                text = annotatedWord,
                                fontSize = fontSize,
                                lineHeight = calculatedLineHeight,
                                letterSpacing = letterSpacing,
                                fontFamily = fontFamily
                            )
                        }
                    }
                }
            }

            // Draggable Reading Ruler Overlay
            if (enableRuler) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(0, rulerOffsetY.roundToInt()) }
                        .fillMaxWidth()
                        .height(rulerHeightDp.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(rulerColor.copy(alpha = 0.32f))
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                rulerOffsetY = (rulerOffsetY + dragAmount.y).coerceIn(40f, 1600f)
                            }
                        }
                        .testTag("reading_ruler_overlay")
                ) {
                    // Top guide track line
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(rulerColor.copy(alpha = 0.85f))
                    )
                    // Bottom guide track line
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .align(Alignment.BottomCenter)
                            .background(rulerColor.copy(alpha = 0.85f))
                    )
                }
            }
        }
    }
}
