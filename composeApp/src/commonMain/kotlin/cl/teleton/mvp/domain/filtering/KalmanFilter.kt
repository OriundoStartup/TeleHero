package cl.teleton.mvp.domain.filtering

class KalmanFilter(
    private val processNoise: Float = 0.01f,
    private val measurementNoise: Float = 0.1f
) {
    private var estimate = 0f
    private var errorEstimate = 1f
    private var initialized = false

    fun update(measurement: Float): Float {
        // Inicializar con el primer valor
        if (!initialized) {
            estimate = measurement
            initialized = true
            return estimate
        }

        // Predicción
        val errorPrediction = errorEstimate + processNoise

        // Actualización (corrección)
        val kalmanGain = errorPrediction / (errorPrediction + measurementNoise)
        estimate += kalmanGain * (measurement - estimate)
        errorEstimate = (1 - kalmanGain) * errorPrediction

        return estimate
    }

    fun reset() {
        estimate = 0f
        errorEstimate = 1f
        initialized = false
    }
}