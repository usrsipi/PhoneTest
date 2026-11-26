package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DeepOceanDarkColorScheme =
  darkColorScheme(
    primary = BiolumCyan,
    onPrimary = OceanAbyss,
    primaryContainer = OceanElevated,
    onPrimaryContainer = BiolumGlow,
    secondary = SeafoamTeal,
    onSecondary = OceanAbyss,
    secondaryContainer = OceanTrench,
    onSecondaryContainer = SeafoamTeal,
    background = OceanAbyss,
    onBackground = IceWhite,
    surface = OceanTrench,
    onSurface = IceWhite,
    surfaceVariant = OceanCardGlass,
    onSurfaceVariant = OceanMist,
    outline = OceanRim,
    outlineVariant = OceanSubtle,
  )

@Composable
fun MyApplicationTheme(content: @Composable () -> Unit) {
  MaterialTheme(
    colorScheme = DeepOceanDarkColorScheme,
    typography = Typography,
    content = content,
  )
}
