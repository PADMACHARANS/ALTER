package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.data.model.AACCard
import com.example.data.model.AACSentenceToken
import com.example.data.model.AccessibilityPreferences
import com.example.data.model.BackgroundTint
import com.example.data.model.DyslexiaStory
import com.example.data.model.LetterMnemonic
import com.example.data.model.ReadAlongItem
import com.example.data.model.ScreeningQuestion
import com.example.data.model.ScreeningReport
import com.example.data.model.SignLetter
import com.example.data.model.SoundStepExercise
import com.example.data.model.SyllableWord
import com.example.data.model.UserProfile
import com.example.data.model.WordBuilderPuzzle
import com.example.data.repository.ReadEaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import android.util.Log

enum class ScreenRoute {
    AUTH,
    DASHBOARD,
    DYSLEXIA_READER,
    PHONICS_MNEMONICS,
    TEXT_SIMPLIFIER,
    PRACTICE_HUB,
    WORD_BUILDER,
    SOUND_STEPS,
    READ_ALONG,
    LESSON_COMPLETION,
    SCREENING,
    SCREENING_RESULT,
    SIGN_LANGUAGE,
    SILENT_VOICE,
    ACCESSIBILITY_SETTINGS,
    DAILY_QUIZ,
    GUIDELINES,
    PROFILE_SETUP
}

