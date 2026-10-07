package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Safe Icon Registry for Indian Sign Language, Literacy & Accessibility features.
 * Validates and maps stable icon keys to avoid any runtime undefined icon errors.
 */
object IconRegistry {

    private val filledIconMap: Map<String, ImageVector> = mapOf(
        "home" to Icons.Default.Home,
        "learn" to Icons.AutoMirrored.Filled.MenuBook,
        "practice" to Icons.Default.PanTool,
        "progress" to Icons.Default.BarChart,
        "profile" to Icons.Default.Person,
        "settings" to Icons.Default.Settings,
        "quiz" to Icons.Default.Quiz,
        "camera" to Icons.Default.Videocam,
        "replay" to Icons.Default.Replay,
        "help" to Icons.AutoMirrored.Filled.Help,
        "shield" to Icons.Default.Shield,
        "family" to Icons.Default.Groups,
        "food" to Icons.Default.Restaurant,
        "education" to Icons.Default.School,
        "emotion" to Icons.Default.SentimentSatisfied,
        "emergency" to Icons.Default.Warning,
        "numbers" to Icons.Default.Pin,
        "greetings" to Icons.Default.WavingHand,
        "speed" to Icons.Default.Speed,
        "check" to Icons.Default.CheckCircle,
        "star" to Icons.Default.Star,
        "fire" to Icons.Default.LocalFireDepartment,
        "bolt" to Icons.Default.ElectricBolt,
        "arrow_back" to Icons.AutoMirrored.Filled.ArrowBack,
        "arrow_forward" to Icons.AutoMirrored.Filled.ArrowForward,
        "volume_up" to Icons.AutoMirrored.Filled.VolumeUp,
        "verified" to Icons.Default.Verified
    )

    private val outlinedIconMap: Map<String, ImageVector> = mapOf(
        "home" to Icons.Outlined.Home,
        "learn" to Icons.AutoMirrored.Filled.MenuBook,
        "practice" to Icons.Outlined.PanTool,
        "progress" to Icons.Outlined.BarChart,
        "profile" to Icons.Outlined.Person,
        "settings" to Icons.Outlined.Settings,
        "quiz" to Icons.Outlined.Quiz,
        "camera" to Icons.Outlined.Videocam,
        "replay" to Icons.Outlined.Replay,
        "help" to Icons.AutoMirrored.Filled.Help,
        "shield" to Icons.Outlined.Shield,
        "family" to Icons.Outlined.Groups,
        "food" to Icons.Outlined.Restaurant,
        "education" to Icons.Outlined.School,
        "emotion" to Icons.Outlined.SentimentSatisfied,
        "emergency" to Icons.Outlined.Warning,
        "numbers" to Icons.Outlined.Pin,
        "greetings" to Icons.Outlined.WavingHand,
        "speed" to Icons.Outlined.Speed,
        "check" to Icons.Outlined.CheckCircle,
        "star" to Icons.Outlined.Star,
        "fire" to Icons.Outlined.LocalFireDepartment,
        "bolt" to Icons.Outlined.ElectricBolt,
        "arrow_back" to Icons.AutoMirrored.Filled.ArrowBack,
        "arrow_forward" to Icons.AutoMirrored.Filled.ArrowForward,
        "volume_up" to Icons.AutoMirrored.Filled.VolumeUp,
        "verified" to Icons.Outlined.Verified
    )

    val fallbackIcon: ImageVector = Icons.Default.PanTool

    fun getIcon(key: String, selected: Boolean = true): ImageVector {
        val sanitized = key.trim().lowercase()
        return if (selected) {
            filledIconMap[sanitized] ?: outlinedIconMap[sanitized] ?: fallbackIcon
        } else {
            outlinedIconMap[sanitized] ?: filledIconMap[sanitized] ?: fallbackIcon
        }
    }
}
