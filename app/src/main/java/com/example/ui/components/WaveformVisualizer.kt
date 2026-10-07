package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.Indigo500
import com.example.ui.theme.Teal400

@Composable
fun WaveformVisualizer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 7
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")

    val heights = (0 until barCount).map { index ->
        val duration = 400 + (index * 120)
        val height by infiniteTransition.animateFloat(
            initialValue = 8f,
            targetValue = if (isPlaying) (16f + (index % 4) * 8f) else 8f,
            animationSpec = infiniteRepeatable(
                animation = tween(duration, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar_$index"
        )
        height
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        heights.forEachIndexed { i, h ->
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(h.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (i % 2 == 0) Indigo500 else Teal400)
            )
        }
    }
}
