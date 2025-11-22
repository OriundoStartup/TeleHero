package cl.teleton.mvp.domain.usecases

import cl.teleton.mvp.domain.models.Landmark
import cl.teleton.mvp.domain.models.LandmarkIds
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2

/**
 * Resultado del análisis de movimiento con métricas detalladas
 */
data class AnalysisResult(
    val qualityScore: Float,        // 0.0 a 1.0
    val currentAngle: Double,        // Ángulo de la articulación
    val feedbackMessage: String,     // Mensaje de feedback (nunca null)
    val isCompensationDetected: Boolean  // Detecta compensaciones
)

/**
 * Caso de uso para analizar la calidad del movimiento del paciente.
 * Analiza flexión de codo y detecta compensaciones posturales.
 * Ubicación: commonMain
 */
class AnalyzeMovementUseCase {

    /**
     * Ejecuta el análisis del movimiento basado en los landmarks detectados
     */
    fun execute(landmarks: List<Landmark>): AnalysisResult {
        // Validación inicial
        if (landmarks.isEmpty()) {
            return AnalysisResult(
                qualityScore = 0f,
                currentAngle = 0.0,
                feedbackMessage = "No se detecta ninguna persona. Acércate a la cámara.",
                isCompensationDetected = false
            )
        }

        // Obtener landmarks clave para análisis de codo izquierdo
        val leftShoulder = landmarks.find { it.id == LandmarkIds.LEFT_SHOULDER }
        val leftElbow = landmarks.find { it.id == LandmarkIds.LEFT_ELBOW }
        val leftWrist = landmarks.find { it.id == LandmarkIds.LEFT_WRIST }

        // Validar que tenemos los puntos necesarios
        if (leftShoulder == null || leftElbow == null || leftWrist == null) {
            return AnalysisResult(
                qualityScore = 0f,
                currentAngle = 0.0,
                feedbackMessage = "Posiciónate frente a la cámara mostrando tu brazo izquierdo completo",
                isCompensationDetected = false
            )
        }

        // Verificar visibilidad mínima de los landmarks
        val minVisibility = 0.5f
        if (leftShoulder.visibility < minVisibility ||
            leftElbow.visibility < minVisibility ||
            leftWrist.visibility < minVisibility) {
            return AnalysisResult(
                qualityScore = 0.3f,
                currentAngle = 0.0,
                feedbackMessage = "Mejora tu iluminación o acércate más a la cámara",
                isCompensationDetected = false
            )
        }

        // Calcular el ángulo del codo
        val elbowAngle = calculateJointAngle(leftShoulder, leftElbow, leftWrist)

        // Detectar compensación: comparar altura de hombros
        val rightShoulder = landmarks.find { it.id == LandmarkIds.RIGHT_SHOULDER }
        val isCompensating = detectShoulderCompensation(leftShoulder, rightShoulder)

        // Calcular calidad del movimiento
        val quality = calculateQualityScore(elbowAngle, isCompensating)

        // Generar feedback personalizado
        val feedback = generateFeedback(quality, elbowAngle, isCompensating)

        return AnalysisResult(
            qualityScore = quality,
            currentAngle = elbowAngle,
            feedbackMessage = feedback,
            isCompensationDetected = isCompensating
        )
    }

    /**
     * Calcula el ángulo entre tres puntos (articulación)
     * Retorna el ángulo en grados (0-180)
     */
    private fun calculateJointAngle(
        p1: Landmark,  // Punto proximal (hombro)
        p2: Landmark,  // Vértice (codo)
        p3: Landmark   // Punto distal (muñeca)
    ): Double {
        val radians = atan2((p3.y - p2.y).toDouble(), (p3.x - p2.x).toDouble()) -
                atan2((p1.y - p2.y).toDouble(), (p1.x - p2.x).toDouble())

        // Convertir radianes a grados
        val degrees = radians * 180.0 / PI

        // Normalizar el ángulo entre 0 y 180 grados
        val normalizedAngle = abs(degrees)
        return if (normalizedAngle > 180.0) 360.0 - normalizedAngle else normalizedAngle
    }

    /**
     * Detecta si hay compensación en los hombros
     * (un hombro más alto que el otro indica compensación)
     */
    private fun detectShoulderCompensation(
        leftShoulder: Landmark,
        rightShoulder: Landmark?
    ): Boolean {
        if (rightShoulder == null || rightShoulder.visibility < 0.5f) {
            return false
        }

        // Diferencia vertical entre hombros (umbral de 5%)
        val shoulderDifference = abs(leftShoulder.y - rightShoulder.y)
        return shoulderDifference > 0.05f
    }

    /**
     * Calcula el score de calidad basado en el ángulo y compensaciones
     */
    private fun calculateQualityScore(angle: Double, isCompensating: Boolean): Float {
        // Si hay compensación, penalizar el score
        if (isCompensating) {
            return 0.2f
        }

        // Rango ideal de movimiento: 30° a 150° (flexión de codo)
        return when (angle) {
            in 30.0..150.0 -> {
                // Score perfecto dentro del rango ideal
                1.0f
            }
            !in 30.0..150.0 -> {
                // Score reducido fuera del rango
                0.5f
            }
            else -> 0.3f
        }
    }

    /**
     * Genera un mensaje de feedback personalizado según el análisis
     */
    private fun generateFeedback(
        quality: Float,
        angle: Double,
        isCompensating: Boolean
    ): String {
        return when {
            isCompensating -> "⚠️ ¡Baja el hombro! Evita compensar con el cuerpo"

            quality >= 0.9f -> "🎉 ¡Excelente movimiento! Sigue así"

            quality >= 0.7f -> "👍 ¡Muy bien! Mantén esa postura"

            angle < 30.0 -> "📐 Intenta flexionar más el codo (ángulo: ${angle.toInt()}°)"

            angle > 150.0 -> "📐 El brazo está muy extendido (ángulo: ${angle.toInt()}°)"

            else -> "💪 Sigue trabajando, vas por buen camino"
        }
    }
}