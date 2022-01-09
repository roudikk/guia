package com.roudikk.navigator.sample.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.google.accompanist.insets.ProvideWindowInsets
import androidx.compose.material.MaterialTheme as MaterialTheme2

private val LightThemeColors = lightColorScheme(

    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
)
private val DarkThemeColors = darkColorScheme(

    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
)

@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) = ProvideWindowInsets {

    val colors = if (useDarkTheme) {
        DarkThemeColors
    } else {
        LightThemeColors
    }

    val colors2 = if (useDarkTheme) {
        darkColors(
            primary = DarkThemeColors.primary,
            secondary = DarkThemeColors.secondary,
            secondaryVariant = DarkThemeColors.secondary,
            background = DarkThemeColors.background,
            surface = DarkThemeColors.surface,
            onPrimary = DarkThemeColors.onPrimary,
            onSecondary = DarkThemeColors.onSecondary,
            onBackground = DarkThemeColors.onBackground,
            onSurface = DarkThemeColors.onSurface
        )
    } else {
        lightColors(
            primary = LightThemeColors.primary,
            secondary = LightThemeColors.secondary,
            secondaryVariant = LightThemeColors.secondary,
            background = LightThemeColors.background,
            surface = LightThemeColors.surface,
            onPrimary = LightThemeColors.onPrimary,
            onSecondary = LightThemeColors.onSecondary,
            onBackground = LightThemeColors.onBackground,
            onSurface = LightThemeColors.onSurface
        )
    }
    MaterialTheme2(
        colors = colors2
    ) {
        MaterialTheme(
            colorScheme = colors,
            typography = AppTypography,
        ) {

            Surface(content = content)
        }
    }
}