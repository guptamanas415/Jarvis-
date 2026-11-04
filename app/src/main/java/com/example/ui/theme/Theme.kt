package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val JarvisNeonDarkColorScheme = darkColorScheme(
    primary = JarvisCyan,
    onPrimary = Color(0xFF00373A),
    primaryContainer = JarvisPrimaryContainer,
    onPrimaryContainer = JarvisCyanLight,
    secondary = JarvisCyanLight,
    onSecondary = Color(0xFF00373A),
    secondaryContainer = JarvisCyanContainer,
    onSecondaryContainer = Color(0xFF006A6F),
    tertiary = JarvisTertiary,
    onTertiary = Color(0xFF003256),
    tertiaryContainer = JarvisTertiaryContainer,
    onTertiaryContainer = Color(0xFF007DC9),
    background = JarvisBackground,
    onBackground = JarvisOnSurface,
    surface = JarvisSurface,
    onSurface = JarvisOnSurface,
    surfaceVariant = JarvisSurfaceContainerHighest,
    onSurfaceVariant = JarvisOnSurfaceVariant,
    outline = JarvisOutline,
    outlineVariant = JarvisOutlineVariant,
    error = JarvisError,
    onError = JarvisOnError,
    errorContainer = JarvisErrorContainer,
    surfaceContainerLowest = JarvisSurfaceContainerLowest,
    surfaceContainerLow = JarvisSurfaceContainerLow,
    surfaceContainer = JarvisSurfaceContainer,
    surfaceContainerHigh = JarvisSurfaceContainerHigh,
    surfaceContainerHighest = JarvisSurfaceContainerHighest
)

val JarvisDeepSpaceColorScheme = darkColorScheme(
    primary = Color(0xFF818CF8),
    onPrimary = Color(0xFF1E1B4B),
    primaryContainer = Color(0xFF0F172A),
    secondary = Color(0xFFC7D2FE),
    onSecondary = Color(0xFF1E1B4B),
    background = Color(0xFF020617),
    onBackground = Color(0xFFF1F5F9),
    surface = Color(0xFF020617),
    onSurface = Color(0xFFF1F5F9),
    surfaceContainer = Color(0xFF0F172A),
    surfaceContainerHigh = Color(0xFF1E293B),
    surfaceContainerHighest = Color(0xFF334155),
    error = JarvisError
)

val JarvisMatrixColorScheme = darkColorScheme(
    primary = Color(0xFF34D399),
    onPrimary = Color(0xFF064E3B),
    primaryContainer = Color(0xFF022C22),
    secondary = Color(0xFFA7F3D0),
    onSecondary = Color(0xFF064E3B),
    background = Color(0xFF021008),
    onBackground = Color(0xFFECFDF5),
    surface = Color(0xFF021008),
    onSurface = Color(0xFFECFDF5),
    surfaceContainer = Color(0xFF064E3B),
    surfaceContainerHigh = Color(0xFF047857),
    surfaceContainerHighest = Color(0xFF059669),
    error = JarvisError
)

@Composable
fun JarvisTheme(
    hudTheme: String = "neon",
    content: @Composable () -> Unit
) {
    val colorScheme = when (hudTheme.lowercase()) {
        "space", "deep space" -> JarvisDeepSpaceColorScheme
        "matrix" -> JarvisMatrixColorScheme
        else -> JarvisNeonDarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Retain legacy name if referenced anywhere
@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    JarvisTheme(content = content)
}
