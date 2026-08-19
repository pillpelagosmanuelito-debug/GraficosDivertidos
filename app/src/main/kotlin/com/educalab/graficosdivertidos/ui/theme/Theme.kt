package com.educalab.graficosdivertidos.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkScheme = darkColorScheme(
    primary = Violet,
    onPrimary = Color.White,
    secondary = Teal,
    tertiary = Amber,
    background = Navy,
    onBackground = SurfaceLight,
    surface = Navy2,
    onSurface = SurfaceLight,
    surfaceVariant = NavyLight,
    error = ErrorRed,
)

private val LightScheme = lightColorScheme(
    primary = Violet,
    onPrimary = Color.White,
    secondary = TealDark,
    tertiary = AmberDark,
    background = SurfaceLight,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = SurfaceMuted,
    error = ErrorRed,
)

@Composable
fun GraficosDivertidosTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (useDarkTheme) DarkScheme else LightScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = GraficosTypography,
        shapes = GraficosShapes,
        content = content,
    )
}
