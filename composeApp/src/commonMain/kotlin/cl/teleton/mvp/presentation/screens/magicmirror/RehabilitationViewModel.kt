package cl.teleton.mvp.presentation.screens.magicmirror

import cl.teleton.mvp.domain.models.Landmark
import cl.teleton.mvp.domain.usecases.AnalyzeMovementUseCase
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Estado de la UI para la pantalla del Espejo Mágico.
 */
data class RehabUiState(
    // Puntos del esqueleto detectados por MediaPipe
    val poseLandmarks: List<Landmark> = emptyList(),

    // Puntuación de calidad del movimiento (0.0 a 1.0)
    val movementQuality: Float = 0f,

    // Ángulo actual de la articulación
    val currentAngle: Double = 0.0,

    // Mensaje de feedback o motivación
    val botMessage: String? = "Esperando detección...",

    // Indica si se detectó compensación postural
    val isCompensating: Boolean = false,

    // Contador de repeticiones válidas
    val repetitionCount: Int = 0
)

/**
 * ViewModel que maneja la lógica de negocio y el estado de la sesión de rehabilitación.
 * Es el puente entre la cámara/IA y la UI.
 */
class RehabilitationViewModel(
    private val useCase: AnalyzeMovementUseCase
) : ViewModel() {

    // Estado mutable privado
    private val _state = MutableStateFlow(RehabUiState())

    // Estado expuesto como solo lectura
    val state = _state.asStateFlow()

    // Variables para contar repeticiones
    private var lastAngle: Double = 0.0
    private var isFlexing: Boolean = false
    private val FLEX_THRESHOLD = 90.0  // Ángulo mínimo para contar flexión

    /**
     * Procesa los puntos del esqueleto recibidos de la cámara.
     * Se llama en cada frame que MediaPipe procesa.
     */
    fun processMovement(landmarks: List<Landmark>) {
        // Ejecutar el análisis de movimiento
        val result = useCase.execute(landmarks)

        // Detectar repeticiones completas
        val newRepCount = detectRepetition(result.currentAngle)

        // Actualizar el estado de la UI
        _state.value = _state.value.copy(
            poseLandmarks = landmarks,
            movementQuality = result.qualityScore,
            currentAngle = result.currentAngle,
            botMessage = result.feedbackMessage,
            isCompensating = result.isCompensationDetected,
            repetitionCount = newRepCount
        )
    }

    /**
     * Detecta cuando se completa una repetición válida del ejercicio
     */
    private fun detectRepetition(currentAngle: Double): Int {
        var count = _state.value.repetitionCount

        // Si el ángulo baja de 90° (flexión)
        if (currentAngle < FLEX_THRESHOLD && !isFlexing) {
            isFlexing = true
        }

        // Si el ángulo sube de 140° después de una flexión (extensión)
        if (currentAngle > 140.0 && isFlexing) {
            isFlexing = false
            count++ // Repetición completa
        }

        lastAngle = currentAngle
        return count
    }

    /**
     * Reinicia el contador de repeticiones
     */
    fun resetRepetitions() {
        _state.value = _state.value.copy(repetitionCount = 0)
        isFlexing = false
    }

    /**
     * Pausa la sesión
     */
    fun pauseSession() {
        // Implementar lógica de pausa si es necesario
    }
}