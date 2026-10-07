package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.data.model.BackgroundTint

@Composable
fun AmbientAtmosphere(
    tint: BackgroundTint,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val tintColor = if (tint == BackgroundTint.NONE) {
        Color.Transparent
    } else {
        Color(tint.hexColor)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .then(
                if (tint != BackgroundTint.NONE) Modifier.background(tintColor) else Modifier
            )
    ) {
        content()
    }
}
