package com.gameverse.ui.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4

// Imports del código principal
import com.gameverse.data.model.NewsItem // Importamos el modelo de Noticias
import com.gameverse.ui.screens.news.NewsScreen
import com.gameverse.ui.state.MainUiState
import com.gameverse.ui.theme.GameverseTheme

// Imports de Prueba (desde 'util')
import com.gameverse.util.FakeMainViewModel

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NewsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var fakeMainViewModel: FakeMainViewModel

    @Before
    fun setUp() {
        fakeMainViewModel = FakeMainViewModel()
    }

    @Test
    fun testNewsScreen_LoadingState_ShowsLoader() {
        // 1. Forzamos el estado de "Cargando"
        fakeMainViewModel.setState(MainUiState(isLoading = true))

        // 2. Lanzamos la pantalla
        composeTestRule.setContent {
            GameverseTheme {
                NewsScreen(mainViewModel = fakeMainViewModel)
            }
        }

        // 3. Validamos que el contenido NO se muestre
        //    (Esta prueba es más efectiva si el Loader tiene un testTag)
        //    composeTestRule.onNodeWithTag("FullScreenLoader").assertIsDisplayed()
        //    Por ahora, solo verificamos que un título de noticia no exista:
        composeTestRule.onNodeWithText("Título de Noticia de Prueba").assertDoesNotExist()
    }

    @Test
    fun testNewsScreen_LoadedState_ShowsNewsItems() {
        // 1. Creamos una lista de noticias de prueba
        val testNews = listOf(
            NewsItem(1, "Título de Noticia de Prueba", "Resumen de prueba 1", ""),
            NewsItem(2, "Otra Noticia Increíble", "Resumen de prueba 2", "")
        )

        // 2. Forzamos el estado de "Cargado" con las noticias
        fakeMainViewModel.setState(MainUiState(isLoading = false, news = testNews))

        // 3. Lanzamos la pantalla
        composeTestRule.setContent {
            GameverseTheme {
                NewsScreen(mainViewModel = fakeMainViewModel)
            }
        }

        // 4. Validamos que los títulos y resúmenes estén visibles
        composeTestRule.onNodeWithText("Título de Noticia de Prueba").assertIsDisplayed()
        composeTestRule.onNodeWithText("Resumen de prueba 1").assertIsDisplayed()

        composeTestRule.onNodeWithText("Otra Noticia Increíble").assertIsDisplayed()
        composeTestRule.onNodeWithText("Resumen de prueba 2").assertIsDisplayed()
    }
}