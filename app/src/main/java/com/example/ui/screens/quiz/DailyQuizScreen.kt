package com.example.ui.screens.quiz

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AmbientAtmosphere
import com.example.ui.components.AppBottomNavigation
import com.example.ui.theme.Amber500
import com.example.ui.theme.CoralRed
import com.example.ui.theme.Indigo600
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.Teal600
import com.example.ui.viewmodel.DailyQuizViewModel
import com.example.ui.viewmodel.ReadEaseViewModel
import com.example.ui.viewmodel.ScreenRoute

@Composable
fun DailyQuizScreen(
    dailyQuizViewModel: DailyQuizViewModel,
    readEaseViewModel: ReadEaseViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        readEaseViewModel.navigateBack()
    }

    val state by dailyQuizViewModel.uiState.collectAsState()
    val preferences by readEaseViewModel.accessibilityPreferences.collectAsState()

    AmbientAtmosphere(tint = preferences.backgroundTint) {
        Box(modifier = modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
            Spacer(modifier = Modifier.height(14.dp))

            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { readEaseViewModel.navigateBack() },
                    modifier = Modifier.testTag("daily_quiz_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Daily Literacy Quiz",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Performance & Accuracy Tracker",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Streak Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Amber500.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Streak",
                            tint = Amber500,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${state.currentStreakDays}d",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Amber500
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Login Streak Reset Notice / Trigger
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (state.currentStreakDays > 0) Amber500.copy(alpha = 0.12f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (state.streakStatusMessage.isNotBlank()) state.streakStatusMessage
                            else if (state.currentStreakDays == 0) "Welcome! Complete today's quiz to start your 1-day reading streak! 🔥"
                            else "Streak active: ${state.currentStreakDays} days in a row! 🔥",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (state.currentStreakDays > 0) Amber500 else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    OutlinedButton(
                        onClick = { dailyQuizViewModel.resetReadingStreakUponLogin(forceReset = true) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(34.dp).testTag("reset_streak_login_button")
                    ) {
                        Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (state.currentStreakDays == 0) "Start Fresh" else "Reset Streak",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Accuracy & Progress Metrics Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Live Accuracy: ${state.accuracyPercentage}%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (state.accuracyPercentage >= 75f) LeafGreen else Indigo600
                        )
                        Text(
                            text = "${state.correctAnswersCount}/${state.totalAttempted} Correct",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { (state.accuracyPercentage / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (state.accuracyPercentage >= 75f) LeafGreen else Teal600,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!state.isCompleted && state.questions.isNotEmpty()) {
                val currentQuestion = state.questions[state.currentQuestionIndex]
                val selectedOption = state.userAnswers[currentQuestion.id]

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Question Prompt Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Question ${state.currentQuestionIndex + 1} of ${state.questions.size} • ${currentQuestion.category}",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Teal600
                                    )

                                    IconButton(
                                        onClick = {
                                            readEaseViewModel.soundManager.speak(currentQuestion.prompt)
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                            contentDescription = "Speak Question",
                                            tint = Indigo600,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = currentQuestion.prompt,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 28.sp
                                )

                                if (currentQuestion.mnemonicHint != null) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Amber500.copy(alpha = 0.12f))
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lightbulb,
                                            contentDescription = "Hint",
                                            tint = Amber500,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = currentQuestion.mnemonicHint,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Options List
                    items(currentQuestion.options.size) { optIndex ->
                        val optionText = currentQuestion.options[optIndex]
                        val isChosen = selectedOption == optIndex
                        val hasAnswered = selectedOption != null
                        val isCorrectOption = optIndex == currentQuestion.correctIndex

                        val containerColor = when {
                            !hasAnswered -> MaterialTheme.colorScheme.surface
                            isCorrectOption -> LeafGreen.copy(alpha = 0.18f)
                            isChosen -> CoralRed.copy(alpha = 0.18f)
                            else -> MaterialTheme.colorScheme.surface
                        }

                        val borderColor = when {
                            !hasAnswered -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            isCorrectOption -> LeafGreen
                            isChosen -> CoralRed
                            else -> Color.Transparent
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .border(width = 1.5.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
                                .clickable(enabled = !hasAnswered) {
                                    dailyQuizViewModel.submitAnswer(currentQuestion.id, optIndex)
                                }
                                .testTag("quiz_option_$optIndex"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = containerColor),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (hasAnswered && isCorrectOption) LeafGreen
                                            else if (hasAnswered && isChosen) CoralRed
                                            else Indigo600.copy(alpha = 0.12f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (hasAnswered && isCorrectOption) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                    } else if (hasAnswered && isChosen) {
                                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                    } else {
                                        Text(
                                            text = ('A' + optIndex).toString(),
                                            fontWeight = FontWeight.Bold,
                                            color = Indigo600
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Text(
                                    text = optionText,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // Explanation Box
                    if (selectedOption != null) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Teal600.copy(alpha = 0.12f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "💡 Explanation",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Teal600
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = currentQuestion.explanation,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                // Next / Finish Question Navigation Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (state.currentQuestionIndex > 0) {
                        OutlinedButton(
                            onClick = { dailyQuizViewModel.previousQuestion() },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Previous")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    Button(
                        onClick = { dailyQuizViewModel.nextQuestion() },
                        enabled = selectedOption != null,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                        modifier = Modifier.testTag("quiz_next_button")
                    ) {
                        Text(
                            text = if (state.currentQuestionIndex < state.questions.size - 1) "Next Question" else "Finish & Save",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            } else if (state.isCompleted) {
                // Completed Summary Card
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .background(Indigo600.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Indigo600,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = "Quiz Completed!",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = state.performanceRating,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Teal600
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Accuracy", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("${state.accuracyPercentage}%", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Indigo600)
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Score", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("${state.correctAnswersCount}/${state.totalAttempted}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Streak", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("${state.currentStreakDays} Days", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Amber500)
                                    }
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                Button(
                                    onClick = { readEaseViewModel.navigateTo(ScreenRoute.DASHBOARD) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("quiz_finish_return_btn"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
                                ) {
                                    Text("Return to Dashboard", fontWeight = FontWeight.Bold, color = Color.White)
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedButton(
                                    onClick = { dailyQuizViewModel.retryQuiz() },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Try Again")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Navigation Bar
        AppBottomNavigation(
            currentRoute = ScreenRoute.DAILY_QUIZ,
            onTabSelected = { route -> readEaseViewModel.switchTab(route) },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
}
