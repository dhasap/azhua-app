package com.azhua.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * ColorScheme untuk Tema Kultivator
 * Tema gelap yang elegan dengan aksen Jade Green dan Imperial Gold
 */
private val CultivatorColorScheme = darkColorScheme(
    // Primary colors
    primary = JadeGreen,
    onPrimary = ObsidianBlack,
    primaryContainer = DeepJade,
    onPrimaryContainer = LightJade,

    // Secondary colors
    secondary = LightJade,
    onSecondary = ObsidianBlack,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = LightJade,

    // Tertiary (Accent)
    tertiary = ImperialGold,
    onTertiary = ObsidianBlack,
    tertiaryContainer = ImperialGoldDark,
    onTertiaryContainer = TextWhite,

    // Background & Surface
    background = ObsidianBlack,
    onBackground = TextWhite,
    surface = DarkSurface,
    onSurface = TextWhite,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSilver,

    // Error
    error = CrimsonRed,
    onError = TextWhite,
    errorContainer = CrimsonRed.copy(alpha = 0.2f),
    onErrorContainer = CrimsonRed,

    // Additional
    outline = DividerColor,
    outlineVariant = DividerColor.copy(alpha = 0.5f),
    scrim = ScrimColor,
    inverseSurface = TextWhite,
    inverseOnSurface = ObsidianBlack,
    inversePrimary = LightJade,
    surfaceTint = JadeGreen
)

/**
 * Theme utama aplikasi AzHua
 * Menggunakan tema gelap (Dark Theme) secara default sesuai tema Kultivator
 */
@Composable
fun AzHuaTheme(
    // Kita paksakan Dark Theme agar sesuai tema kultivator
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = CultivatorColorScheme
    val view = LocalView.current

    // Setup system bars (status bar & navigation bar)
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            
            // Status bar color sama dengan background untuk seamless look
            window.statusBarColor = colorScheme.background.toArgb()
            
            // Navigation bar juga sama
            window.navigationBarColor = colorScheme.surface.toArgb()
            
            // Light status bar icons (false = dark icons untuk dark theme)
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

/**
 * Helper untuk mendapatkan warna gradient Jade
 */
object CultivatorColors {
    val gradientJade = listOf(JadeGreen, LightJade)
    val gradientObsidian = listOf(ObsidianBlack, DarkSurface)
    val gradientGold = listOf(ImperialGold, ImperialGoldDark)
}
