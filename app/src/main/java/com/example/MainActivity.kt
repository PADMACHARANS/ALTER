package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.audio.SoundManager
import com.example.data.db.AppDatabase
import com.example.data.repository.ReadEaseRepository
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.auth.ProfileSetupScreen
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.dyslexia.DyslexiaReaderScreen
import com.example.ui.screens.dyslexia.PhonicsMnemonicsScreen
import com.example.ui.screens.dyslexia.TextSimplifierScreen
import com.example.ui.screens.practice.LessonCompletionScreen
import com.example.ui.screens.practice.PracticeHubScreen
import com.example.ui.screens.practice.ReadAlongScreen
import com.example.ui.screens.practice.SoundStepsScreen
import com.example.ui.screens.practice.WordBuilderScreen
import com.example.ui.screens.guidelines.GuidelinesScreen
import com.example.ui.screens.quiz.DailyQuizScreen
import com.example.ui.screens.screening.ScreeningResultScreen
import com.example.ui.screens.screening.ScreeningScreen
import com.example.ui.screens.settings.AccessibilitySettingsScreen
import com.example.ui.screens.signlanguage.SignLanguageScreen
import com.example.ui.screens.silentvoice.SilentVoiceScreen
import com.example.ui.theme.ReadEaseTheme
import com.example.ui.viewmodel.DailyQuizViewModel
import com.example.ui.viewmodel.ReadEaseViewModel
import com.example.ui.viewmodel.ScreenRoute

class MainActivity : ComponentActivity() {

    private lateinit var soundManager: SoundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getInstance(applicationContext)
        soundManager = SoundManager(applicationContext)
        val userDao = database.userDao()
        val repository = ReadEaseRepository(userDao, applicationContext)

        setContent {
            val viewModel = remember {
                ReadEaseViewModel(repository, soundManager)
            }
            val dailyQuizViewModel = remember {
                DailyQuizViewModel(userDao, soundManager)
            }

            ReadEaseTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    ReadEaseAppContent(
                        viewModel = viewModel,
                        dailyQuizViewModel = dailyQuizViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::soundManager.isInitialized) {
            soundManager.release()
        }
    }
}

@Composable
fun ReadEaseAppContent(
    viewModel: ReadEaseViewModel,
    dailyQuizViewModel: DailyQuizViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    when (currentScreen) {
        ScreenRoute.AUTH -> AuthScreen(viewModel = viewModel, dailyQuizViewModel = dailyQuizViewModel, modifier = modifier)
        ScreenRoute.DASHBOARD -> DashboardScreen(viewModel = viewModel, modifier = modifier)
        ScreenRoute.DYSLEXIA_READER -> DyslexiaReaderScreen(viewModel = viewModel, modifier = modifier)
        ScreenRoute.PHONICS_MNEMONICS -> PhonicsMnemonicsScreen(viewModel = viewModel, modifier = modifier)
        ScreenRoute.TEXT_SIMPLIFIER -> TextSimplifierScreen(viewModel = viewModel, modifier = modifier)
        ScreenRoute.PRACTICE_HUB -> PracticeHubScreen(viewModel = viewModel, modifier = modifier)
        ScreenRoute.WORD_BUILDER -> WordBuilderScreen(viewModel = viewModel, modifier = modifier)
        ScreenRoute.SOUND_STEPS -> SoundStepsScreen(viewModel = viewModel, modifier = modifier)
        ScreenRoute.READ_ALONG -> ReadAlongScreen(viewModel = viewModel, modifier = modifier)
        ScreenRoute.LESSON_COMPLETION -> LessonCompletionScreen(viewModel = viewModel, modifier = modifier)
        ScreenRoute.SCREENING -> ScreeningScreen(viewModel = viewModel, modifier = modifier)
        ScreenRoute.SCREENING_RESULT -> ScreeningResultScreen(viewModel = viewModel, modifier = modifier)
        ScreenRoute.SIGN_LANGUAGE -> SignLanguageScreen(viewModel = viewModel, modifier = modifier)
        ScreenRoute.SILENT_VOICE -> SilentVoiceScreen(viewModel = viewModel, modifier = modifier)
        ScreenRoute.ACCESSIBILITY_SETTINGS -> AccessibilitySettingsScreen(viewModel = viewModel, modifier = modifier)
        ScreenRoute.DAILY_QUIZ -> DailyQuizScreen(
            dailyQuizViewModel = dailyQuizViewModel,
            readEaseViewModel = viewModel,
            modifier = modifier
        )
        ScreenRoute.GUIDELINES -> GuidelinesScreen(
            viewModel = viewModel,
            modifier = modifier
        )
        ScreenRoute.PROFILE_SETUP -> ProfileSetupScreen(
            viewModel = viewModel,
            modifier = modifier
        )
    }
}
