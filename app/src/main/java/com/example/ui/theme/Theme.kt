package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = GeoPrimaryContainer,
  onPrimary = GeoOnPrimaryContainer,
  primaryContainer = GeoPrimary,
  onPrimaryContainer = Color.White,
  secondary = GeoPrimaryContainer,
  onSecondary = GeoOnPrimaryContainer,
  tertiary = GamingAmber,
  background = Color(0xFF111318),
  onBackground = Color(0xFFE2E2E9),
  surface = Color(0xFF111318),
  onSurface = Color(0xFFE2E2E9),
  surfaceVariant = Color(0xFF232832),
  onSurfaceVariant = Color(0xFFC3C6CF),
  outline = GeoOutline,
  error = GamingRose
)

private val LightColorScheme = lightColorScheme(
  primary = GeoPrimary,
  onPrimary = GeoOnPrimary,
  primaryContainer = GeoPrimaryContainer,
  onPrimaryContainer = GeoOnPrimaryContainer,
  secondary = GeoPrimary,
  onSecondary = GeoOnPrimary,
  tertiary = GamingAmber,
  background = GeoBackground,
  onBackground = GeoOnBackground,
  surface = GeoSurface,
  onSurface = GeoOnSurface,
  surfaceVariant = GeoSurfaceVariant,
  onSurfaceVariant = GeoOnSurfaceVariant,
  outline = GeoOutlineVariant,
  error = GamingRose
)

@Composable
fun GamingBacklogTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

