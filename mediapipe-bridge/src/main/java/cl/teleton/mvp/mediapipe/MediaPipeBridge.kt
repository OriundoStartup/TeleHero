package cl.teleton.mvp.mediapipe

import android.content.Context
import android.graphics.Bitmap
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

class MediaPipeBridge(
    private val context: Context,
    private val onResult: (List<SimpleLandmark>) -> Unit
) {
    private var poseLandmarker: PoseLandmarker? = null

    fun initialize(modelAssetPath: String = "pose_landmarker_lite.task") {
        val baseOptions = BaseOptions.builder()
            .setModelAssetPath(modelAssetPath)
            .build()

        val options = PoseLandmarker.PoseLandmarkerOptions.builder()
            .setBaseOptions(baseOptions)
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setResultListener { result, _ ->
                handleResult(result)
            }
            .setErrorListener { error ->
                println("MediaPipe Error: ${error.message}")
            }
            .build()

        poseLandmarker = PoseLandmarker.createFromOptions(context, options)
    }

    fun detectAsync(bitmap: Bitmap, timestampMs: Long) {
        val mpImage = BitmapImageBuilder(bitmap).build()
        poseLandmarker?.detectAsync(mpImage, timestampMs)
    }

    private fun handleResult(result: PoseLandmarkerResult) {
        val landmarks = result.landmarks().firstOrNull() ?: return

        val simpleLandmarks = landmarks.mapIndexed { index, landmark ->
            SimpleLandmark(
                id = index,
                x = landmark.x(),
                y = landmark.y(),
                z = landmark.z(),
                visibility = 1.0f  // Valor por defecto
            )
        }

        onResult(simpleLandmarks)
    }

    fun close() {
        poseLandmarker?.close()
        poseLandmarker = null
    }
}

data class SimpleLandmark(
    val id: Int,
    val x: Float,
    val y: Float,
    val z: Float,
    val visibility: Float
)