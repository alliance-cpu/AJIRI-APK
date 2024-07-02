package com.example.ajiri.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable


private val DarkColorScheme = darkColorScheme(
    primary = Green700,
    onPrimary = White,
    secondary = Green700,
    onSecondary = White,
    background = White,
    onBackground = Black,
    surface = Green700,
    onSurface = Black,
    error = ErrorRed,
    onError = White
)

private val LightColorScheme = lightColorScheme(
    primary = Green500,
    onPrimary = White,
    secondary = Green500,
    onSecondary = White,
    background = White,
    onBackground = Black,
    surface = Green500,
    onSurface = Black,
    error = ErrorRed,
    onError = White
)

@Composable
fun AjiriTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
