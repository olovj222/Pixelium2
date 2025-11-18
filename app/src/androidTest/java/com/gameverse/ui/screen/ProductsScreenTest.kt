package com.gameverse.ui.screen

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4

// Imports de la App (código principal)
import com.gameverse.data.model.Product
import com.gameverse.ui.screens.products.ProductsScreen
import com.gameverse.ui.state.MainUiState
import com.gameverse.ui.theme.GameverseTheme

// Imports de Prueba (¡Desde tu archivo 'util'!)
import com.gameverse.util.FakeCartViewModel
import com.gameverse.util.FakeMainViewModel

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
        fakeMainViewModel = FakeMainViewModel()
        fakeCartViewModel = FakeCartViewModel()
    }

    @Test
    fun testProductsScreen_LoadingState_ShowsLoader() {
        fakeMainViewModel.setState(MainUiState(isLoading = true))

        composeTestRule.setContent {
            GameverseTheme {
                ProductsScreen(
                    mainViewModel = fakeMainViewModel,
                    cartViewModel = fakeCartViewModel
                )
            }
        }

        // Validamos que no haya productos visibles
        composeTestRule.onNodeWithText("Controlador Élite").assertDoesNotExist()
    }

    @Test
    fun testProductsScreen_LoadedState_ShowsProducts() {
        // 1. Datos de prueba
        val testProducts = listOf(
            Product(1, "Controlador Élite", "Descripción 1", 99.99, ""),
            Product(2, "Teclado Mecánico", "Descripción 2", 120.00, "")
        )

        // 2. Estado cargado
        fakeMainViewModel.setState(MainUiState(isLoading = false, products = testProducts))

        composeTestRule.setContent {
            GameverseTheme {
                ProductsScreen(
                    mainViewModel = fakeMainViewModel,
                    cartViewModel = fakeCartViewModel
                )
            }
        }

        // Esperar a que la UI se estabilice
        composeTestRule.waitForIdle()

        // 3. Validaciones

        // Producto 1
        composeTestRule
            .onNodeWithText("Controlador Élite")
            .performScrollTo() // ¡Importante! Hace scroll si es necesario
            .assertIsDisplayed()

        // Producto 2
        composeTestRule
            .onNodeWithText("Teclado Mecánico")
            .performScrollTo()
            .assertIsDisplayed()

        // 4. Validar botones "Agregar al carrito" (Texto corregido)
        // Usamos onAllNodesWithText para encontrar todos los botones con ese texto
        // y verificamos que al menos el primero sea visible.
        composeTestRule
            .onAllNodesWithText("Agregar al carrito")[0]
            .assertIsDisplayed()
    }
}