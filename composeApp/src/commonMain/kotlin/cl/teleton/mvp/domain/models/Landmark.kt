package cl.teleton.mvp.domain.models
/**
 * Representa un punto de referencia clave (landmark) en el cuerpo.
 */
data class Landmark(
    val id: Int,
    val x: Float, // Coordenada X (0.0 a 1.0)
    val y: Float, // Coordenada Y (0.0 a 1.0)
    val visibility: Float = 0.0f // Confianza
)
/**
 * Constantes de los índices de Landmards definidos por MediaPipe.
 * Son cruciales para identificar las articulaciones a analizar.
 *
 */
object LandmarkIds {
    const val LEFT_SHOULDER = 11
    const val RIGHT_SHOULDER = 12
    const val LEFT_ELBOW = 13
    const val RIGHT_ELBOW = 14
    const val LEFT_WRIST = 15
    const val RIGHT_WRIST = 16
    const val LEFT_HIP = 23
    const val RIGHT_HIP = 24
    // Agrega más si los necesitas para ejercicios específicos
}
