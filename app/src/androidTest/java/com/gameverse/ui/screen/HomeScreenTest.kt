package com.gameverse.ui.screen

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gameverse.ui.screens.home.HomeScreen
import com.gameverse.ui.state.MainUiState
import com.gameverse.ui.theme.GameverseTheme
import com.gameverse.util.FakeMainViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var fakeMainViewModel: FakeMainViewModel

    @Before
    fun setUp() {
        fakeMainViewModel = FakeMainViewModel()
    }

    @Test
    fun testHomeScreen_LoadingState_ShowsLoader() {
        fakeMainViewModel.setState(MainUiState(isLoading = true))

        composeTestRule.setContent {
            GameverseTheme {
                HomeScreen(
                    mainViewModel = fakeMainViewModel,
                    onNavigateToProducts = {}
                )
            }
        }

        composeTestRule.waitForIdle()

        // Validar que el contenido principal NO esté visible
        composeTestRule
            .onNodeWithText("Bienvenido a LVL-UP Gamer", useUnmergedTree = true)
            .assertDoesNotExist()
    }

    @Test
    fun testHomeScreen_LoadedState_ShowsContentAndTitles() {
        fakeMainViewModel.setState(MainUiState(isLoading = false))

        composeTestRule.setContent {
            GameverseTheme {
                HomeScreen(
                    mainViewModel = fakeMainViewModel,
                    onNavigateToProducts = {}
                )
            }
        }

        // Esperar a que la UI se estabilice
        composeTestRule.waitForIdle()
        composeTestRule.mainClock.advanceTimeBy(1000)
        composeTestRule.waitForIdle()

        // Debug: Imprimir el árbol COMPLETO para ver los textos exactos
        composeTestRule.onRoot().printToLog("HomeScreenDebug")

        // Validar título principal
        composeTestRule
            .onNodeWithText("Bienvenido a LVL-UP Gamer", substring = true, useUnmergedTree = true)
            .assertExists()
            .assertIsDisplayed()

        // Validar segundo título - Usa substring para buscar parte del texto
        composeTestRule
            .onNode(
                hasText("encontraré", substring = true, ignoreCase = true),
                useUnmergedTree = true
            )
            .assertExists()
            .performScrollTo()
            .assertIsDisplayed()

        // Validar tercer título
        composeTestRule
            .onNode(
                hasText("Proximas mejoras", substring = true, ignoreCase = true),
                useUnmergedTree = true
            )
            .assertExists()
            .performScrollTo()
            .assertIsDisplayed()

        // Validar el logo
        composeTestRule
            .onNodeWithContentDescription("Logo de Gameverse", useUnmergedTree = true)
            .assertExists()

        // Validar botón
        composeTestRule
            .onNodeWithText("Ver Productos", substring = true, useUnmergedTree = true)
            .assertExists()
            .performScrollTo()
    }

    @Test
    fun testHomeScreen_ButtonNavigatesToProducts() {
        fakeMainViewModel.setState(MainUiState(isLoading = false))

        var navigatedToProducts = false

        composeTestRule.setContent {
            GameverseTheme {
                HomeScreen(
                    mainViewModel = fakeMainViewModel,
                    onNavigateToProducts = { navigatedToProducts = true }
                )
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule.mainClock.advanceTimeBy(500)

        // Buscar y hacer click en el botón "Ver Productos"
        composeTestRule
            .onNodeWithText("Ver Productos", useUnmergedTree = true)
            .assertExists()
            .performScrollTo()
            .performClick()

        // Verificar que se llamó la navegación
        assert(navigatedToProducts) { "No se navegó a productos" }
    }
}