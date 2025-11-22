package cl.teleton.mvp.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cl.teleton.mvp.presentation.theme.PlayButtonGradient
import cl.teleton.mvp.presentation.theme.TeleHeroBackgroundGradient

/**
 * Componente Tarjeta de Información
 * Usa los colores de superficie del tema (blanco) para destacar sobre el fondo.
 */
@Composable
fun InfoCard(title: String, content: String) {
    // Usamos Surface para tener elevación y bordes redondeados
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium, // Bordes redondeados (del tema)
        color = MaterialTheme.colorScheme.surface, // Fondo blanco (SurfaceVibrant)
        shadowElevation = 4.dp // Pequeña sombra
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // El color del texto usa onSurface, el color para el contenido de la superficie
            Text(
                title,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Spacer(Modifier.height(4.dp))
            Text(
                content,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

/**
 * Pantalla principal de inicio.
 * Aplica el gradiente de fondo y el botón de jugar con gradiente.
 */
@Composable
fun HomeScreen(onPlayClick: () -> Unit) {
    // 1. Fondo: Aplicamos el gradiente de fondo al Box principal
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TeleHeroBackgroundGradient) // <--- GRADIENTE TELEHERO
            .systemBarsPadding()
    ) {
        // Contenido principal
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 40.dp, bottom = 120.dp), // Espacio para botón
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título: Color blanco para contraste con el fondo azul
            Text(
                "TeleHero MVP",
                style = MaterialTheme.typography.headlineLarge.copy(color = Color.White)
            )
            Spacer(Modifier.height(16.dp))

            // Tarjetas de información
            InfoCard(
                title = "Objetivo O4/O5 (IA)",
                content = "Modelo Q-Learning para evaluar y motivar el movimiento."
            )
            InfoCard(
                title = "Objetivo O3 (Visión)",
                content = "MediaPipe Pose para detección de 33 puntos (Landmarks)."
            )
        }

        // 2. Botón JUGAR: Usamos Surface para aplicar el gradiente
        Surface(
            onClick = onPlayClick, // Acción al hacer clic
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(64.dp)
                .align(Alignment.BottomCenter) // Anclado en la parte inferior central
                .padding(bottom = 32.dp),
            shape = MaterialTheme.shapes.extraLarge, // Bordes muy redondeados para estilo juvenil
            color = Color.Transparent, // Importante: Hacer la superficie transparente
            shadowElevation = 8.dp
        ) {
            // Este Box toma el gradiente como fondo
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PlayButtonGradient, MaterialTheme.shapes.extraLarge), // <--- GRADIENTE DEL BOTÓN
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "JUGAR - Nivel 5",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary // Texto blanco
                )
            }
        }
    }
}