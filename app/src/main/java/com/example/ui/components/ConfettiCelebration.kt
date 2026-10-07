package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

data class ConfettiParticle(
    val startX: Float,
    val speedY: Float,
    val size: Float,
    val color: Color
)

@Composable
fun ConfettiCelebration(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    if (!isActive) return

    val progress = remember { Animatable(0f) }
    val particles = remember {
        val colors = listOf(
            Color(0xFF6366F1), Color(0xFF0D9488), Color(0xFFF59E0B),
            Color(0xFFEC4899), Color(0xFF3B82F6), Color(0xFF10B981)
        )
        List(60) {
            ConfettiParticle(
                startX = Random.nextFloat(),
                speedY = 0.5f + Random.nextFloat() * 0.8f,
                size = 10f + Random.nextFloat() * 12f,
                color = colors[Random.nextInt(colors.size)]
            )
        }
    }

    LaunchedEffect(isActive) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2400, easing = LinearEasing)
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        particles.forEach { p ->
            val currentY = (progress.value * canvasHeight * p.speedY) % canvasHeight
            val currentX = (p.startX * canvasWidth) + (kotlin.math.sin(progress.value * 10f + p.startX * 20f) * 30f)
            drawRect(
                color = p.color.copy(alpha = (1f - progress.value * 0.4f).coerceIn(0f, 1f)),
                topLeft = Offset(currentX, currentY),
                size = Size(p.size, p.size * 0.6f)
            )
        }
    }
}
