package cl.teleton.mvp.presentation.theme



import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 1. Esquema de Colores CLARO (Usando los colores vibrantes)
private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlueVibrant,      // Azul vibrante
    onPrimary = Color.White,
    secondary = AccentCyan,            // Cyan de acento
    onSecondary = Color.White,
    tertiary = SecondaryYellow,        // Amarillo de acento
    background = BackgroundVibrant,    // Fondo muy claro
    surface = SurfaceVibrant,          // Superficie blanca
    onSurface = Color.Black
)

/**
 * Tema principal para la aplicación TeleHero.
 */
@Composable
fun TeleHeroTheme(
    content: @Composable () -> Unit
) {
    // Usamos el esquema de colores vibrante
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}