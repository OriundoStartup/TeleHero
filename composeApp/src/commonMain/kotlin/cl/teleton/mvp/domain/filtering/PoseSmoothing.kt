package cl.teleton.mvp.domain.filtering

import cl.teleton.mvp.domain.models.Landmark

class PoseSmoothing(
    private val filterType: FilterType = FilterType.KALMAN,
    private val windowSize: Int = 5
) {
    private val kalmanFiltersX = mutableMapOf<Int, KalmanFilter>()
    private val kalmanFiltersY = mutableMapOf<Int, KalmanFilter>()
    private val movingAverageBuffersX = mutableMapOf<Int, MutableList<Float>>()
    private val movingAverageBuffersY = mutableMapOf<Int, MutableList<Float>>()

    fun smoothLandmarks(landmarks: List<Landmark>): List<Landmark> {
        return when (filterType) {
            FilterType.KALMAN -> applyKalmanFilter(landmarks)
            FilterType.MOVING_AVERAGE -> applyMovingAverage(landmarks)
            FilterType.NONE -> landmarks
        }
    }

    private fun applyKalmanFilter(landmarks: List<Landmark>): List<Landmark> {
        return landmarks.map { landmark ->
            // Inicializar filtros si no existen
            if (!kalmanFiltersX.containsKey(landmark.id)) {
                kalmanFiltersX[landmark.id] = KalmanFilter()
                kalmanFiltersY[landmark.id] = KalmanFilter()
            }

            landmark.copy(
                x = kalmanFiltersX[landmark.id]!!.update(landmark.x),
                y = kalmanFiltersY[landmark.id]!!.update(landmark.y)
            )
        }
    }

    private fun applyMovingAverage(landmarks: List<Landmark>): List<Landmark> {
        return landmarks.map { landmark ->
            // Inicializar buffers si no existen
            if (!movingAverageBuffersX.containsKey(landmark.id)) {
                movingAverageBuffersX[landmark.id] = mutableListOf()
                movingAverageBuffersY[landmark.id] = mutableListOf()
            }

            landmark.copy(
                x = addToBufferAndAverage(movingAverageBuffersX[landmark.id]!!, landmark.x),
                y = addToBufferAndAverage(movingAverageBuffersY[landmark.id]!!, landmark.y)
            )
        }
    }

    private fun addToBufferAndAverage(buffer: MutableList<Float>, value: Float): Float {
        buffer.add(value)

        if (buffer.size > windowSize) {
            buffer.removeAt(0)
        }

        return buffer.average().toFloat()
    }

    fun reset() {
        kalmanFiltersX.values.forEach { it.reset() }
        kalmanFiltersY.values.forEach { it.reset() }
        movingAverageBuffersX.clear()
        movingAverageBuffersY.clear()
    }
}

enum class FilterType {
    NONE,
    KALMAN,
    MOVING_AVERAGE
}