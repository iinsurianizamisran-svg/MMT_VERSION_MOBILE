package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ClinicalTealPrimaryDark,
    onPrimary = Color(0xFF003730),
    primaryContainer = ClinicalTealContainerDark,
    onPrimaryContainer = Color(0xFFA7F0E4),
    secondary = Color(0xFF80DEEA),
    onSecondary = Color(0xFF00363A),
    secondaryContainer = Color(0xFF004D56),
    onSecondaryContainer = Color(0xFFB2EBF2),
    tertiary = Color(0xFFFBBF24),
    error = Color(0xFFF87171),
    background = MedicalBackgroundDark,
    surface = MedicalSurfaceDark,
    surfaceVariant = MedicalSurfaceVariantDark,
    onBackground = Color(0xFFF1F5F9),
    onSurface = Color(0xFFF1F5F9),
    outline = Color(0xFF475569)
)

private val LightColorScheme = lightColorScheme(
    primary = ClinicalTealPrimary,
    onPrimary = ClinicalTealOnPrimary,
    primaryContainer = ClinicalTealContainer,
    onPrimaryContainer = ClinicalTealOnContainer,
    secondary = MedicalCyanSecondary,
    onSecondary = MedicalCyanOnSecondary,
    secondaryContainer = MedicalCyanContainer,
    onSecondaryContainer = MedicalCyanOnContainer,
    tertiary = ClinicalAmberTertiary,
    tertiaryContainer = ClinicalAmberContainer,
    onTertiaryContainer = ClinicalAmberOnContainer,
    error = ClinicalCriticalRed,
    errorContainer = ClinicalCriticalContainer,
    onErrorContainer = ClinicalCriticalOnContainer,
    background = MedicalBackgroundLight,
    surface = MedicalSurfaceLight,
    surfaceVariant = MedicalSurfaceVariantLight,
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A),
    outline = MedicalOutlineLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Prefer tailored clinical palette
    content: @Composable () -> Unit,
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
