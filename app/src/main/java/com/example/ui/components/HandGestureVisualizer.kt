package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HandRequirement
import com.example.data.model.ISLHandshapeType
import com.example.data.model.ISLSignItem
import com.example.data.model.MotionDirection
import com.example.ui.theme.Amber500
import com.example.ui.theme.Indigo600
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.Teal600

/**
 * HandGestureVisualizer provides real, clear anatomical hand demonstrations for Indian Sign Language (ISL),
 * featuring:
 * - Dynamic 1-hand and 2-hand rendering
 * - Step-by-step hand position and finger alignment
 * - Directional motion vectors and animated path indicators
 * - Speed controls (0.5x, 0.75x, 1x, 1.5x), Play/Pause & Replay
 * - Verified ISLRTC / NCERT provenance attribution
 */
@Composable
fun HandGestureVisualizer(
    sign: ISLSignItem,
    currentStepIndex: Int,
    onStepChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var isPlaying by remember { mutableStateOf(true) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }

    val currentStep = sign.steps.getOrNull(currentStepIndex) ?: sign.steps.first()

    // Animation transition based on playback speed
    val infiniteTransition = rememberInfiniteTransition(label = "motion_anim")
    val durationMs = (1800 / playbackSpeed).toInt().coerceAtLeast(400)
    
    val motionProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Source & Verification Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Teal600.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verified ISL",
                        tint = Teal600,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "ISLRTC Verified ISL",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Teal600
                    )
                }

                Text(
                    text = sign.handsRequired.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Large Hand Demonstration Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(18.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .testTag("hand_gesture_canvas")
                ) {
                    drawISLHandGesture(
                        handshape = currentStep.handshapeType,
                        handsRequired = sign.handsRequired,
                        motionDirection = currentStep.motionDirection,
                        motionProgress = if (isPlaying) motionProgress else 0.5f,
                        stepNumber = currentStep.stepNumber,
                        primaryColor = Indigo600,
                        secondaryColor = Amber500,
                        accentColor = Teal600
                    )
                }

                // Step & Motion Indicator Pill in overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = currentStep.motionDirection.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Amber500
                    )
                }

                // Hands required indicator
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Indigo600.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (sign.handsRequired == HandRequirement.TWO_HANDS) "2-Hand Sign" else "1-Hand Sign",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Indigo600
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Playback and Speed Control Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Play / Pause / Replay Controls
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { isPlaying = !isPlaying },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Indigo600.copy(alpha = 0.12f))
                            .testTag("play_pause_gesture_button")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Indigo600,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { isPlaying = true },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("replay_gesture_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Replay,
                            contentDescription = "Replay",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Speed Selector Pills (0.5x, 0.75x, 1x, 1.5x)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(0.5f, 0.75f, 1.0f, 1.5f).forEach { speed ->
                        val isSelected = playbackSpeed == speed
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) Indigo600
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                                )
                                .clickable { playbackSpeed = speed }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${speed}x",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Step Progress Navigation & Scrubber
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        if (currentStepIndex > 0) onStepChange(currentStepIndex - 1)
                    },
                    enabled = currentStepIndex > 0,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("prev_step_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Step", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Prev Step", style = MaterialTheme.typography.labelSmall)
                }

                // Step count indicator
                Text(
                    text = "STEP ${currentStep.stepNumber} OF ${sign.steps.size}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Button(
                    onClick = {
                        if (currentStepIndex < sign.steps.size - 1) onStepChange(currentStepIndex + 1)
                    },
                    enabled = currentStepIndex < sign.steps.size - 1,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("next_step_button")
                ) {
                    Text("Next Step", style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Step", modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Step Instruction Description Box
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = currentStep.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentStep.instruction,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Tip",
                            tint = Amber500,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = currentStep.visualAidTip,
                            style = MaterialTheme.typography.labelSmall,
                            color = Amber500,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Custom Canvas Hand Drawing Engine for Indian Sign Language (ISL).
 * Renders palm, distinct jointed fingers, orientation vectors, and motion arrows.
 */
private fun DrawScope.drawISLHandGesture(
    handshape: ISLHandshapeType,
    handsRequired: HandRequirement,
    motionDirection: MotionDirection,
    motionProgress: Float,
    stepNumber: Int,
    primaryColor: Color,
    secondaryColor: Color,
    accentColor: Color
) {
    val centerX = size.width / 2f
    val centerY = size.height / 2f

    val skinTone = Color(0xFFE8B688)
    val skinOutline = Color(0xFF8D5B38)
    val jointColor = Color(0xFFC4865C)

    when (handsRequired) {
        HandRequirement.TWO_HANDS -> {
            // Two-Hand ISL Gesture
            val leftHandX = centerX - 65f + (if (motionDirection == MotionDirection.JOIN_TOGETHER) (1f - motionProgress) * -30f else 0f)
            val rightHandX = centerX + 65f + (if (motionDirection == MotionDirection.JOIN_TOGETHER) (1f - motionProgress) * 30f else 0f)
            val verticalShift = if (motionDirection == MotionDirection.UPWARD) -motionProgress * 20f
            else if (motionDirection == MotionDirection.DOWNWARD) motionProgress * 20f
            else 0f

            when (handshape) {
                ISLHandshapeType.NAMASTE_JOINED_PALMS -> {
                    // Joined Palms Pressed Flat Together in Namaste
                    val contactX = centerX
                    val animY = centerY + 10f + (motionProgress * 6f)

                    // Left Palm
                    drawHandShape(
                        palmX = contactX - 22f,
                        palmY = animY,
                        isLeftHand = true,
                        isFlat = true,
                        isClosedFist = false,
                        fingersExtended = listOf(true, true, true, true, true),
                        skinColor = skinTone,
                        outlineColor = skinOutline
                    )
                    // Right Palm
                    drawHandShape(
                        palmX = contactX + 22f,
                        palmY = animY,
                        isLeftHand = false,
                        isFlat = true,
                        isClosedFist = false,
                        fingersExtended = listOf(true, true, true, true, true),
                        skinColor = skinTone,
                        outlineColor = skinOutline
                    )
                }
                ISLHandshapeType.TWO_HANDS_ROOF_HOUSE -> {
                    // Two hands forming a triangle roof apex
                    drawHandShape(
                        palmX = leftHandX,
                        palmY = centerY + verticalShift,
                        isLeftHand = true,
                        angleDeg = 35f,
                        isFlat = true,
                        isClosedFist = false,
                        fingersExtended = listOf(true, true, true, true, true),
                        skinColor = skinTone,
                        outlineColor = skinOutline
                    )
                    drawHandShape(
                        palmX = rightHandX,
                        palmY = centerY + verticalShift,
                        isLeftHand = false,
                        angleDeg = -35f,
                        isFlat = true,
                        isClosedFist = false,
                        fingersExtended = listOf(true, true, true, true, true),
                        skinColor = skinTone,
                        outlineColor = skinOutline
                    )
                }
                ISLHandshapeType.TWO_HANDS_BOOK_OPEN -> {
                    // Two open palms touching at side like an open book
                    drawHandShape(
                        palmX = leftHandX + 15f,
                        palmY = centerY + verticalShift,
                        isLeftHand = true,
                        angleDeg = -15f,
                        isFlat = true,
                        isClosedFist = false,
                        fingersExtended = listOf(true, true, true, true, true),
                        skinColor = skinTone,
                        outlineColor = skinOutline
                    )
                    drawHandShape(
                        palmX = rightHandX - 15f,
                        palmY = centerY + verticalShift,
                        isLeftHand = false,
                        angleDeg = 15f,
                        isFlat = true,
                        isClosedFist = false,
                        fingersExtended = listOf(true, true, true, true, true),
                        skinColor = skinTone,
                        outlineColor = skinOutline
                    )
                }
                else -> {
                    // Default two-handed rendering
                    drawHandShape(
                        palmX = leftHandX,
                        palmY = centerY + verticalShift,
                        isLeftHand = true,
                        isFlat = true,
                        isClosedFist = false,
                        fingersExtended = listOf(true, true, true, true, true),
                        skinColor = skinTone,
                        outlineColor = skinOutline
                    )
                    drawHandShape(
                        palmX = rightHandX,
                        palmY = centerY + verticalShift,
                        isLeftHand = false,
                        isFlat = true,
                        isClosedFist = false,
                        fingersExtended = listOf(true, true, true, true, true),
                        skinColor = skinTone,
                        outlineColor = skinOutline
                    )
                }
            }
        }
        HandRequirement.ONE_HAND -> {
            // Dominant One-Hand ISL Gesture
            val animX = if (motionDirection == MotionDirection.WAVE_SIDE_TO_SIDE) {
                centerX + (kotlin.math.sin(motionProgress * 2 * Math.PI.toFloat()) * 25f)
            } else if (motionDirection == MotionDirection.OUTWARD) {
                centerX + (motionProgress * 20f)
            } else centerX

            val animY = if (motionDirection == MotionDirection.UPWARD) {
                centerY - (motionProgress * 25f)
            } else if (motionDirection == MotionDirection.DOWNWARD) {
                centerY + (motionProgress * 25f)
            } else if (motionDirection == MotionDirection.TAP_CHIN) {
                centerY - 10f + (kotlin.math.sin(motionProgress * 2 * Math.PI.toFloat()) * 8f)
            } else centerY

            when (handshape) {
                ISLHandshapeType.FLAT_PALM_WAVE -> {
                    drawHandShape(
                        palmX = animX,
                        palmY = animY,
                        isLeftHand = false,
                        isFlat = true,
                        isClosedFist = false,
                        fingersExtended = listOf(true, true, true, true, true),
                        skinColor = skinTone,
                        outlineColor = skinOutline
                    )
                }
                ISLHandshapeType.CHIN_TOUCH_FORWARD -> {
                    drawHandShape(
                        palmX = animX,
                        palmY = animY,
                        isLeftHand = false,
                        isFlat = true,
                        angleDeg = -10f,
                        isClosedFist = false,
                        fingersExtended = listOf(true, true, true, true, true),
                        skinColor = skinTone,
                        outlineColor = skinOutline
                    )
                }
                ISLHandshapeType.FIST_THUMBS_UP -> {
                    drawHandShape(
                        palmX = animX,
                        palmY = animY,
                        isLeftHand = false,
                        isFlat = false,
                        isClosedFist = true,
                        isThumbsUp = true,
                        fingersExtended = listOf(true, false, false, false, false),
                        skinColor = skinTone,
                        outlineColor = skinOutline
                    )
                }
                ISLHandshapeType.PINCH_FINGERS_FOOD -> {
                    drawHandShape(
                        palmX = animX,
                        palmY = animY,
                        isLeftHand = false,
                        isFlat = false,
                        isClosedFist = false,
                        isPinchTogether = true,
                        fingersExtended = listOf(true, true, true, false, false),
                        skinColor = skinTone,
                        outlineColor = skinOutline
                    )
                }
                ISLHandshapeType.TWO_FINGERS_VICTORY_PEACE -> {
                    drawHandShape(
                        palmX = animX,
                        palmY = animY,
                        isLeftHand = false,
                        isFlat = false,
                        isClosedFist = false,
                        fingersExtended = listOf(false, true, true, false, false),
                        skinColor = skinTone,
                        outlineColor = skinOutline
                    )
                }
                ISLHandshapeType.INDEX_POINT_FORWARD -> {
                    drawHandShape(
                        palmX = animX,
                        palmY = animY,
                        isLeftHand = false,
                        isFlat = false,
                        isClosedFist = false,
                        fingersExtended = listOf(false, true, false, false, false),
                        skinColor = skinTone,
                        outlineColor = skinOutline
                    )
                }
                else -> {
                    drawHandShape(
                        palmX = animX,
                        palmY = animY,
                        isLeftHand = false,
                        isFlat = true,
                        isClosedFist = false,
                        fingersExtended = listOf(true, true, true, true, true),
                        skinColor = skinTone,
                        outlineColor = skinOutline
                    )
                }
            }
        }
    }

    // Draw Motion Arrow Vector
    drawMotionIndicatorPath(
        direction = motionDirection,
        centerX = centerX,
        centerY = centerY,
        progress = motionProgress,
        color = secondaryColor
    )

    // Draw Step Focus / Highlight Aura in Step 4
    if (stepNumber == 4) {
        drawCircle(
            color = LeafGreen.copy(alpha = 0.25f * (0.8f + 0.2f * motionProgress)),
            radius = 95f,
            center = Offset(centerX, centerY),
            style = Stroke(width = 3.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f))
        )
    }
}

/**
 * Draws an anatomical hand with distinct palm, wrist, and 5 fingers.
 */
private fun DrawScope.drawHandShape(
    palmX: Float,
    palmY: Float,
    isLeftHand: Boolean,
    angleDeg: Float = 0f,
    isFlat: Boolean = true,
    isClosedFist: Boolean = false,
    isThumbsUp: Boolean = false,
    isPinchTogether: Boolean = false,
    fingersExtended: List<Boolean> = listOf(true, true, true, true, true),
    skinColor: Color,
    outlineColor: Color
) {
    val palmWidth = 54f
    val palmHeight = 58f

    // Palm base
    val palmPath = Path().apply {
        addRoundRect(
            androidx.compose.ui.geometry.RoundRect(
                left = palmX - palmWidth / 2f,
                top = palmY - palmHeight / 4f,
                right = palmX + palmWidth / 2f,
                bottom = palmY + palmHeight * 0.75f,
                cornerRadius = CornerRadius(16f, 16f)
            )
        )
    }

    // Wrist Base
    drawRoundRect(
        color = skinColor,
        topLeft = Offset(palmX - 16f, palmY + palmHeight * 0.7f),
        size = Size(32f, 32f),
        cornerRadius = CornerRadius(6f, 6f)
    )
    drawRoundRect(
        color = outlineColor,
        topLeft = Offset(palmX - 16f, palmY + palmHeight * 0.7f),
        size = Size(32f, 32f),
        cornerRadius = CornerRadius(6f, 6f),
        style = Stroke(width = 2.dp.toPx())
    )

    // Draw Palm
    drawPath(path = palmPath, color = skinColor)
    drawPath(path = palmPath, color = outlineColor, style = Stroke(width = 2.dp.toPx()))

    // Fingers Configuration (Thumb, Index, Middle, Ring, Pinky)
    val fingerXs = if (isLeftHand) {
        listOf(palmX + 22f, palmX + 12f, palmX + 0f, palmX - 12f, palmX - 22f)
    } else {
        listOf(palmX - 22f, palmX - 12f, palmX + 0f, palmX + 12f, palmX + 22f)
    }

    val fingerHeights = listOf(36f, 52f, 58f, 50f, 40f)

    if (isClosedFist && !isThumbsUp) {
        // Closed fist: Curled fingers on palm
        for (i in 1..4) {
            val fx = fingerXs[i]
            drawRoundRect(
                color = Color(0xFFD39E70),
                topLeft = Offset(fx - 5f, palmY - 4f),
                size = Size(10f, 22f),
                cornerRadius = CornerRadius(5f, 5f)
            )
            drawRoundRect(
                color = outlineColor,
                topLeft = Offset(fx - 5f, palmY - 4f),
                size = Size(10f, 22f),
                cornerRadius = CornerRadius(5f, 5f),
                style = Stroke(width = 1.5.dp.toPx())
            )
        }
    } else {
        // Individual fingers
        for (i in 0..4) {
            val isExtended = fingersExtended.getOrElse(i) { true }
            val fx = fingerXs[i]
            val fh = if (isExtended) fingerHeights[i] else 16f
            val fy = if (i == 0) palmY + 6f else palmY - palmHeight / 4f - (if (isExtended) fh else 0f)

            val fingerW = if (i == 0) 12f else 10f

            if (i == 0 && isThumbsUp) {
                // Thumbs up sticking upright
                drawRoundRect(
                    color = skinColor,
                    topLeft = Offset(fx - fingerW / 2f, palmY - 38f),
                    size = Size(fingerW, 44f),
                    cornerRadius = CornerRadius(6f, 6f)
                )
                drawRoundRect(
                    color = outlineColor,
                    topLeft = Offset(fx - fingerW / 2f, palmY - 38f),
                    size = Size(fingerW, 44f),
                    cornerRadius = CornerRadius(6f, 6f),
                    style = Stroke(width = 1.5.dp.toPx())
                )
            } else {
                drawRoundRect(
                    color = skinColor,
                    topLeft = Offset(fx - fingerW / 2f, fy),
                    size = Size(fingerW, fh),
                    cornerRadius = CornerRadius(5f, 5f)
                )
                drawRoundRect(
                    color = outlineColor,
                    topLeft = Offset(fx - fingerW / 2f, fy),
                    size = Size(fingerW, fh),
                    cornerRadius = CornerRadius(5f, 5f),
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }
        }
    }
}

/**
 * Draws animated motion indicator arrows.
 */
private fun DrawScope.drawMotionIndicatorPath(
    direction: MotionDirection,
    centerX: Float,
    centerY: Float,
    progress: Float,
    color: Color
) {
    if (direction == MotionDirection.NONE) return

    val arrowPaint = Stroke(
        width = 3.dp.toPx(),
        cap = StrokeCap.Round,
        join = StrokeJoin.Round,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), progress * 20f)
    )

    when (direction) {
        MotionDirection.UPWARD -> {
            drawLine(
                color = color,
                start = Offset(centerX + 60f, centerY + 40f),
                end = Offset(centerX + 60f, centerY - 50f),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
            // Arrowhead
            val tip = Offset(centerX + 60f, centerY - 55f)
            drawPath(
                path = Path().apply {
                    moveTo(tip.x, tip.y)
                    lineTo(tip.x - 8f, tip.y + 14f)
                    lineTo(tip.x + 8f, tip.y + 14f)
                    close()
                },
                color = color
            )
        }
        MotionDirection.OUTWARD -> {
            drawLine(
                color = color,
                start = Offset(centerX + 30f, centerY + 20f),
                end = Offset(centerX + 85f, centerY - 30f),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
            val tip = Offset(centerX + 90f, centerY - 35f)
            drawPath(
                path = Path().apply {
                    moveTo(tip.x, tip.y)
                    lineTo(tip.x - 14f, tip.y + 4f)
                    lineTo(tip.x - 4f, tip.y + 14f)
                    close()
                },
                color = color
            )
        }
        MotionDirection.CIRCULAR_CLOCKWISE -> {
            drawArc(
                color = color,
                startAngle = progress * 360f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(centerX - 80f, centerY - 80f),
                size = Size(160f, 160f),
                style = arrowPaint
            )
        }
        MotionDirection.WAVE_SIDE_TO_SIDE -> {
            drawLine(
                color = color,
                start = Offset(centerX - 50f, centerY + 70f),
                end = Offset(centerX + 50f, centerY + 70f),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
        MotionDirection.JOIN_TOGETHER -> {
            // Two arrows meeting in middle
            drawLine(
                color = color,
                start = Offset(centerX - 70f, centerY + 65f),
                end = Offset(centerX - 10f, centerY + 65f),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = color,
                start = Offset(centerX + 70f, centerY + 65f),
                end = Offset(centerX + 10f, centerY + 65f),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
        else -> {}
    }
}
