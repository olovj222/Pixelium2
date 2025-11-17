package com.gameverse.ui.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4

// --- Imports de la App (código principal) ---
import com.gameverse.ui.screens.home.HomeScreen
import com.gameverse.ui.state.MainUiState
import com.gameverse.ui.theme.GameverseTheme

// --- Imports de Prueba (¡Desde tu archivo 'util'!) ---
// (Asegúrate de que la ruta 'com.gameverse.util' sea correcta)
import com.gameverse.util.FakeMainViewModel

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// --- ¡YA NO HAY CLASES FALSAS DEFINIDAS AQUÍ! ---


@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Usamos el FakeMainViewModel importado
    private lateinit var fakeMainViewModel: FakeMainViewModel

    @Before
    fun setUp() {
        // Creamos la instancia del ViewModel falso
        fakeMainViewModel = FakeMainViewModel()
    }

    @Test
    fun testHomeScreen_LoadingState_ShowsLoader() {
        // 1. Forzamos el estado de "Cargando"
        fakeMainViewModel.setState(MainUiState(isLoading = true))

        // 2. Lanzamos la pantalla
        composeTestRule.setContent {
            GameverseTheme {
                HomeScreen(
                    mainViewModel = fakeMainViewModel,
                    onNavigateToProducts = {} // <-- 1. ¡ERROR CORREGIDO!
                )
            }
        }

        // 3. Validamos que el contenido principal NO esté
        composeTestRule.onNodeWithText("Bienvenido a LVL-UP Gamer").assertDoesNotExist()
    }

    @Test
    fun testHomeScreen_LoadedState_ShowsContentAndTitles() {
        // 1. Forzamos el estado de "Cargado"
        fakeMainViewModel.setState(MainUiState(isLoading = false))

        // 2. Lanzamos la pantalla
        composeTestRule.setContent {
            GameverseTheme {
                HomeScreen(
                    mainViewModel = fakeMainViewModel,
                    onNavigateToProducts = {} // <-- 2. ¡ERROR CORREGIDO!
                )
            }
        }

        // 3. Validamos los títulos y contenido
        composeTestRule.onNodeWithText("Bienvenido a LVL-UP Gamer").assertIsDisplayed()
        composeTestRule.onNodeWithText("¿Que encontrare aqui?").assertIsDisplayed()
        composeTestRule.onNodeWithText("¿Proximas mejoras?").assertIsDisplayed()

        // 4. Validamos las imágenes por su descripción de contenido
        composeTestRule.onNodeWithContentDescription("Logo de Gameverse").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Animación Voltereta").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Animación Nube").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Animación Mario").assertIsDisplayed()
    }
}