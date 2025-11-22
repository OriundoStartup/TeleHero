package cl.teleton.mvp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.teleton.mvp.permissions.PermissionsHandler
import kotlin.system.exitProcess

class MainActivity : ComponentActivity() {

    private lateinit var permissionsHandler: PermissionsHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        permissionsHandler = PermissionsHandler(this)

        setContent {
            var hasPermission by remember {
                mutableStateOf(permissionsHandler.hasCameraPermission())
            }

            LaunchedEffect(Unit) {
                if (!hasPermission) {
                    permissionsHandler.requestCameraPermission { granted ->
                        hasPermission = granted
                    }
                }
            }

            MaterialTheme {
                if (hasPermission) {
                    App()
                } else {
                    PermissionDeniedScreen(
                        onRequestAgain = {
                            permissionsHandler.requestCameraPermission { granted ->
                                hasPermission = granted
                            }
                        },
                        onExit = {
                            finish()
                            exitProcess(0)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PermissionDeniedScreen(
    onRequestAgain: () -> Unit,
    onExit: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "📷",
                style = MaterialTheme.typography.displayLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Permiso de Cámara Requerido",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Esta aplicación necesita acceso a la cámara para detectar tu postura y ayudarte con los ejercicios.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onRequestAgain,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Permitir Acceso a la Cámara")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onExit) {
                Text("Salir")
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}