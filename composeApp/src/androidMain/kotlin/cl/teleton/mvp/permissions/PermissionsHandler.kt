package cl.teleton.mvp.permissions


import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class PermissionsHandler(private val activity: ComponentActivity) {

    private var onPermissionResult: ((Boolean) -> Unit)? = null

    private val requestPermissionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        onPermissionResult?.invoke(isGranted)
    }

    fun requestCameraPermission(onResult: (Boolean) -> Unit) {
        onPermissionResult = onResult

        when {
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                onResult(true)
            }
            activity.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                // Usuario rechazó anteriormente, mostrar explicación
                onResult(false)
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
}