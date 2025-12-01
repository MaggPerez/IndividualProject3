package com.example.individualproject3.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val PuzzleBotColorScheme = lightColorScheme(
    //primary colors
    primary = BrightGreen,
    onPrimary = TextOnColor,
    primaryContainer = LightCyan,
    onPrimaryContainer = TextPrimary,

    //secondary colors
    secondary = BrightBlue,
    onSecondary = TextOnColor,
    secondaryContainer = PowderBlue,
    onSecondaryContainer = TextPrimary,

    //tertiary colors
    tertiary = BrightPink,
    onTertiary = TextOnColor,
    tertiaryContainer = LightPurple,
    onTertiaryContainer = TextPrimary,

    //background colors
    background = SurfaceLight,
    onBackground = TextPrimary,

    //surface colors
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCream,
    onSurfaceVariant = TextSecondary,

    //error colors
    error = ErrorRed,
    onError = TextOnColor,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    //outline colors
    outline = Color(0xFFBDC3C7),
    outlineVariant = Color(0xFFECF0F1),
)

@Composable
fun IndividualProject3Theme(
    darkTheme: Boolean = false,

    //disable dynamic color for consistent branding
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    //always use PuzzleBot color scheme for consistency
    val colorScheme = PuzzleBotColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = SkyBlue.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}