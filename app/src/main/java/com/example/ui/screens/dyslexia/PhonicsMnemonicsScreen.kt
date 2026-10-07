package com.example.ui.screens.dyslexia

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.AmbientAtmosphere
import com.example.ui.components.PhonicsMnemonics
import com.example.ui.viewmodel.ReadEaseViewModel

@Composable
fun PhonicsMnemonicsScreen(
    viewModel: ReadEaseViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.soundManager.stopSpeaking()
        viewModel.navigateBack()
    }

    val preferences by viewModel.accessibilityPreferences.collectAsState()
    val mnemonics = viewModel.letterMnemonics

    AmbientAtmosphere(tint = preferences.backgroundTint) {
        Column(
            modifier = modifier
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
                    onClick = {
                        viewModel.soundManager.stopSpeaking()
                        viewModel.navigateBack()
                    },
                    modifier = Modifier.testTag("mnemonics_back_button")
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
                        text = "Letter Confusion Aids",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Visual mnemonics for tricky b/d, p/q and m/w pairs",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                item {
                    // Reusable PhonicsMnemonics Component
                    PhonicsMnemonics(
                        mnemonics = mnemonics,
                        onLetterSpeak = { letter ->
                            viewModel.soundManager.playToneLetterSound(letter)
                        },
                        onWordSpeak = { word ->
                            viewModel.soundManager.speak(word, rate = preferences.speechRate)
                        },
                        showQuizSection = true
                    )
                }
            }
        }
    }
}
