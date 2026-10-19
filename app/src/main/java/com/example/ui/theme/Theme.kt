package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.data.model.ThemeContrastMode

private val CyberDarkColorScheme = darkColorScheme(
    primary = CyberPrimary,
    onPrimary = Color.Black,
    primaryContainer = CyberSecondary,
    onPrimaryContainer = Color.White,
    secondary = CyberSecondary,
    onSecondary = Color.White,
    background = CyberDarkBackground,
    onBackground = CyberDarkTextPrimary,
    surface = CyberDarkSurface,
    onSurface = CyberDarkTextPrimary,
    surfaceVariant = CyberDarkSurfaceVariant,
    onSurfaceVariant = CyberDarkTextSecondary,
    outline = Color(0xFF2D2A4A)
)

private val CyberLightColorScheme = lightColorScheme(
    primary = Color(0xFF512DA8),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1C4E9),
    onPrimaryContainer = Color(0xFF311B92),
    secondary = Color(0xFF00ACC1),
    onSecondary = Color.White,
    background = CyberLightBackground,
    onBackground = CyberLightTextPrimary,
    surface = CyberLightSurface,
    onSurface = CyberLightTextPrimary,
    surfaceVariant = CyberLightSurfaceVariant,
    onSurfaceVariant = CyberLightTextSecondary,
    outline = Color(0xFFB39DDB)
)

private val EmeraldColorScheme = darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.Black,
    primaryContainer = EmeraldSecondary,
    onPrimaryContainer = Color.Black,
    secondary = EmeraldSecondary,
    onSecondary = Color.Black,
    background = EmeraldBackground,
    onBackground = Color(0xFFE8F5E9),
    surface = EmeraldSurface,
    onSurface = Color(0xFFE8F5E9),
    surfaceVariant = Color(0xFF1B3A26),
    onSurfaceVariant = Color(0xFF81C784),
    outline = Color(0xFF265A3B)
)

private val SunsetColorScheme = darkColorScheme(
    primary = SunsetPrimary,
    onPrimary = Color.Black,
    primaryContainer = SunsetSecondary,
    onPrimaryContainer = Color.White,
    secondary = SunsetSecondary,
    onSecondary = Color.White,
    background = SunsetBackground,
    onBackground = Color(0xFFFBE9E7),
    surface = SunsetSurface,
    onSurface = Color(0xFFFBE9E7),
    surfaceVariant = Color(0xFF3E2219),
    onSurfaceVariant = Color(0xFFFFCCBC),
    outline = Color(0xFF5D2E1F)
)

private val IndigoColorScheme = darkColorScheme(
    primary = IndigoPrimary,
    onPrimary = Color.White,
    primaryContainer = IndigoSecondary,
    onPrimaryContainer = Color.Black,
    secondary = IndigoSecondary,
    onSecondary = Color.Black,
    background = IndigoBackground,
    onBackground = Color(0xFFE8EAF6),
    surface = IndigoSurface,
    onSurface = Color(0xFFE8EAF6),
    surfaceVariant = Color(0xFF1E2749),
    onSurfaceVariant = Color(0xFF9FA8DA),
    outline = Color(0xFF303F9F)
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
    contrastMode: ThemeContrastMode = ThemeContrastMode.CYBER_VIOLET,
    content: @Composable () -> Unit
) {
    val colorScheme = when (contrastMode) {
        ThemeContrastMode.CYBER_VIOLET -> CyberDarkColorScheme
        ThemeContrastMode.ELECTRIC_LIGHT -> CyberLightColorScheme
        ThemeContrastMode.EMERALD_GREEN -> EmeraldColorScheme
        ThemeContrastMode.SUNSET_ORANGE -> SunsetColorScheme
        ThemeContrastMode.ROYAL_INDIGO -> IndigoColorScheme
        ThemeContrastMode.HIGH_CONTRAST_ACCESSIBILITY -> HighContrastColorScheme
        ThemeContrastMode.SEPIA_NIGHT -> SepiaColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
