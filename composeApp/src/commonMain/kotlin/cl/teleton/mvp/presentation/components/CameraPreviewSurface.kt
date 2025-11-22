package cl.teleton.mvp.presentation.components
import androidx.compose.runtime.Composable
import cl.teleton.mvp.domain.models.Landmark

@Composable
expect fun CameraPreviewSurface(onPoseDetected: (List<Landmark>) -> Unit)
