package cl.teleton.mvp.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.teleton.mvp.domain.filtering.FilterType
import cl.teleton.mvp.domain.filtering.PoseSmoothing
import cl.teleton.mvp.domain.models.Landmark
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExerciseViewModel : ViewModel() {

    private val poseSmoothing = PoseSmoothing(
        filterType = FilterType.KALMAN,
        windowSize = 5
    )

    private val _rawLandmarks = MutableStateFlow<List<Landmark>>(emptyList())
    val rawLandmarks: StateFlow<List<Landmark>> = _rawLandmarks.asStateFlow()

    private val _smoothedLandmarks = MutableStateFlow<List<Landmark>>(emptyList())
    val smoothedLandmarks: StateFlow<List<Landmark>> = _smoothedLandmarks.asStateFlow()

    fun onPoseDetected(landmarks: List<Landmark>) {
        viewModelScope.launch {
            // Guardar raw
            _rawLandmarks.value = landmarks

            // Filtrar landmarks con baja confianza antes de suavizar
            val validLandmarks = landmarks.filter { it.visibility > 0.3f }

            // Aplicar filtrado de ruido
            val smoothed = if (validLandmarks.isNotEmpty()) {
                poseSmoothing.smoothLandmarks(validLandmarks)
            } else {
                emptyList()
            }

            _smoothedLandmarks.value = smoothed

            // Analizar ejercicio con datos suavizados
            if (smoothed.isNotEmpty()) {
                analyzeExercise(smoothed)
            }
        }
    }

    private fun analyzeExercise(landmarks: List<Landmark>) {
        // Tu lógica de análisis de ejercicios aquí
        // Por ejemplo, calcular ángulos, contar repeticiones, etc.
    }

    fun resetFilters() {
        poseSmoothing.reset()
    }
}