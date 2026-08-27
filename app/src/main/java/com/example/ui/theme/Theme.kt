package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * UYGULAMA GENELİ RENK KİMLİĞİ
 *
 * Önceden uygulamanın resmi teması (Material "Baseline Purple") ile ekranların çoğunda
 * elle yazılmış Slate/Camgöbeği renkleri (telefon tasarım stüdyosu, yazılım grafikleri,
 * başarım/tedarik zinciri kartları vb.) birbirinden tamamen kopuktu — uygulama aynı anda
 * hem açık mor bir Material You uygulaması hem de koyu bir "geliştirici konsolu" gibi
 * görünüyordu. Bu şema, uygulamada zaten en çok kullanılan Slate + Camgöbeği paletini
 * resmi tema haline getirir; böylece MaterialTheme.colorScheme üzerinden gelen bileşenler
 * (butonlar, seçili sekme, üst çubuk vurguları) ile elle boyanmış özel kartlar artık aynı
 * aileden renkler kullanır.
 */
private val LightColorScheme = lightColorScheme(
    primary = BrandCyan,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0C4A6E),
    secondary = Slate600,
    onSecondary = Color.White,
    secondaryContainer = Slate100,
    onSecondaryContainer = Slate800,
    background = Slate50,
    onBackground = Slate900,
    surface = Color.White,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate600,
    outline = Slate300,
    outlineVariant = Slate200,
    error = Color(0xFFDC2626),
    onError = Color.White,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF7F1D1D)
)

private val DarkColorScheme = darkColorScheme(
    primary = BrandCyanDark,
    onPrimary = Slate900,
    primaryContainer = Color(0xFF0C4A6E),
    onPrimaryContainer = Color(0xFFE0F2FE),
    secondary = Slate300,
    onSecondary = Slate900,
    secondaryContainer = Slate700,
    onSecondaryContainer = Slate100,
    background = Slate900,
    onBackground = Slate100,
    surface = Slate800,
    onSurface = Slate100,
    surfaceVariant = Slate700,
    onSurfaceVariant = Slate300,
    outline = Slate600,
    outlineVariant = Slate700,
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFEE2E2)
)

@Composable
fun MyApplicationTheme(
    // NOT: Karanlık mod kasıtlı olarak kapalı. Uygulamanın pek çok yerinde metin rengi
    // MaterialTheme.colorScheme üzerinden değil, doğrudan sabit (hardcoded) Slate900/800 gibi
    // koyu renklerle yazılmış — bunlar "açık arka plan üzerinde koyu yazı" varsayımıyla
    // tasarlandı. Sistem karanlık modundayken arka plan koyuya dönüp bu sabit koyu yazılar
    // görünmez hale geliyordu (koyu üstüne koyu). Bu yüzden şimdilik sadece açık tema
    // zorlanıyor; gerçek karanlık mod ancak o hardcoded renkler tek tek denetlenip
    // MaterialTheme.colorScheme.onSurface/onBackground gibi tema-duyarlı referanslara
    // çevrildikten sonra güvenle açılabilir.
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false, // Marka kimliğinin tutarlı kalması için dinamik (duvar kağıdı) renk kapalı
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