class ReadEaseViewModel(
    private val repository: ReadEaseRepository,
    val soundManager: SoundManager
) : ViewModel() {

    // User Profile
    val userProfile: StateFlow<UserProfile> = repository.userProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserProfile()
    )

    // Guest Session Flag
    private val _isGuestSession = MutableStateFlow(false)
    val isGuestSession: StateFlow<Boolean> = _isGuestSession.asStateFlow()

    // Navigation Stack
    private val _screenStack = MutableStateFlow(listOf(ScreenRoute.AUTH))
    val currentScreen: StateFlow<ScreenRoute> = MutableStateFlow(ScreenRoute.AUTH).apply {
        viewModelScope.launch {
            _screenStack.collect { stack ->
                value = stack.lastOrNull() ?: ScreenRoute.AUTH
            }
        }
    }

    init {
        // Evaluate routing dynamically upon Firebase authentication session updates
        try {
            Firebase.auth.addAuthStateListener {
                evaluateRouting()
            }
        } catch (e: Exception) {
            Log.w("ReadEaseViewModel", "Firebase auth listener init exception: ${e.message}")
        }
    }

    fun continueAsGuest() {
        soundManager.playToneClick()
        _isGuestSession.value = true
        viewModelScope.launch {
            val current = repository.userProfile.firstOrNull() ?: UserProfile()
            val updated = current.copy(
                name = if (current.name.isBlank() || current.name == "Learner") "Guest Learner" else current.name,
                avatarEmoji = current.avatarEmoji.ifBlank { "🌟" }
            )
            repository.saveUserProfile(updated)
            evaluateRouting()
        }
    }

    fun evaluateRouting() {
        val user = try { Firebase.auth.currentUser } catch (e: Exception) { null }
        val isGuest = _isGuestSession.value

        if (user == null && !isGuest) {
            val current = _screenStack.value.lastOrNull() ?: ScreenRoute.AUTH
            if (current != ScreenRoute.AUTH) {
                _screenStack.value = listOf(ScreenRoute.AUTH)
            }
            return
        }

        viewModelScope.launch {
            val profile = repository.userProfile.firstOrNull() ?: UserProfile()
            val current = _screenStack.value.lastOrNull() ?: ScreenRoute.AUTH
            if (current == ScreenRoute.AUTH || current == ScreenRoute.GUIDELINES || current == ScreenRoute.PROFILE_SETUP) {
                val stack = mutableListOf<ScreenRoute>()
                if (!profile.guidelinesAccepted) {
                    stack.add(ScreenRoute.GUIDELINES)
                } else if (!profile.onboardingCompleted) {
                    stack.add(ScreenRoute.PROFILE_SETUP)
                } else {
                    stack.add(ScreenRoute.DASHBOARD)
                }
                _screenStack.value = stack
            }
        }
    }

    fun navigateTo(route: ScreenRoute) {
        soundManager.playToneClick()
        val current = _screenStack.value
        _screenStack.value = current + route
    }

    fun switchTab(route: ScreenRoute) {
        soundManager.playToneClick()
        _screenStack.value = listOf(route)
    }

    fun signOut() {
        soundManager.playToneClick()
        _isGuestSession.value = false
        try {
            Firebase.auth.signOut()
        } catch (e: Exception) {
            Log.w("ReadEaseViewModel", "SignOut exception: ${e.message}")
        }
        _screenStack.value = listOf(ScreenRoute.AUTH)
    }

    fun navigateBack(): Boolean {
        soundManager.playToneClick()
        val current = _screenStack.value
        return if (current.size > 1) {
            _screenStack.value = current.dropLast(1)
            true
        } else {
            false
        }
    }

    // Accessibility Preferences
    val accessibilityPreferences: StateFlow<AccessibilityPreferences> = repository.accessibilityPreferences

    fun updatePreferences(newPrefs: AccessibilityPreferences) {
        repository.updateAccessibilityPreferences(newPrefs)
    }

    fun toggleReadingRuler() {
        val current = accessibilityPreferences.value
        repository.updateAccessibilityPreferences(current.copy(readingRulerEnabled = !current.readingRulerEnabled))
        soundManager.playToneClick()
    }

    fun setBackgroundTint(tint: BackgroundTint) {
        val current = accessibilityPreferences.value
        repository.updateAccessibilityPreferences(current.copy(backgroundTint = tint))
        soundManager.playToneClick()
    }

    // Repository datasets
    val dyslexiaStories: List<DyslexiaStory> = repository.dyslexiaStories
    val letterMnemonics: List<LetterMnemonic> = repository.letterMnemonics
    val syllableWords: List<SyllableWord> = repository.syllableWords
    val wordBuilderPuzzles: List<WordBuilderPuzzle> = repository.wordBuilderPuzzles
    val soundStepsExercises: List<SoundStepExercise> = repository.soundStepsExercises
    val readAlongItems: List<ReadAlongItem> = repository.readAlongItems
    val signLetters: List<SignLetter> = repository.signLetters
    val commonSignPhrases = repository.commonSignPhrases
    val islSigns: List<com.example.data.model.ISLSignItem> = repository.islSigns
    val aacCards: List<AACCard> = repository.aacCards
    val screeningQuestions = repository.screeningQuestions

    // ISL Sign Progress Flow
    val allSignProgress: StateFlow<List<com.example.data.model.SignProgressEntity>> = repository.allSignProgress.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allPracticeSessions: StateFlow<List<com.example.data.model.PracticeSessionEntity>> = repository.allPracticeSessions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _selectedISLSign = MutableStateFlow<com.example.data.model.ISLSignItem?>(null)
    val selectedISLSign: StateFlow<com.example.data.model.ISLSignItem?> = _selectedISLSign.asStateFlow()

    fun selectISLSign(sign: com.example.data.model.ISLSignItem?) {
        _selectedISLSign.value = sign
    }

    fun recordISLPracticeAttempt(
        signId: String,
        score: Int,
        confidence: Float,
        feedback: String,
        currentStep: Int = 4
    ) {
        viewModelScope.launch {
            repository.recordSignPracticeAttempt(signId, score, confidence, feedback, currentStep)
            soundManager.playToneFanfare()
        }
    }

    fun saveSignProgress(progress: com.example.data.model.SignProgressEntity) {
        viewModelScope.launch {
            repository.saveSignProgress(progress)
        }
    }

    // State for Reader
    private val _selectedStory = MutableStateFlow(dyslexiaStories.first())
    val selectedStory: StateFlow<DyslexiaStory> = _selectedStory.asStateFlow()

    fun selectStory(story: DyslexiaStory) {
        _selectedStory.value = story
        soundManager.stopSpeaking()
    }

    // State for Word Builder
    private val _wbPuzzleIndex = MutableStateFlow(0)
    val wbPuzzleIndex: StateFlow<Int> = _wbPuzzleIndex.asStateFlow()
    val currentWbPuzzle: WordBuilderPuzzle get() = wordBuilderPuzzles.getOrElse(_wbPuzzleIndex.value) { wordBuilderPuzzles.first() }

    private val _wbCurrentLetters = MutableStateFlow<List<String>>(emptyList())
    val wbCurrentLetters: StateFlow<List<String>> = _wbCurrentLetters.asStateFlow()

    fun addWbLetter(letter: String) {
        soundManager.playToneLetterSound(letter.firstOrNull() ?: ' ')
        _wbCurrentLetters.value = _wbCurrentLetters.value + letter
        checkWbSolution()
    }

    fun removeWbLastLetter() {
        soundManager.playToneClick()
        val current = _wbCurrentLetters.value
        if (current.isNotEmpty()) {
            _wbCurrentLetters.value = current.dropLast(1)
        }
    }

    fun clearWbLetters() {
        soundManager.playToneClick()
        _wbCurrentLetters.value = emptyList()
    }

    private fun checkWbSolution() {
        val target = currentWbPuzzle.targetWord
        val formed = _wbCurrentLetters.value.joinToString("")
        if (formed.equals(target, ignoreCase = true)) {
            soundManager.playToneFanfare()
            viewModelScope.launch {
                repository.addXpAndStreak(35)
                repository.recordLessonCompletion("wb_${currentWbPuzzle.id}", "Word Builder", 100)
                _lastCompletionMessage.value = "Magnificent! You assembled '$target'!"
                navigateTo(ScreenRoute.LESSON_COMPLETION)
            }
        }
    }

    fun nextWbPuzzle() {
        _wbPuzzleIndex.value = (_wbPuzzleIndex.value + 1) % wordBuilderPuzzles.size
        _wbCurrentLetters.value = emptyList()
    }

    // Sound Steps State
    private val _soundStepIndex = MutableStateFlow(0)
    val soundStepIndex: StateFlow<Int> = _soundStepIndex.asStateFlow()
    val currentSoundStep: SoundStepExercise get() = soundStepsExercises.getOrElse(_soundStepIndex.value) { soundStepsExercises.first() }

    private val _activeSoundSegment = MutableStateFlow(0)
    val activeSoundSegment: StateFlow<Int> = _activeSoundSegment.asStateFlow()

    fun playSoundSegment(index: Int) {
        _activeSoundSegment.value = index
        val segment = currentSoundStep.soundSegments.getOrNull(index) ?: ""
        soundManager.speak(segment, rate = 0.8f, pitch = 1.0f)
    }

    fun blendSoundStep() {
        soundManager.speak(currentSoundStep.fullWord, rate = 0.85f, pitch = 1.0f)
        viewModelScope.launch {
            repository.addXpAndStreak(25)
            repository.recordLessonCompletion("ss_${currentSoundStep.id}", "Sound Steps", 100)
            _lastCompletionMessage.value = "Great job blending '${currentSoundStep.fullWord}'!"
            navigateTo(ScreenRoute.LESSON_COMPLETION)
        }
    }

    fun nextSoundStep() {
        _soundStepIndex.value = (_soundStepIndex.value + 1) % soundStepsExercises.size
        _activeSoundSegment.value = 0
    }

    // Read Along State
    private val _readAlongHighlightedWord = MutableStateFlow<String?>("")
    val readAlongHighlightedWord: StateFlow<String?> = _readAlongHighlightedWord.asStateFlow()

    fun speakReadAlongSentence(sentence: String) {
        soundManager.speak(sentence, rate = accessibilityPreferences.value.speechRate)
    }

    // Screener State
    private val _activeScreeningQuestions = MutableStateFlow<List<ScreeningQuestion>>(repository.getShuffledScreeningQuestions())
    val activeScreeningQuestions: StateFlow<List<ScreeningQuestion>> = _activeScreeningQuestions.asStateFlow()

    private val _screenerAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val screenerAnswers: StateFlow<Map<Int, Int>> = _screenerAnswers.asStateFlow()

    private val _screeningReport = MutableStateFlow<ScreeningReport?>(null)
    val screeningReport: StateFlow<ScreeningReport?> = _screeningReport.asStateFlow()

    fun startNewShuffledScreener() {
        _activeScreeningQuestions.value = repository.getShuffledScreeningQuestions()
        _screenerAnswers.value = emptyMap()
        _screeningReport.value = null
    }

    fun setScreenerAnswer(questionId: Int, score: Int) {
        soundManager.playToneClick()
        val current = _screenerAnswers.value.toMutableMap()
        current[questionId] = score
        _screenerAnswers.value = current
    }

    fun submitScreener() {
        val questions = _activeScreeningQuestions.value.ifEmpty { repository.screeningQuestions }
        val report = repository.calculateScreeningReport(questions, _screenerAnswers.value)
        _screeningReport.value = report
        viewModelScope.launch {
            repository.saveScreeningResult(report)
            soundManager.playToneFanfare()
            navigateTo(ScreenRoute.SCREENING_RESULT)
        }
    }

    fun resetScreener() {
        startNewShuffledScreener()
    }

    // Silent Voice AAC State
    private val _aacSentence = MutableStateFlow<List<AACSentenceToken>>(emptyList())
    val aacSentence: StateFlow<List<AACSentenceToken>> = _aacSentence.asStateFlow()

    fun addAacCardToSentence(card: AACCard) {
        soundManager.playToneClick()
        val token = AACSentenceToken(text = card.title, emoji = card.symbolEmoji)
        _aacSentence.value = _aacSentence.value + token
    }

    fun clearAacSentence() {
        soundManager.playToneClick()
        _aacSentence.value = emptyList()
    }

    fun speakAacSentence() {
        val sentence = _aacSentence.value.joinToString(" ") { it.text }
        if (sentence.isNotEmpty()) {
            soundManager.speak(sentence, rate = accessibilityPreferences.value.speechRate)
        }
    }

    fun speakAacCard(card: AACCard) {
        soundManager.speak(card.speechPhrase, rate = accessibilityPreferences.value.speechRate)
    }

    // Lesson Completion info
    private val _lastCompletionMessage = MutableStateFlow("Lesson Complete!")
    val lastCompletionMessage: StateFlow<String> = _lastCompletionMessage.asStateFlow()

    fun acceptGuidelines() {
        viewModelScope.launch {
            val current = repository.userProfile.firstOrNull() ?: UserProfile()
            val updated = current.copy(guidelinesAccepted = true)
            repository.saveUserProfile(updated)
            if (!updated.onboardingCompleted) {
                _screenStack.value = listOf(ScreenRoute.PROFILE_SETUP)
            } else {
                _screenStack.value = listOf(ScreenRoute.DASHBOARD)
            }
        }
    }

    fun completeOnboarding(fullName: String, displayName: String, preferredLanguage: String, learningLevel: String) {
        viewModelScope.launch {
            val current = repository.userProfile.firstOrNull() ?: UserProfile()
            val updated = current.copy(
                fullName = fullName,
                name = displayName,
                preferredLanguage = preferredLanguage,
                learningLevel = learningLevel,
                onboardingCompleted = true,
                guidelinesAccepted = true
            )
            repository.saveUserProfile(updated)
            _screenStack.value = listOf(ScreenRoute.DASHBOARD)
        }
    }

    fun updateUserProfile(name: String, emoji: String) {
        viewModelScope.launch {
            val current = repository.userProfile.firstOrNull() ?: UserProfile()
            val updated = current.copy(
                name = name,
                avatarEmoji = emoji
            )
            repository.saveUserProfile(updated)
            evaluateRouting()
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }
}
