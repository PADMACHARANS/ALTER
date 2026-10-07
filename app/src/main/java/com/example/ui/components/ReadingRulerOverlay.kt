package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun ReadingRulerOverlay(
    isEnabled: Boolean,
    heightDp: Int = 48,
    rulerColorHex: String = "#FEF08A",
    modifier: Modifier = Modifier
) {
    if (!isEnabled) return

    var offsetY by remember { mutableFloatStateOf(280f) }

    val baseColor = try {
        Color(android.graphics.Color.parseColor(rulerColorHex))
    } catch (e: Exception) {
        Color(0xFFFEF08A)
    }

    // Ruler highlight band with high visibility edges and semi-transparent center
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(0, offsetY.roundToInt()) }
                .fillMaxWidth()
                .height(heightDp.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(baseColor.copy(alpha = 0.35f))
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetY = (offsetY + dragAmount.y).coerceIn(80f, 1800f)
                    }
                }
        ) {
            // Subtle top and bottom tracking lines
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(baseColor.copy(alpha = 0.85f))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .align(androidx.compose.ui.Alignment.BottomCenter)
                    .background(baseColor.copy(alpha = 0.85f))
            )
        }
    }
}
