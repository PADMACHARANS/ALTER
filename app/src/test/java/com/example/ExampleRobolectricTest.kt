package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.AppDatabase
import com.example.data.model.AccessibilityPreferences
import com.example.data.model.BackgroundTint
import com.example.data.model.UserProfile
import com.example.data.repository.ReadEaseRepository
import com.example.ui.viewmodel.DailyQuizViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("ALTER", appName)
  }

  @Test
  fun `verify default user profile and preferences`() {
    val profile = UserProfile()
    assertEquals("Learner", profile.name)
    assertEquals(1, profile.level)

    val prefs = AccessibilityPreferences()
    assertEquals(BackgroundTint.NONE, prefs.backgroundTint)
    assertNotNull(prefs.rulerColorHex)
  }

  @Test
  fun `verify dyslexia background tints and typography attributes`() {
    val tints = BackgroundTint.values()
    assertTrue(tints.any { it == BackgroundTint.CREAM })
    assertTrue(tints.any { it == BackgroundTint.PASTEL_YELLOW })
    assertTrue(tints.any { it == BackgroundTint.SOFT_BLUE })
    assertTrue(tints.any { it == BackgroundTint.MINT })

    val customizedPrefs = AccessibilityPreferences(
      fontScale = 1.3f,
      letterSpacingSp = 1.5f,
      lineSpacingMultiplier = 1.8f,
      backgroundTint = BackgroundTint.PASTEL_YELLOW,
      bionicReadingEnabled = true,
      readingRulerEnabled = true
    )

    assertEquals(1.3f, customizedPrefs.fontScale, 0.01f)
    assertEquals(1.5f, customizedPrefs.letterSpacingSp, 0.01f)
    assertEquals(1.8f, customizedPrefs.lineSpacingMultiplier, 0.01f)
    assertEquals(BackgroundTint.PASTEL_YELLOW, customizedPrefs.backgroundTint)
    assertTrue(customizedPrefs.bionicReadingEnabled)
    assertTrue(customizedPrefs.readingRulerEnabled)
  }

  @Test
  fun `verify phonics mnemonics letter pairs and visual associations`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = AppDatabase.getInstance(context)
    val repo = ReadEaseRepository(db.userDao(), context)

    val mnemonics = repo.letterMnemonics
    assertTrue("Should contain multiple letter mnemonics", mnemonics.size >= 4)

    val bMnemonic = mnemonics.find { it.letter == 'b' }
    assertNotNull("b mnemonic should exist", bMnemonic)
    assertEquals('d', bMnemonic!!.counterpart)
    assertTrue(bMnemonic.hintRule.contains("bat", ignoreCase = true))

    val pMnemonic = mnemonics.find { it.letter == 'p' }
    assertNotNull("p mnemonic should exist", pMnemonic)
    assertEquals('q', pMnemonic!!.counterpart)

    val mMnemonic = mnemonics.find { it.letter == 'm' }
    assertNotNull("m mnemonic should exist", mMnemonic)
    assertEquals('w', mMnemonic!!.counterpart)
  }

  @Test
  fun `verify daily quiz accuracy percentage calculation and performance tracking`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = AppDatabase.getInstance(context)
    val viewModel = DailyQuizViewModel(db.userDao())

    // Test accuracy calculation edge cases
    assertEquals(0f, viewModel.calculateAccuracyPercentage(0, 0), 0.01f)
    assertEquals(80f, viewModel.calculateAccuracyPercentage(4, 5), 0.01f)
    assertEquals(100f, viewModel.calculateAccuracyPercentage(5, 5), 0.01f)
    assertEquals(33.3f, viewModel.calculateAccuracyPercentage(1, 3), 0.01f)

    // Answer questions
    val questions = viewModel.uiState.value.questions
    assertTrue(questions.isNotEmpty())

    val firstQ = questions[0]
    viewModel.submitAnswer(firstQ.id, firstQ.correctIndex)

    val state = viewModel.uiState.value
    assertEquals(1, state.totalAttempted)
    assertEquals(1, state.correctAnswersCount)
    assertEquals(100f, state.accuracyPercentage, 0.01f)
  }

  @Test
  fun `verify reset reading streak upon login`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = AppDatabase.getInstance(context)
    val userDao = db.userDao()

    // Setup an initial profile with 7 streak days
    userDao.saveUserProfile(UserProfile(streakDays = 7))

    val viewModel = DailyQuizViewModel(userDao)

    // Trigger reset upon login
    viewModel.resetReadingStreakUponLoginSuspend(forceReset = true)

    // Wait and verify updated profile in database
    val updatedProfile = userDao.getUserProfile().first()
    assertNotNull(updatedProfile)
    assertEquals(0, updatedProfile!!.streakDays)

    val state = viewModel.uiState.value
    assertEquals(0, state.currentStreakDays)
    assertTrue(state.isStreakReset)
    assertTrue(state.streakStatusMessage.contains("reset", ignoreCase = true))
  }

  @Test
  fun `verify 10 shuffled dyslexia questions and 0 to 100 rating calculation`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = AppDatabase.getInstance(context)
    val repo = ReadEaseRepository(db.userDao(), context)

    val questions = repo.screeningQuestions
    assertEquals("Should have exactly 10 dyslexia questions", 10, questions.size)

    val shuffled = repo.getShuffledScreeningQuestions()
    assertEquals(10, shuffled.size)

    // Test rating 0/100 (all 0s)
    val zeroAnswers = questions.associate { it.id to 0 }
    val zeroReport = repo.calculateScreeningReport(questions, zeroAnswers)
    assertEquals(0, zeroReport.percentage)
    assertTrue(zeroReport.severityLevel.contains("0/100"))
    assertTrue(zeroReport.severityLevel.contains("Typical", ignoreCase = true))

    // Test rating 100/100 (all 3s = 30 points)
    val maxAnswers = questions.associate { it.id to 3 }
    val maxReport = repo.calculateScreeningReport(questions, maxAnswers)
    assertEquals(100, maxReport.percentage)
    assertTrue(maxReport.severityLevel.contains("100/100"))
    assertTrue(maxReport.severityLevel.contains("High Indicators", ignoreCase = true))

    // Test moderate rating (half = 15 points = 50%)
    val halfAnswers = questions.associate { it.id to (if (it.id % 2 == 0) 3 else 0) } // 5 * 3 = 15 points
    val halfReport = repo.calculateScreeningReport(questions, halfAnswers)
    assertEquals(50, halfReport.percentage)
    assertTrue(halfReport.severityLevel.contains("50/100"))
    assertTrue(halfReport.severityLevel.contains("Moderate Indicators", ignoreCase = true))
  }

  @Test
  fun `verify Indian Sign Language dictionary and progress tracking`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = AppDatabase.getInstance(context)
    val userDao = db.userDao()
    val repo = ReadEaseRepository(userDao, context)

    val signs = repo.islSigns
    assertTrue("Should have extensive ISL sign items", signs.size >= 15)

    val namaste = signs.find { it.id == "isl_namaste" }
    assertNotNull("Namaste sign must exist in ISL dictionary", namaste)
    assertTrue("Source reference should reference ISLRTC", namaste!!.sourceName.contains("ISLRTC"))

    // Verify sign progress recording
    repo.recordSignPracticeAttempt(
      signId = "isl_namaste",
      score = 95,
      confidence = 0.95f,
      feedback = "Crisp handshape alignment",
      currentStep = 4
    )

    val progress = repo.getSignProgress("isl_namaste").first()
    assertNotNull("Recorded sign progress should be saved", progress)
    assertEquals(1, progress!!.attempts)
    assertEquals(1, progress.successfulAttempts)
    assertTrue(progress.masteryScore > 0)
  }
}
