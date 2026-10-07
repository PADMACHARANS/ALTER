package com.example.data.model

enum class PracticeCategory {
    WORD_BUILDER,
    SOUND_STEPS,
    READ_ALONG
}

data class WordBuilderPuzzle(
    val id: String,
    val targetWord: String,
    val scrambledLetters: List<String>,
    val hint: String,
    val category: String,
    val phonemes: List<String>,
    val explanation: String
)

data class SoundStepExercise(
    val id: String,
    val fullWord: String,
    val soundSegments: List<String>, // e.g. ["/k/", "/æ/", "/t/"]
    val stepDescriptions: List<String>,
    val visualClue: String,
    val blendSpeedMs: Long = 600L
)

data class ReadAlongItem(
    val id: String,
    val title: String,
    val sentences: List<String>,
    val wordTokens: List<String>,
    val readingTip: String
)
