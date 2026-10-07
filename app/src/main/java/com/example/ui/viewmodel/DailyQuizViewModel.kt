package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.data.db.UserDao
import com.example.data.model.DailyQuizQuestion
import com.example.data.model.DailyQuizUiState
import com.example.data.model.LessonProgressEntity
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

/**
 * DailyQuizViewModel manages daily literacy and phonics quizzes,
 * tracks granular user performance metrics, computes real-time accuracy percentages,
 * and maintains / resets the reading streak state upon login as requested.
 */
class DailyQuizViewModel(
    private val userDao: UserDao,
    private val soundManager: SoundManager? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(DailyQuizUiState())
    val uiState: StateFlow<DailyQuizUiState> = _uiState.asStateFlow()

    // Curated Daily Literacy & Dyslexia Assessment Questions
    val defaultDailyQuestions = listOf(
        DailyQuizQuestion(
            id = 1,
            prompt = "In the 'Bed Rule' mnemonic, which letter is the headboard on the left?",
            options = listOf("Letter 'b'", "Letter 'd'", "Letter 'p'", "Letter 'q'"),
            correctIndex = 0,
            explanation = "'b' has its stem first on the left, forming the headboard of the word 'bed'.",
            category = "Letter Reversal",
            mnemonicHint = "Think of the word 'bed' — headboard is on the left ('b')!"
        ),
        DailyQuizQuestion(
            id = 2,
            prompt = "Which letter points right like a parachute floating in the air?",
            options = listOf("Letter 'q'", "Letter 'p'", "Letter 'd'", "Letter 'b'"),
            correctIndex = 1,
            explanation = "'p' has a circle popping to the right side like a parachute.",
            category = "Letter Reversal",
            mnemonicHint = "P = Parachute popping to the right!"
        ),
        DailyQuizQuestion(
            id = 3,
            prompt = "Blend these phoneme sounds together: /c/ + /a/ + /t/",
            options = listOf("Cot", "Cut", "Cat", "Cap"),
            correctIndex = 2,
            explanation = "Blending the sounds /k/ + /æ/ + /t/ creates the word 'Cat'.",
            category = "Phoneme Blending",
            mnemonicHint = "Say the sounds smoothly: k-a-t -> Cat!"
        ),
        DailyQuizQuestion(
            id = 4,
            prompt = "How many syllables are in the word 'FAN-TAS-TIC'?",
            options = listOf("1 syllable", "2 syllables", "3 syllables", "4 syllables"),
            correctIndex = 2,
            explanation = "'Fantastic' has 3 chunks: fan - tas - tic.",
            category = "Syllable Awareness",
            mnemonicHint = "Clap your hands for each beat: fan (clap) tas (clap) tic (clap)!"
        ),
        DailyQuizQuestion(
            id = 5,
            prompt = "Which letter looks like two mountain peaks pointing upward?",
            options = listOf("Letter 'w'", "Letter 'm'", "Letter 'n'", "Letter 'v'"),
            correctIndex = 1,
            explanation = "'m' reaches up with two summits like mountains.",
            category = "Visual Memory",
            mnemonicHint = "M = Mountain peaks (up)!"
        )
    )

    init {
        loadQuestions()
        observeUserProfileStreak()
    }

    private fun loadQuestions() {
        _uiState.update { current ->
            current.copy(
                questions = defaultDailyQuestions,
                currentQuestionIndex = 0,
                userAnswers = emptyMap(),
                correctAnswersCount = 0,
                totalAttempted = 0,
                accuracyPercentage = 0f,
                isCompleted = false
            )
        }
    }

    private fun observeUserProfileStreak() {
        viewModelScope.launch {
            userDao.getUserProfile().collect { profile ->
                if (profile != null) {
                    val defaultMsg = if (profile.streakDays == 0) {
                        "Welcome! Complete today's quiz to start your 1-day reading streak! 🔥"
                    } else {
                        "Welcome back! Your ${profile.streakDays}-day streak is active."
                    }
                    _uiState.update { current ->
                        current.copy(
                            currentStreakDays = profile.streakDays,
                            streakStatusMessage = if (current.streakStatusMessage.isBlank()) defaultMsg else current.streakStatusMessage
                        )
                    }
                }
            }
        }
    }

    /**
     * Resets or verifies the reading streak state upon user login.
     * Accurately distinguishes between brand new users (streak 0) and returning active users.
     */
    suspend fun resetReadingStreakUponLoginSuspend(forceReset: Boolean = false) {
        val currentProfile = userDao.getUserProfile().firstOrNull() ?: UserProfile()
        val now = System.currentTimeMillis()
        val lastActive = currentProfile.lastActiveTimestamp

        val diffMillis = now - lastActive
        val diffDays = TimeUnit.MILLISECONDS.toDays(diffMillis)

        val isNewUser = currentProfile.streakDays == 0 && currentProfile.totalLessonsCompleted == 0
        val isStreakBroken = diffDays > 1 && !isNewUser

        if (forceReset || isStreakBroken) {
            val updatedProfile = currentProfile.copy(
                streakDays = 0,
                lastActiveTimestamp = now
            )
            userDao.saveUserProfile(updatedProfile)

            val message = if (isNewUser && !forceReset) {
                "Welcome! Complete today's quiz or lesson to begin your reading streak! 🔥"
            } else {
                "Reading streak reset upon login. Complete today's quiz to start fresh!"
            }

            _uiState.update { state ->
                state.copy(
                    currentStreakDays = 0,
                    isStreakReset = true,
                    streakStatusMessage = message
                )
            }
            soundManager?.playToneClick()
        } else {
            val message = if (currentProfile.streakDays == 0) {
                "Welcome! Complete today's quiz or lesson to start your 1-day reading streak! 🔥"
            } else {
                "Welcome back! Your ${currentProfile.streakDays}-day streak is active."
            }
            _uiState.update { state ->
                state.copy(
                    currentStreakDays = currentProfile.streakDays,
                    isStreakReset = false,
                    streakStatusMessage = message
                )
            }
        }
    }

    fun resetReadingStreakUponLogin(forceReset: Boolean = false) {
        viewModelScope.launch {
            resetReadingStreakUponLoginSuspend(forceReset)
        }
    }

    /**
     * Submit an answer for a specific question, update performance stats,
     * calculate the new accuracy percentage, and provide audio feedback.
     */
    fun submitAnswer(questionId: Int, selectedOptionIndex: Int) {
        val questions = _uiState.value.questions
        val question = questions.find { it.id == questionId } ?: return

        val isCorrect = question.correctIndex == selectedOptionIndex
        val updatedAnswers = _uiState.value.userAnswers.toMutableMap()
        updatedAnswers[questionId] = selectedOptionIndex

        val correctCount = questions.count { q ->
            updatedAnswers[q.id] == q.correctIndex
        }
        val attemptedCount = updatedAnswers.size
        val accuracy = calculateAccuracyPercentage(correctCount, attemptedCount)

        if (isCorrect) {
            soundManager?.playToneSuccess()
        } else {
            soundManager?.playToneClick()
        }

        _uiState.update { state ->
            state.copy(
                userAnswers = updatedAnswers,
                isAnswerRevealed = true,
                correctAnswersCount = correctCount,
                totalAttempted = attemptedCount,
                accuracyPercentage = accuracy,
                performanceRating = evaluatePerformanceRating(accuracy)
            )
        }
    }

    /**
     * Calculate accuracy percentage rounded to 1 decimal place.
     */
    fun calculateAccuracyPercentage(correctCount: Int, totalAttempted: Int): Float {
        if (totalAttempted <= 0) return 0f
        val raw = (correctCount.toFloat() / totalAttempted.toFloat()) * 100f
        return (raw * 10f).roundToInt() / 10f
    }

    /**
     * Advance to the next question in the daily assessment.
     */
    fun nextQuestion() {
        soundManager?.playToneClick()
        val currentIdx = _uiState.value.currentQuestionIndex
        val maxIdx = _uiState.value.questions.size - 1

        if (currentIdx < maxIdx) {
            _uiState.update { it.copy(currentQuestionIndex = currentIdx + 1, isAnswerRevealed = false) }
        } else {
            finishQuiz()
        }
    }

    /**
     * Move back to previous question.
     */
    fun previousQuestion() {
        soundManager?.playToneClick()
        val currentIdx = _uiState.value.currentQuestionIndex
        if (currentIdx > 0) {
            _uiState.update { it.copy(currentQuestionIndex = currentIdx - 1, isAnswerRevealed = true) }
        }
    }

    /**
     * Complete the daily quiz:
     * - Records user accuracy in progress history
     * - If accuracy >= 60%, increments user streak by 1 and awards XP
     * - Saves progress to Room DB
     */
    fun finishQuiz() {
        val state = _uiState.value
        val accuracy = state.accuracyPercentage
        val isPassed = accuracy >= 60f
        val earnedXp = (state.correctAnswersCount * 20) + (if (isPassed) 50 else 10)

        viewModelScope.launch {
            val profile = userDao.getUserProfile().firstOrNull() ?: UserProfile()
            val newStreak = if (isPassed) profile.streakDays + 1 else profile.streakDays
            val newXp = profile.currentXp + earnedXp
            val newLevel = (newXp / 200) + 1

            val updatedProfile = profile.copy(
                streakDays = newStreak,
                currentXp = newXp,
                level = newLevel,
                totalLessonsCompleted = profile.totalLessonsCompleted + 1,
                lastActiveTimestamp = System.currentTimeMillis()
            )
            userDao.saveUserProfile(updatedProfile)

            // Record lesson progress
            userDao.insertLessonProgress(
                LessonProgressEntity(
                    lessonId = "daily_quiz_${System.currentTimeMillis()}",
                    category = "Daily Assessment",
                    scorePercent = accuracy.toInt(),
                    completedAt = System.currentTimeMillis()
                )
            )

            _uiState.update { current ->
                current.copy(
                    isCompleted = true,
                    currentStreakDays = newStreak,
                    earnedXp = earnedXp,
                    performanceRating = evaluatePerformanceRating(accuracy)
                )
            }

            soundManager?.playToneFanfare()
        }
    }

    /**
     * Restart the daily quiz for retry.
     */
    fun retryQuiz() {
        soundManager?.playToneClick()
        loadQuestions()
    }

    private fun evaluatePerformanceRating(accuracy: Float): String {
        return when {
            accuracy >= 90f -> "Master Reader 🌟"
            accuracy >= 75f -> "Great Progress 🎯"
            accuracy >= 50f -> "Keep Practicing 📖"
            else -> "Needs Review 💡"
        }
    }
}
