package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Indigo600
import com.example.ui.viewmodel.ScreenRoute

data class NavigationTabItem(
    val route: ScreenRoute,
    val label: String,
    val iconKey: String
)

val MainNavigationTabs = listOf(
    NavigationTabItem(ScreenRoute.DASHBOARD, "Home", "home"),
    NavigationTabItem(ScreenRoute.SIGN_LANGUAGE, "ISL Learn", "learn"),
    NavigationTabItem(ScreenRoute.PRACTICE_HUB, "Practice", "practice"),
    NavigationTabItem(ScreenRoute.DAILY_QUIZ, "Progress", "progress"),
    NavigationTabItem(ScreenRoute.PROFILE_SETUP, "Profile", "profile")
)

@Composable
fun AppBottomNavigation(
    currentRoute: ScreenRoute,
    onTabSelected: (ScreenRoute) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        MainNavigationTabs.forEach { tab ->
            val isSelected = when (tab.route) {
                ScreenRoute.DASHBOARD -> currentRoute == ScreenRoute.DASHBOARD
                ScreenRoute.SIGN_LANGUAGE -> currentRoute == ScreenRoute.SIGN_LANGUAGE
                ScreenRoute.PRACTICE_HUB -> currentRoute in listOf(
                    ScreenRoute.PRACTICE_HUB,
                    ScreenRoute.WORD_BUILDER,
                    ScreenRoute.SOUND_STEPS,
                    ScreenRoute.READ_ALONG
                )
                ScreenRoute.DAILY_QUIZ -> currentRoute in listOf(
                    ScreenRoute.DAILY_QUIZ,
                    ScreenRoute.LESSON_COMPLETION
                )
                ScreenRoute.PROFILE_SETUP -> currentRoute in listOf(
                    ScreenRoute.PROFILE_SETUP,
                    ScreenRoute.ACCESSIBILITY_SETTINGS,
                    ScreenRoute.GUIDELINES
                )
                else -> currentRoute == tab.route
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != tab.route) {
                        onTabSelected(tab.route)
                    }
                },
                icon = {
                    Icon(
                        imageVector = IconRegistry.getIcon(tab.iconKey, isSelected),
                        contentDescription = tab.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 11.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Indigo600,
                    selectedTextColor = Indigo600,
                    indicatorColor = Indigo600.copy(alpha = 0.12f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag("nav_tab_${tab.label.lowercase().replace(" ", "_")}")
            )
        }
    }
}
