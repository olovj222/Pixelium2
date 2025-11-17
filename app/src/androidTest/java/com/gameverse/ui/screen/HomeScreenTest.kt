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
        // Forzar estado de carga
        fakeMainViewModel.setState(MainUiState(isLoading = true))

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
        composeTestRule.mainClock.advanceTimeBy(200)
        composeTestRule.waitForIdle()

        // Debug: Ver el árbol
        composeTestRule.onRoot().printToLog("HomeScreenLoading")

        // Validar que el contenido principal NO esté visible
        composeTestRule
            .onNodeWithText("Bienvenido a LVL-UP Gamer", useUnmergedTree = true)
            .assertDoesNotExist()
    }

    @Test
    fun testHomeScreen_LoadedState_ShowsContentAndTitles() {
        // Forzar estado cargado
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
        composeTestRule.mainClock.advanceTimeBy(200)
        composeTestRule.waitForIdle()

        // Debug: Ver qué hay en el árbol
        composeTestRule.onRoot().printToLog("HomeScreenLoaded")

        // Validar los títulos con useUnmergedTree
        composeTestRule
            .onNodeWithText("Bienvenido a LVL-UP Gamer", substring = true, useUnmergedTree = true)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("¿Que encontraré aquí?", substring = true, useUnmergedTree = true)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("¿Próximas mejoras?", substring = true, useUnmergedTree = true)
            .assertIsDisplayed()

        // Validar las imágenes por contentDescription
        composeTestRule
            .onNodeWithContentDescription("Logo de Gameverse", useUnmergedTree = true)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Animación Voltereta", useUnmergedTree = true)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Animación Nube", useUnmergedTree = true)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Animación Mario", useUnmergedTree = true)
            .assertIsDisplayed()
    }
}

// Que onda