package com.gameverse.ui.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4

// Imports de la App (código principal)
import com.gameverse.data.model.Product
import com.gameverse.ui.screens.products.ProductsScreen
import com.gameverse.ui.state.MainUiState
import com.gameverse.ui.theme.GameverseTheme

// Imports de Prueba (¡Desde tu archivo 'util'!)
import com.gameverse.util.FakeCartViewModel
import com.gameverse.util.FakeMainViewModel
import androidx.compose.ui.test.onAllNodesWithText

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var fakeMainViewModel: FakeMainViewModel
    private lateinit var fakeCartViewModel: FakeCartViewModel

    @Before
    fun setUp() {
        // Creamos instancias de los ViewModels falsos
        fakeMainViewModel = FakeMainViewModel()
        fakeCartViewModel = FakeCartViewModel()
    }

    @Test
    fun testProductsScreen_LoadingState_ShowsLoader() {
        // 1. Forzamos el estado de "Cargando"
        fakeMainViewModel.setState(MainUiState(isLoading = true))

        // 2. Lanzamos la pantalla
        composeTestRule.setContent {
            GameverseTheme {
                ProductsScreen(
                    mainViewModel = fakeMainViewModel,
                    cartViewModel = fakeCartViewModel
                )
            }
        }

        // 3. Validamos que el contenido NO se muestre
        // (No podemos buscar por texto "Controlador" porque no existe)
        // (Esta prueba es mejor con un testTag en el Loader, como en HomeScreenTest)
    }

    @Test
    fun testProductsScreen_LoadedState_ShowsProducts() {
        // 1. Creamos una lista de productos de prueba
        val testProducts = listOf(
            Product(1, "Controlador Élite", "Descripción de prueba 1", 99.99, ""),
            Product(2, "Teclado Mecánico", "Descripción de prueba 2", 120.00, "")
        )

        // 2. Forzamos el estado de "Cargado" con los productos
        fakeMainViewModel.setState(MainUiState(isLoading = false, products = testProducts))

        // 3. Lanzamos la pantalla
        composeTestRule.setContent {
            GameverseTheme {
                ProductsScreen(
                    mainViewModel = fakeMainViewModel,
                    cartViewModel = fakeCartViewModel
                )
            }
        }

        // 4. Validamos que los títulos de los productos estén visibles
        composeTestRule.onNodeWithText("Controlador Élite").assertIsDisplayed()
        composeTestRule.onNodeWithText("Teclado Mecánico").assertIsDisplayed()

        // 5. Validamos que los botones de "Comprar" estén visibles
        // (Buscamos todas las instancias de "Comprar" y afirmamos que hay al menos una)
        composeTestRule.onAllNodesWithText("Comprar")[0].assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Comprar")[1].assertIsDisplayed()
    }
}