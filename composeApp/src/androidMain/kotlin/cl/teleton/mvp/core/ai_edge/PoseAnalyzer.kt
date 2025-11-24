package cl.teleton.mvp.core.ai_edge

import android.content.Context
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import cl.teleton.mvp.domain.models.Landmark
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions

class PoseAnalyzer(
    private val context: Context,
    private val onPoseDetected: (List<Landmark>) -> Unit
) : ImageAnalysis.Analyzer {

    private val options = AccuratePoseDetectorOptions.Builder()
        .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE)
        .build()

    private val poseDetector = PoseDetection.getClient(options)

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )

            poseDetector.process(image)
                .addOnSuccessListener { pose ->
                    val landmarks = pose.allPoseLandmarks.map { mlKitLandmark ->
                        Landmark(
                            id = mlKitLandmark.landmarkType,
                            x = mlKitLandmark.position.x,
                            y = mlKitLandmark.position.y,
                            visibility = mlKitLandmark.inFrameLikelihood
                        )
                    }
                    onPoseDetected(landmarks)
                }
                .addOnFailureListener { e ->
                    println("Error en detección de pose: ${e.message}")
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    fun close() {
        poseDetector.close()
    }
}