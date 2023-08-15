package com.sebastianvm.musicplayer.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightThemeColors = lightColorScheme(
    primary = Color.mdThemeLightPrimary,
    onPrimary = Color.mdThemeLightOnPrimary,
    primaryContainer = Color.mdThemeLightPrimaryContainer,
    onPrimaryContainer = Color.mdThemeLightOnPrimaryContainer,
    secondary = Color.mdThemeLightSecondary,
    onSecondary = Color.mdThemeLightOnSecondary,
    secondaryContainer = Color.mdThemeLightSecondaryContainer,
    onSecondaryContainer = Color.mdThemeLightOnSecondaryContainer,
    tertiary = Color.mdThemeLightTertiary,
    onTertiary = Color.mdThemeLightOnTertiary,
    tertiaryContainer = Color.mdThemeLightTertiaryContainer,
    onTertiaryContainer = Color.mdThemeLightOnTertiaryContainer,
    error = Color.mdThemeLightError,
    errorContainer = Color.mdThemeLightErrorContainer,
    onError = Color.mdThemeLightOnError,
    onErrorContainer = Color.mdThemeLightOnErrorContainer,
    background = Color.mdThemeLightBackground,
    onBackground = Color.mdThemeLightOnBackground,
    surface = Color.mdThemeLightSurface,
    onSurface = Color.mdThemeLightOnSurface,
    surfaceVariant = Color.mdThemeLightSurfaceVariant,
    onSurfaceVariant = Color.mdThemeLightOnSurfaceVariant,
    outline = Color.mdThemeLightOutline,
    inverseOnSurface = Color.mdThemeLightInverseOnSurface,
    inverseSurface = Color.mdThemeLightInverseSurface
)
private val DarkThemeColors = darkColorScheme(
    primary = Color.mdThemeDarkPrimary,
    onPrimary = Color.mdThemeDarkOnPrimary,
    primaryContainer = Color.mdThemeDarkPrimaryContainer,
    onPrimaryContainer = Color.mdThemeDarkOnPrimaryContainer,
    secondary = Color.mdThemeDarkSecondary,
    onSecondary = Color.mdThemeDarkOnSecondary,
    secondaryContainer = Color.mdThemeDarkSecondaryContainer,
    onSecondaryContainer = Color.mdThemeDarkOnSecondaryContainer,
    tertiary = Color.mdThemeDarkTertiary,
    onTertiary = Color.mdThemeDarkOnTertiary,
    tertiaryContainer = Color.mdThemeDarkTertiaryContainer,
    onTertiaryContainer = Color.mdThemeDarkOnTertiaryContainer,
    error = Color.mdThemeDarkError,
    errorContainer = Color.mdThemeDarkErrorContainer,
    onError = Color.mdThemeDarkOnError,
    onErrorContainer = Color.mdThemeDarkOnErrorContainer,
    background = Color.mdThemeDarkBackground,
    onBackground = Color.mdThemeDarkOnBackground,
    surface = Color.mdThemeDarkSurface,
    onSurface = Color.mdThemeDarkOnSurface,
    surfaceVariant = Color.mdThemeDarkSurfaceVariant,
    onSurfaceVariant = Color.mdThemeDarkOnSurfaceVariant,
    outline = Color.mdThemeDarkOutline,
    inverseOnSurface = Color.mdThemeDarkInverseOnSurface,
    inverseSurface = Color.mdThemeDarkInverseSurface
)

@Composable
fun M3AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        useDarkTheme -> DarkThemeColors
        else -> LightThemeColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
