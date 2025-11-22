package cl.teleton.mvp.core.ai_edge

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import cl.teleton.mvp.domain.models.Landmark
import cl.teleton.mvp.mediapipe.MediaPipeBridge
import java.io.ByteArrayOutputStream

class PoseAnalyzer(
    context: Context,
    private val onPoseDetected: (List<Landmark>) -> Unit
) : ImageAnalysis.Analyzer {

    private val bridge = MediaPipeBridge(context) { simpleLandmarks ->
        val landmarks = simpleLandmarks.map { simple ->
            Landmark(
                id = simple.id,
                x = simple.x,
                y = simple.y,
                visibility = simple.visibility
            )
        }
        onPoseDetected(landmarks)
    }

    init {
        bridge.initialize("pose_landmarker_lite.task")
    }

    override fun analyze(imageProxy: ImageProxy) {
        val frameTime = System.currentTimeMillis()

        try {
            val bitmap = imageProxyToBitmap(imageProxy)
            bridge.detectAsync(bitmap, frameTime)
        } catch (e: Exception) {
            println("Error en PoseAnalyzer: ${e.message}")
        } finally {
            imageProxy.close()
        }
    }

    fun close() {
        bridge.close()
    }

    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap {
        val yBuffer = imageProxy.planes[0].buffer
        val vuBuffer = imageProxy.planes[2].buffer

        val ySize = yBuffer.remaining()
        val vuSize = vuBuffer.remaining()

        val nv21 = ByteArray(ySize + vuSize)

        yBuffer.get(nv21, 0, ySize)
        vuBuffer.get(nv21, ySize, vuSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, imageProxy.width, imageProxy.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 100, out)
        val imageBytes = out.toByteArray()

        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
}