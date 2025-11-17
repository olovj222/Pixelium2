package com.gameverse.ui.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4

// Imports del código principal
import com.gameverse.data.model.CartProduct
import com.gameverse.ui.screens.cart.CartScreen
import com.gameverse.ui.state.CartUiState
import com.gameverse.ui.theme.GameverseTheme

// Imports de Prueba (desde 'util')
import com.gameverse.util.FakeCartViewModel

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CartScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var fakeCartViewModel: FakeCartViewModel

    @Before
    fun setUp() {
        // Creamos la instancia del ViewModel falso
        fakeCartViewModel = FakeCartViewModel()
    }

    @Test
    fun testCartScreen_WhenEmpty_ShowsEmptyMessage() {
        // 1. Forzamos el estado de "Carrito Vacío"
        fakeCartViewModel.setState(CartUiState(cartItems = emptyList()))

        // 2. Lanzamos la pantalla
        composeTestRule.setContent {
            GameverseTheme {
                CartScreen(
                    cartViewModel = fakeCartViewModel,
                    onGoBack = {} // Pasamos una lambda vacía
                )
            }
        }

        // 3. Validamos que se muestre el mensaje de "vacío"
        composeTestRule.onNodeWithText("Tu carrito está vacío").assertIsDisplayed()

        // 4. Validamos que el botón de pago NO se muestre
        composeTestRule.onNodeWithText("Simular Pago").assertDoesNotExist()
    }

    @Test
    fun testCartScreen_WithItems_ShowsItemsAndTotal() {
        // 1. Creamos un producto de prueba
        val testProduct = CartProduct(1, "Producto de Prueba", 123.45, "")

        // 2. Forzamos el estado con un item y un total
        fakeCartViewModel.setState(
            CartUiState(
                cartItems = listOf(testProduct),
                total = 123.45
            )
        )

        // 3. Lanzamos la pantalla
        composeTestRule.setContent {
            GameverseTheme {
                CartScreen(
                    cartViewModel = fakeCartViewModel,
                    onGoBack = {} // Pasamos una lambda vacía
                )
            }
        }

        // 4. Validamos que el item se muestre
        composeTestRule.onNodeWithText("Producto de Prueba").assertIsDisplayed()

        // 5. Validamos que el total y el botón de pago se muestren
        composeTestRule.onNodeWithText("Total:").assertIsDisplayed()
        composeTestRule.onNodeWithText("Simular Pago").assertIsDisplayed()

        // 6. Validamos que el mensaje de "vacío" NO se muestre
        composeTestRule.onNodeWithText("Tu carrito está vacío").assertDoesNotExist()
    }
}