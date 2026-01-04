package com.example.hobbyyk_new.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryOrangeDark,
    onPrimary = Color(0xFF5D2000),
    primaryContainer = Color(0xFF8B3100),
    onPrimaryContainer = Color(0xFFFFDBCB),
    secondary = SecondaryCream,
    onSecondary = Color(0xFF3E2D16),
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    background = SurfaceDark
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryOrange,
    onPrimary = Color.White,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = Color(0xFF765A2D),
    onSecondary = Color.White,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    background = SurfaceLight,

    surfaceVariant = Color(0xFFF5E0D6),
    onSurfaceVariant = Color(0xFF53433A)
)

@Composable
fun HobbyYKNEWTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}