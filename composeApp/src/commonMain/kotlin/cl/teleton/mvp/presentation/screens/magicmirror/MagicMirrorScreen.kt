package cl.teleton.mvp.presentation.screens.magicmirror

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.teleton.mvp.domain.usecases.AnalyzeMovementUseCase
import cl.teleton.mvp.presentation.components.CameraPreviewSurface
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory

@Composable
fun MagicMirrorScreen(
    onBackClick: () -> Unit,
    analyzeMovementUseCase: AnalyzeMovementUseCase
) {
    // Inicializar el ViewModel
    val viewModel = getViewModel(
        key = "rehab-view-model",
        factory = viewModelFactory {
            RehabilitationViewModel(analyzeMovementUseCase)
        }
    )
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Componente de la Cámara con detección de poses
        CameraPreviewSurface(
            onPoseDetected = { landmarks ->
                viewModel.processMovement(landmarks)
            }
        )

        // Overlay superior con estadísticas
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .background(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón de regreso
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }

                // Contador de repeticiones
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "REPETICIONES",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "${state.repetitionCount}",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Botón de reinicio
                IconButton(onClick = { viewModel.resetRepetitions() }) {
                    Icon(
                        Icons.Rounded.Refresh,
                        contentDescription = "Reiniciar",
                        tint = Color.White
                    )
                }
            }
        }

        // Panel inferior con feedback y métricas
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
                .padding(20.dp)
        ) {
            // Mensaje de feedback principal
            Text(
                text = state.botMessage ?: "Esperando movimiento...",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Métricas en fila
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Ángulo actual
                MetricCard(
                    label = "ÁNGULO",
                    value = "${state.currentAngle.toInt()}°",
                    color = if (state.currentAngle in 30.0..150.0)
                        Color.Green else Color.Yellow
                )

                // Calidad del movimiento
                MetricCard(
                    label = "CALIDAD",
                    value = "${(state.movementQuality * 100).toInt()}%",
                    color = when {
                        state.movementQuality > 0.8f -> Color.Green
                        state.movementQuality > 0.5f -> Color.Yellow
                        else -> Color.Red
                    }
                )

                // Estado de compensación
                MetricCard(
                    label = "POSTURA",
                    value = if (state.isCompensating) "⚠️" else "✓",
                    color = if (state.isCompensating) Color.Red else Color.Green
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Barra de progreso de calidad
            LinearProgressIndicator(
                progress = { state.movementQuality },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = when {
                    state.movementQuality > 0.8f -> Color.Green
                    state.movementQuality > 0.5f -> Color.Yellow
                    else -> Color.Red
                },
                trackColor = Color.White.copy(alpha = 0.2f)
            )
        }
    }
}

@Composable
private fun MetricCard(
    label: String,
    value: String,
    color: Color
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}