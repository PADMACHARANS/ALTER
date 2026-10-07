package com.example.data.model

data class AccessibilityPreferences(
    val fontScale: Float = 1.0f,
    val letterSpacingSp: Float = 0.5f,
    val lineSpacingMultiplier: Float = 1.5f,
    val selectedFont: String = "OpenDyslexic", // "OpenDyslexic", "CleanSans", "Serif", "Monospace"
    val readingRulerEnabled: Boolean = false,
    val rulerHeightDp: Int = 44,
    val rulerColorHex: String = "#FEF08A", // Soft translucent yellow
    val backgroundTint: BackgroundTint = BackgroundTint.NONE,
    val bionicReadingEnabled: Boolean = false,
    val soundEffectsEnabled: Boolean = true,
    val speechRate: Float = 0.9f,
    val speechPitch: Float = 1.0f,
    val highContrastMode: Boolean = false
)

enum class BackgroundTint(val displayName: String, val hexColor: Long) {
    NONE("Original", 0x00000000),
    CREAM("Warm Cream", 0xFFFAF7EE),
    PASTEL_YELLOW("Soft Yellow", 0xFFFEFCE8),
    SOFT_BLUE("Calm Sky", 0xFFF0F9FF),
    MINT("Gentle Mint", 0xFFF0FDF4),
    PEACH("Warm Peach", 0xFFFFF7ED),
    ROSE("Soft Rose", 0xFFFFF1F2),
    DARK_COMFORT("Dark Velvet", 0xFF0F172A)
}
