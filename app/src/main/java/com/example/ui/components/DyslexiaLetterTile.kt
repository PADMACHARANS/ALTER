package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LetterMnemonic
import com.example.ui.theme.Indigo600
import com.example.ui.theme.Teal600

@Composable
fun DyslexiaLetterTile(
    mnemonic: LetterMnemonic,
    isSelected: Boolean,
    onLetterClick: (Char) -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) Indigo600 else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
        label = "borderColor"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onLetterClick(mnemonic.letter) },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = if (isSelected) 2.dp else 1.dp, color = borderColor, shape = RoundedCornerShape(18.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Big Bold Letter Circle with tactile weight
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) Indigo600 else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = mnemonic.letter.toString(),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Information & Rules
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = mnemonic.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = mnemonic.visualMnemonic,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "💡 Rule: ${mnemonic.hintRule}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Teal600,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Audio Speak Button
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Indigo600.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Speak Letter",
                    tint = Indigo600,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
