package com.example.shefarmai.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = SheFarmGreenPrimaryDark,
    onPrimary = SheFarmOnPrimaryDark,
    primaryContainer = SheFarmPrimaryContainerDark,
    onPrimaryContainer = SheFarmOnPrimaryContainerDark,
    secondary = SheFarmSecondaryDark,
    secondaryContainer = SheFarmSecondaryContainerDark,
    onSecondaryContainer = SheFarmOnSecondaryContainerDark,
    tertiary = SheFarmTertiaryDark,
    tertiaryContainer = SheFarmTertiaryContainerDark,
    onTertiaryContainer = SheFarmOnTertiaryContainerDark,
    background = SheFarmBackgroundDark,
    surface = SheFarmSurfaceDark,
    surfaceVariant = SheFarmSurfaceVariantDark,
    onSurface = SheFarmOnSurfaceDark,
    onSurfaceVariant = SheFarmOnSurfaceVariantDark
)

private val LightColorScheme = lightColorScheme(
    primary = SheFarmGreenPrimary,
    onPrimary = SheFarmOnPrimary,
    primaryContainer = SheFarmPrimaryContainer,
    onPrimaryContainer = SheFarmOnPrimaryContainer,
    secondary = SheFarmSecondary,
    secondaryContainer = SheFarmSecondaryContainer,
    onSecondaryContainer = SheFarmOnSecondaryContainer,
    tertiary = SheFarmTertiary,
    tertiaryContainer = SheFarmTertiaryContainer,
    onTertiaryContainer = SheFarmOnTertiaryContainer,
    background = SheFarmBackground,
    surface = SheFarmSurface,
    surfaceVariant = SheFarmSurfaceVariant,
    onSurface = SheFarmOnSurface,
    onSurfaceVariant = SheFarmOnSurfaceVariant
)

@Composable
fun SheFarmAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}