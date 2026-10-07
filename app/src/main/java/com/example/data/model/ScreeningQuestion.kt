package com.example.data.model

enum class ScreenerArea(val label: String) {
    LETTER_REVERSAL("Letter Orientation & Confusions"),
    PHONOLOGICAL("Phonological Processing"),
    VISUAL_FATIGUE("Visual Crowding & Eye Fatigue"),
    READING_FLUENCY("Reading Fluency & Rhythm"),
    WORKING_MEMORY("Sequencing & Recall")
}

data class ScreeningQuestion(
    val id: Int,
    val area: ScreenerArea,
    val question: String,
    val contextExample: String,
    val promptDescription: String
)

data class ScreeningReport(
    val totalScore: Int,
    val maxScore: Int,
    val percentage: Int = if (maxScore > 0) ((totalScore.toFloat() / maxScore) * 100).toInt() else 0,
    val severityLevel: String, // "Mild Indicators", "Moderate Indicators", "Strong Indicators", "Typical"
    val areaBreakdown: Map<ScreenerArea, Int>,
    val personalizedInsights: List<String>,
    val recommendedTools: List<String>,
    val timestamp: Long = System.currentTimeMillis()
)
