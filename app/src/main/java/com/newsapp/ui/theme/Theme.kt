package com.newsapp.ui.theme

import android.app.Activity
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

private val LightColors = lightColorScheme(
    primary = RedPrimary,
    onPrimary = Color.White,
    primaryContainer = RedContainer,
    onPrimaryContainer = Color(0xFF410002),
    secondary = RedAccent,
    onSecondary = Color.White,
    secondaryContainer = RedContainer,
    onSecondaryContainer = Color(0xFF410002),
    tertiary = RedDark,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDAD6),
    onTertiaryContainer = Color(0xFF410002),
    background = RedBackground,
    onBackground = Color(0xFF211A19),
    surface = Color.White,
    onSurface = Color(0xFF211A19),
    surfaceVariant = Color(0xFFF5DDDA),
    surfaceContainer = Color(0xFFFFEFED),
    surfaceContainerHigh = Color(0xFFFFE5E2),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFFB4AB),
    onPrimary = Color(0xFF690005),
    primaryContainer = DarkContainer,
    onPrimaryContainer = Color(0xFFFFDAD6),
    secondary = Color(0xFFFFB4AB),
    onSecondary = Color(0xFF690005),
    secondaryContainer = Color(0xFF93000A),
    onSecondaryContainer = Color(0xFFFFDAD6),
    tertiary = Color(0xFFFFB4AB),
    onTertiary = Color(0xFF690005),
    tertiaryContainer = Color(0xFF93000A),
    onTertiaryContainer = Color(0xFFFFDAD6),
    background = DarkBackground,
    onBackground = Color(0xFFF1DEDC),
    surface = DarkSurface,
    onSurface = Color(0xFFF1DEDC),
    surfaceVariant = Color(0xFF534341),
    surfaceContainer = Color(0xFF302524),
    surfaceContainerHigh = Color(0xFF3B302E),
)

@Composable
fun NewsAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
