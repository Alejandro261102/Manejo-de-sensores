package com.example.sensorappipn.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color // <--- Esta era la importación que faltaba
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Esquema Oscuro
private val DarkColorScheme = darkColorScheme(
    primary = GuindaIPN,
    secondary = AzulESCOM,
    tertiary = Pink80
)

// Esquema Claro
private val LightColorScheme = lightColorScheme(
    primary = GuindaIPN,
    secondary = AzulESCOM,
    tertiary = Pink40
)

@Composable
fun SensorAppIPNTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    isEscomTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    // Definimos los colores base según el switch de tema
    val primaryColor = if (isEscomTheme) AzulESCOM else GuindaIPN

    val colorScheme = when {
        darkTheme -> DarkColorScheme.copy(
            primary = primaryColor,
            background = if(isEscomTheme) Color(0xFF001A33) else Color(0xFF2D0A1C)
        )
        else -> LightColorScheme.copy(primary = primaryColor)
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}