package com.example.ui.screens.practice

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.ui.components.ConfettiCelebration
import com.example.ui.theme.Amber500
import com.example.ui.theme.Indigo600
import com.example.ui.theme.Teal600
import com.example.ui.viewmodel.ReadEaseViewModel
import com.example.ui.viewmodel.ScreenRoute

@Composable
fun LessonCompletionScreen(
    viewModel: ReadEaseViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(ScreenRoute.DASHBOARD)
    }

    val preferences by viewModel.accessibilityPreferences.collectAsState()
    val message by viewModel.lastCompletionMessage.collectAsState()
    val profile by viewModel.userProfile.collectAsState()

    AmbientAtmosphere(tint = preferences.backgroundTint) {
        Box(modifier = modifier.fillMaxSize()) {
            // Confetti layer
            ConfettiCelebration(isActive = true)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Trophy / Star Circle
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(Amber500.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Success",
                        tint = Amber500,
                        modifier = Modifier.size(56.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Outstanding Job!",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Stats Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = Amber500, modifier = Modifier.size(20.dp))
                                Text("+35 XP", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Amber500)
                            }
                            Text("Earned", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Box(modifier = Modifier.width(1.dp).height(36.dp).background(Color.LightGray))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${profile.streakDays} Days", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Teal600)
                            Text("Streak Active", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Box(modifier = Modifier.width(1.dp).height(36.dp).background(Color.LightGray))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Level ${profile.level}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Indigo600)
                            Text("Current Rank", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))

                Button(
                    onClick = { viewModel.navigateTo(ScreenRoute.DASHBOARD) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("completion_dashboard_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
                ) {
                    Icon(Icons.Default.Home, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Return to Dashboard", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = { viewModel.navigateTo(ScreenRoute.PRACTICE_HUB) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("completion_practice_more_button"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Replay, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Practice Another Skill", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
