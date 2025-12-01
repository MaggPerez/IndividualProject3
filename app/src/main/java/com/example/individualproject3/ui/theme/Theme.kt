package com.example.individualproject3.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

// Light Color Scheme - Bright & Playful
private val LightColorScheme = lightColorScheme(
    primary = NavyBlue,
    onPrimary = White,
    primaryContainer = LightBlue,
    onPrimaryContainer = NavyBlue,

    secondary = CoralOrange,
    onSecondary = White,
    secondaryContainer = SoftCoral,
    onSecondaryContainer = NavyBlue,

    tertiary = GoldenYellow,
    onTertiary = NavyBlue,
    tertiaryContainer = GoldenYellow.copy(alpha = 0.3f),
    onTertiaryContainer = NavyBlue,

    error = ErrorRed,
    onError = White,
    errorContainer = ErrorRed.copy(alpha = 0.2f),
    onErrorContainer = ErrorRed,

    background = LightGray,
    onBackground = DarkGray,

    surface = SurfaceLight,
    onSurface = DarkGray,
    surfaceVariant = LightBlue.copy(alpha = 0.2f),
    onSurfaceVariant = NavyBlue,

    outline = MediumGray,
    outlineVariant = LightBlue
)

// Dark Color Scheme - For dark mode
private val DarkColorScheme = darkColorScheme(
    primary = SkyBlue,
    onPrimary = NavyBlue,
    primaryContainer = NavyBlue,
    onPrimaryContainer = LightBlue,

    secondary = CoralOrange,
    onSecondary = White,
    secondaryContainer = SoftCoral,
    onSecondaryContainer = White,

    tertiary = GoldenYellow,
    onTertiary = NavyBlue,
    tertiaryContainer = GoldenYellow.copy(alpha = 0.2f),
    onTertiaryContainer = GoldenYellow,

    error = ErrorRed,
    onError = White,
    errorContainer = ErrorRed.copy(alpha = 0.3f),
    onErrorContainer = ErrorRed,

    background = SurfaceDark,
    onBackground = White,

    surface = SurfaceDark,
    onSurface = White,
    surfaceVariant = NavyBlue.copy(alpha = 0.3f),
    onSurfaceVariant = SkyBlue,

    outline = MediumGray,
    outlineVariant = NavyBlue
)

// Custom Shapes - Rounded Corners Throughout
val PuzzleBotShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),    // Small chips, badges
    small = RoundedCornerShape(12.dp),        // Small buttons, inputs
    medium = RoundedCornerShape(16.dp),       // Cards, large buttons
    large = RoundedCornerShape(20.dp),        // Dialogs, bottom sheets
    extraLarge = RoundedCornerShape(28.dp)    // Large containers
)

@Composable
fun IndividualProject3Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // ALWAYS use custom colors - dynamic theming disabled
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = PuzzleBotShapes,  // Apply custom rounded shapes
        content = content
    )
}
