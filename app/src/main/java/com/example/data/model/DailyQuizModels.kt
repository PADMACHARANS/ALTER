package com.example.data.model

data class DailyQuizQuestion(
    val id: Int,
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val category: String,
    val mnemonicHint: String? = null
)

data class QuestionPerformance(
    val questionId: Int,
    val selectedIndex: Int,
    val isCorrect: Boolean,
    val category: String
)

data class DailyQuizUiState(
    val questions: List<DailyQuizQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val userAnswers: Map<Int, Int> = emptyMap(),
    val isAnswerRevealed: Boolean = false,
    val correctAnswersCount: Int = 0,
    val totalAttempted: Int = 0,
    val accuracyPercentage: Float = 0f,
    val currentStreakDays: Int = 0,
    val isStreakReset: Boolean = false,
    val streakStatusMessage: String = "",
    val isCompleted: Boolean = false,
    val earnedXp: Int = 0,
    val performanceRating: String = "Ready"
)
