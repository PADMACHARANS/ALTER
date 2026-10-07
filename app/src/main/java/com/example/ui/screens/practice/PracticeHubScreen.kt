package com.example.ui.screens.practice

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AmbientAtmosphere
import com.example.ui.components.AppBottomNavigation
import com.example.ui.theme.Amber500
import com.example.ui.theme.Indigo600
import com.example.ui.theme.Teal600
import com.example.ui.viewmodel.ReadEaseViewModel
import com.example.ui.viewmodel.ScreenRoute

@Composable
fun PracticeHubScreen(
    viewModel: ReadEaseViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val preferences by viewModel.accessibilityPreferences.collectAsState()
    val profile by viewModel.userProfile.collectAsState()

    AmbientAtmosphere(tint = preferences.backgroundTint) {
        Box(modifier = modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(14.dp))

                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("practice_hub_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "Practice Hub",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Interactive multisensory exercises & games",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 85.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                item {
                    PracticeModeCard(
                        title = "Word Builder",
                        description = "Tap tactile letter tiles to assemble phonics words. Solve anagrams with hints.",
                        icon = Icons.Default.Extension,
                        accentColor = Indigo600,
                        xpBonus = "+35 XP",
                        tag = "practice_mode_word_builder",
                        onClick = { viewModel.navigateTo(ScreenRoute.WORD_BUILDER) }
                    )
                }

                item {
                    PracticeModeCard(
                        title = "Sound Steps",
                        description = "Phonemic awareness blending. Step through each individual sound chunk /k/ /æ/ /t/ -> CAT.",
                        icon = Icons.Default.Hearing,
                        accentColor = Teal600,
                        xpBonus = "+25 XP",
                        tag = "practice_mode_sound_steps",
                        onClick = { viewModel.navigateTo(ScreenRoute.SOUND_STEPS) }
                    )
                }

                item {
                    PracticeModeCard(
                        title = "ISL Gesture Practice",
                        description = "Indian Sign Language camera & manual gesture verification with real-time feedback.",
                        icon = Icons.Default.PanTool,
                        accentColor = Teal600,
                        xpBonus = "+50 XP",
                        tag = "practice_mode_isl_camera",
                        onClick = { viewModel.navigateTo(ScreenRoute.SIGN_LANGUAGE) }
                    )
                }

                item {
                    PracticeModeCard(
                        title = "Read-Along Stories",
                        description = "Karaoke-style synchronized word highlighting with comforting natural voice audio.",
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        accentColor = Amber500,
                        xpBonus = "+40 XP",
                        tag = "practice_mode_read_along",
                        onClick = { viewModel.navigateTo(ScreenRoute.READ_ALONG) }
                    )
                }
            }
        }

        // Bottom Navigation Bar
        AppBottomNavigation(
            currentRoute = ScreenRoute.PRACTICE_HUB,
            onTabSelected = { route -> viewModel.switchTab(route) },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
}

@Composable
fun PracticeModeCard(
    title: String,
    description: String,
    icon: ImageVector,
    accentColor: Color,
    xpBonus: String,
    tag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .testTag(tag),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Amber500.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Amber500,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = xpBonus,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Amber500
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            )
        }
    }
}
