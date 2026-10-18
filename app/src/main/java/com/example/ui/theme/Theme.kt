package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.data.model.ThemeContrastMode

private val MidnightBlueColorScheme = darkColorScheme(
    primary = MxCyan,
    onPrimary = Color.Black,
    primaryContainer = MxNeonBlue,
    onPrimaryContainer = Color.White,
    secondary = MxCyan,
    onSecondary = Color.Black,
    background = MidnightBackground,
    onBackground = MidnightTextPrimary,
    surface = MidnightSurface,
    onSurface = MidnightTextPrimary,
    surfaceVariant = MidnightSurfaceVariant,
    onSurfaceVariant = MidnightTextSecondary,
    outline = Color(0xFF2A3654)
)

private val OledColorScheme = darkColorScheme(
    primary = MxCyan,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF003852),
    onPrimaryContainer = MxCyan,
    secondary = MxAccentOrange,
    onSecondary = Color.Black,
    background = OledBackground,
    onBackground = OledTextPrimary,
    surface = OledSurface,
    onSurface = OledTextPrimary,
    surfaceVariant = OledSurfaceVariant,
    onSurfaceVariant = OledTextSecondary,
    outline = Color(0xFF333333)
)

private val HighContrastColorScheme = darkColorScheme(
    primary = HighContrastYellow,
    onPrimary = Color.Black,
    primaryContainer = HighContrastCyan,
    onPrimaryContainer = Color.Black,
    secondary = HighContrastCyan,
    onSecondary = Color.Black,
    background = HighContrastBackground,
    onBackground = HighContrastText,
    surface = HighContrastSurface,
    onSurface = HighContrastText,
    surfaceVariant = Color(0xFF1F1F1F),
    onSurfaceVariant = HighContrastYellow,
    outline = HighContrastBorder
)

private val SepiaColorScheme = darkColorScheme(
    primary = SepiaAmber,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF4A3414),
    onPrimaryContainer = Color(0xFFFFD599),
    secondary = SepiaAmber,
    onSecondary = Color.Black,
    background = SepiaBackground,
    onBackground = SepiaTextPrimary,
    surface = SepiaSurface,
    onSurface = SepiaTextPrimary,
    surfaceVariant = SepiaSurfaceVariant,
    onSurfaceVariant = SepiaTextSecondary,
    outline = Color(0xFF4A3C2D)
)

@Composable
fun MyApplicationTheme(
    contrastMode: ThemeContrastMode = ThemeContrastMode.MIDNIGHT_BLUE,
    content: @Composable () -> Unit
) {
    val colorScheme = when (contrastMode) {
        ThemeContrastMode.MIDNIGHT_BLUE -> MidnightBlueColorScheme
        ThemeContrastMode.OLED_DARK -> OledColorScheme
        ThemeContrastMode.HIGH_CONTRAST_ACCESSIBILITY -> HighContrastColorScheme
        ThemeContrastMode.SEPIA_NIGHT -> SepiaColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
