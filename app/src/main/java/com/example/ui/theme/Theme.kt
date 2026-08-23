package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Default Midnight Gold (Dark)
private val MidnightGoldScheme = darkColorScheme(
    primary = Color(0xFFF5820A),
    onPrimary = Color.White,
    secondary = Color(0xFF00E5FF),
    tertiary = Color(0xFF8B5CF6),
    background = Color(0xFF0B1220),
    surface = Color(0xFF151E2E),
    onBackground = Color(0xFFFFFFFF),
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF202C3F),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF202C3F)
)

// Devotional Light
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFE65100),
    onPrimary = Color.White,
    secondary = Color(0xFF0288D1),
    tertiary = Color(0xFF7B1FA2),
    background = Color(0xFFF8FAF9),
    surface = Color(0xFFFFFFFF),
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF64748B),
    outline = Color(0xFFCBD5E1)
)

// Vrindavan Sunrise
private val VrindavanSunriseScheme = darkColorScheme(
    primary = Color(0xFFFF6D00),
    onPrimary = Color.White,
    secondary = Color(0xFFFFD600),
    tertiary = Color(0xFFFFAB40),
    background = Color(0xFF1A0C00),
    surface = Color(0xFF2A1505),
    onBackground = Color(0xFFFFF3E0),
    onSurface = Color(0xFFFFF3E0),
    surfaceVariant = Color(0xFF3E200B),
    onSurfaceVariant = Color(0xFFFFCC80),
    outline = Color(0xFF4E2A10)
)

// Peacock Blue
private val PeacockBlueScheme = darkColorScheme(
    primary = Color(0xFF00B0FF),
    onPrimary = Color.White,
    secondary = Color(0xFF1DE9B6),
    tertiary = Color(0xFF00E5FF),
    background = Color(0xFF001F2D),
    surface = Color(0xFF002B3D),
    onBackground = Color(0xFFE0F7FA),
    onSurface = Color(0xFFE0F7FA),
    surfaceVariant = Color(0xFF003850),
    onSurfaceVariant = Color(0xFF80DEEA),
    outline = Color(0xFF004866)
)

// Radha Pink
private val RadhaPinkScheme = darkColorScheme(
    primary = Color(0xFFFF4081),
    onPrimary = Color.White,
    secondary = Color(0xFFFF80AB),
    tertiary = Color(0xFFEA80FC),
    background = Color(0xFF1F0A14),
    surface = Color(0xFF2D1220),
    onBackground = Color(0xFFFCE4EC),
    onSurface = Color(0xFFFCE4EC),
    surfaceVariant = Color(0xFF3F1B2F),
    onSurfaceVariant = Color(0xFFF8BBD0),
    outline = Color(0xFF53243F)
)

// Temple Sandalwood
private val TempleSandalwoodScheme = lightColorScheme(
    primary = Color(0xFF8D6E63),
    onPrimary = Color.White,
    secondary = Color(0xFF6D4C41),
    tertiary = Color(0xFFA1887F),
    background = Color(0xFFFAF8F5),
    surface = Color(0xFFFFFDFB),
    onBackground = Color(0xFF3E2723),
    onSurface = Color(0xFF3E2723),
    surfaceVariant = Color(0xFFEFEBE9),
    onSurfaceVariant = Color(0xFF6D4C41),
    outline = Color(0xFFD7CCC8)
)

@Composable
fun JaapTheme(
    themeMode: String = "DARK",
    content: @Composable () -> Unit
) {
    val isSysDark = isSystemInDarkTheme()
    val colorScheme = when (themeMode) {
        "VRINDAVAN_SUNRISE" -> VrindavanSunriseScheme
        "PEACOCK_BLUE" -> PeacockBlueScheme
        "RADHA_PINK" -> RadhaPinkScheme
        "TEMPLE_SANDALWOOD" -> TempleSandalwoodScheme
        "LIGHT" -> LightColorScheme
        "SYSTEM" -> if (isSysDark) MidnightGoldScheme else LightColorScheme
        else -> MidnightGoldScheme // MIDNIGHT_GOLD / DARK
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
