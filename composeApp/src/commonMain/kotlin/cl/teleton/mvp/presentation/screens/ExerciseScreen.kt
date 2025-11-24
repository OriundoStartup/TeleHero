package cl.teleton.mvp.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cl.teleton.mvp.presentation.components.CameraPreviewSurface
import cl.teleton.mvp.presentation.components.PoseOverlay
import cl.teleton.mvp.presentation.viewmodels.ExerciseViewModel

@Composable
fun ExerciseScreen(
    viewModel: ExerciseViewModel
) {
    val smoothedLandmarks by viewModel.smoothedLandmarks.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Cámara
        CameraPreviewSurface(
            onPoseDetected = { landmarks ->
                viewModel.onPoseDetected(landmarks)
            }
        )

        // Overlay del esqueleto (encima de la cámara)
        if (smoothedLandmarks.isNotEmpty()) {
            PoseOverlay(
                landmarks = smoothedLandmarks,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}