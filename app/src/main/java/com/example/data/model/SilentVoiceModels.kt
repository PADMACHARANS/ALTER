package com.example.data.model

enum class AACCategory(val title: String, val icon: String, val colorHex: Long) {
    URGENT("Urgent & Help", "Warning", 0xFFEF4444),
    NEEDS("Basic Needs", "LocalCafe", 0xFF0D9488),
    FEELINGS("Emotions & Feelings", "Mood", 0xFF8B5CF6),
    PEOPLE("People & Places", "People", 0xFF3B82F6),
    ACTIVITIES("Activities & School", "School", 0xFFF59E0B),
    RESPONSES("Quick Responses", "ThumbUp", 0xFF10B981)
}

data class AACCard(
    val id: String,
    val title: String,
    val speechPhrase: String,
    val category: AACCategory,
    val symbolEmoji: String,
    val priority: Boolean = false
)

data class AACSentenceToken(
    val id: Long = System.nanoTime(),
    val text: String,
    val emoji: String
)
