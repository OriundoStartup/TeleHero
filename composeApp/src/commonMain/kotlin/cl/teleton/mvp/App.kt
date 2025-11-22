// composeApp/src/commonMain/kotlin/App.kt

package cl.teleton.mvp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cl.teleton.mvp.domain.usecases.AnalyzeMovementUseCase
import cl.teleton.mvp.presentation.screens.home.HomeScreen
import cl.teleton.mvp.presentation.screens.magicmirror.MagicMirrorScreen
import cl.teleton.mvp.presentation.theme.TeleHeroTheme

// 1. Definimos las rutas de navegación
enum class Screen {
    HOME,
    MAGIC_MIRROR
}

@Composable
fun App() {
    // ⬇️ ENVOLVER TODA LA APP EN EL TEMA
    TeleHeroTheme {
        // Estado que maneja qué pantalla se muestra actualmente
        var currentScreen by remember { mutableStateOf(Screen.HOME) }

        // Obtenemos el useCase (simulando inyección de dependencia)
        val analyzeMovementUseCase = remember { AnalyzeMovementUseCase() }

        when (currentScreen) {
            Screen.HOME -> {
                HomeScreen(
                    onPlayClick = {
                        currentScreen = Screen.MAGIC_MIRROR
                    }
                )
            }
            Screen.MAGIC_MIRROR -> {
                MagicMirrorScreen(
                    onBackClick = { currentScreen = Screen.HOME },
                    analyzeMovementUseCase = analyzeMovementUseCase
                )
            }
        }
    } // ⬆️ FIN DEL TEMA
}