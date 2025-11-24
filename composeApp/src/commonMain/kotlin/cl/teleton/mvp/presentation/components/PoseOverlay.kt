package cl.teleton.mvp.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import cl.teleton.mvp.domain.models.Landmark
import cl.teleton.mvp.domain.models.LandmarkIds

@Composable
fun PoseOverlay(
    landmarks: List<Landmark>,
    modifier: Modifier = Modifier,
    pointColor: Color = Color.Green,
    lineColor: Color = Color.Cyan,
    pointRadius: Float = 8f,
    lineWidth: Float = 4f,
    confidenceThreshold: Float = 0.5f
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        // Dibujar conexiones primero (para que los puntos queden encima)
        drawPoseConnections(
            landmarks = landmarks,
            color = lineColor,
            strokeWidth = lineWidth,
            confidenceThreshold = confidenceThreshold
        )

        // Dibujar puntos
        landmarks.forEach { landmark ->
            if (landmark.visibility > confidenceThreshold) {
                drawCircle(
                    color = pointColor,
                    radius = pointRadius,
                    center = Offset(landmark.x, landmark.y)
                )
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPoseConnections(
    landmarks: List<Landmark>,
    color: Color,
    strokeWidth: Float,
    confidenceThreshold: Float
) {
    val connections = getPoseConnections()

    connections.forEach { (startId, endId) ->
        val start = landmarks.find { it.id == startId }
        val end = landmarks.find { it.id == endId }

        if (start != null && end != null &&
            start.visibility > confidenceThreshold &&
            end.visibility > confidenceThreshold
        ) {
            drawLine(
                color = color,
                start = Offset(start.x, start.y),
                end = Offset(end.x, end.y),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

private fun getPoseConnections(): List<Pair<Int, Int>> {
    return listOf(
        // Cara
        Pair(LandmarkIds.NOSE, LandmarkIds.LEFT_EYE_INNER),
        Pair(LandmarkIds.NOSE, LandmarkIds.RIGHT_EYE_INNER),
        Pair(LandmarkIds.LEFT_EYE_INNER, LandmarkIds.LEFT_EYE),
        Pair(LandmarkIds.LEFT_EYE, LandmarkIds.LEFT_EYE_OUTER),
        Pair(LandmarkIds.LEFT_EYE_OUTER, LandmarkIds.LEFT_EAR),
        Pair(LandmarkIds.RIGHT_EYE_INNER, LandmarkIds.RIGHT_EYE),
        Pair(LandmarkIds.RIGHT_EYE, LandmarkIds.RIGHT_EYE_OUTER),
        Pair(LandmarkIds.RIGHT_EYE_OUTER, LandmarkIds.RIGHT_EAR),

        // Torso
        Pair(LandmarkIds.LEFT_SHOULDER, LandmarkIds.RIGHT_SHOULDER),
        Pair(LandmarkIds.LEFT_SHOULDER, LandmarkIds.LEFT_HIP),
        Pair(LandmarkIds.RIGHT_SHOULDER, LandmarkIds.RIGHT_HIP),
        Pair(LandmarkIds.LEFT_HIP, LandmarkIds.RIGHT_HIP),

        // Brazo izquierdo
        Pair(LandmarkIds.LEFT_SHOULDER, LandmarkIds.LEFT_ELBOW),
        Pair(LandmarkIds.LEFT_ELBOW, LandmarkIds.LEFT_WRIST),
        Pair(LandmarkIds.LEFT_WRIST, LandmarkIds.LEFT_PINKY),
        Pair(LandmarkIds.LEFT_WRIST, LandmarkIds.LEFT_INDEX),
        Pair(LandmarkIds.LEFT_PINKY, LandmarkIds.LEFT_INDEX),
        Pair(LandmarkIds.LEFT_WRIST, LandmarkIds.LEFT_THUMB),
        Pair(LandmarkIds.LEFT_THUMB, LandmarkIds.LEFT_INDEX),

        // Brazo derecho
        Pair(LandmarkIds.RIGHT_SHOULDER, LandmarkIds.RIGHT_ELBOW),
        Pair(LandmarkIds.RIGHT_ELBOW, LandmarkIds.RIGHT_WRIST),
        Pair(LandmarkIds.RIGHT_WRIST, LandmarkIds.RIGHT_PINKY),
        Pair(LandmarkIds.RIGHT_WRIST, LandmarkIds.RIGHT_INDEX),
        Pair(LandmarkIds.RIGHT_PINKY, LandmarkIds.RIGHT_INDEX),
        Pair(LandmarkIds.RIGHT_WRIST, LandmarkIds.RIGHT_THUMB),
        Pair(LandmarkIds.RIGHT_THUMB, LandmarkIds.RIGHT_INDEX),

        // Pierna izquierda
        Pair(LandmarkIds.LEFT_HIP, LandmarkIds.LEFT_KNEE),
        Pair(LandmarkIds.LEFT_KNEE, LandmarkIds.LEFT_ANKLE),
        Pair(LandmarkIds.LEFT_ANKLE, LandmarkIds.LEFT_HEEL),
        Pair(LandmarkIds.LEFT_ANKLE, LandmarkIds.LEFT_FOOT_INDEX),
        Pair(LandmarkIds.LEFT_HEEL, LandmarkIds.LEFT_FOOT_INDEX),

        // Pierna derecha
        Pair(LandmarkIds.RIGHT_HIP, LandmarkIds.RIGHT_KNEE),
        Pair(LandmarkIds.RIGHT_KNEE, LandmarkIds.RIGHT_ANKLE),
        Pair(LandmarkIds.RIGHT_ANKLE, LandmarkIds.RIGHT_HEEL),
        Pair(LandmarkIds.RIGHT_ANKLE, LandmarkIds.RIGHT_FOOT_INDEX),
        Pair(LandmarkIds.RIGHT_HEEL, LandmarkIds.RIGHT_FOOT_INDEX)
    )
}