package cl.teleton.mvp.presentation.theme



import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Gradiente de fondo principal: Más dinámico
val TeleHeroBackgroundGradient = Brush.verticalGradient(
    colors = listOf(
        AccentCyan,           // Cyan arriba (Fresco)
        PrimaryBlueVibrant,   // Azul medio
        PrimaryDarkVibrant    // Azul oscuro abajo (Anclaje)
    ),
    startY = 0.0f,
    endY = Float.POSITIVE_INFINITY
)

// Gradiente para botones de énfasis (JUGAR)
val PlayButtonGradient = Brush.horizontalGradient(
    colors = listOf(
        SecondaryYellow,
        Color(0xFFFF9800) // Naranja brillante
    )
)