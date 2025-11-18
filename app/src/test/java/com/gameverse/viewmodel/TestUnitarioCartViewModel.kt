package com.gameverse.viewmodel

import com.gameverse.data.model.Product
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {

    // CartViewModel no tiene dependencias externas en su constructor actual,
    // por lo que no necesitamos mockear el repositorio.
    private lateinit var viewModel: CartViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CartViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addToCart should add item and update total`() = runTest {
        // GIVEN
        val product = Product(1, "Test Product", "Description", 100.0, "url")

        // WHEN
        viewModel.addToCart(product)
        testDispatcher.scheduler.advanceUntilIdle() // Asegura que la actualización del StateFlow ocurra

        // THEN
        val currentState = viewModel.uiState.value
        currentState.cartItems.size shouldBe 1
        currentState.cartItems[0].id shouldBe 1
        currentState.total shouldBe 100.0
    }

    @Test
    fun `addToCart multiple items should calculate correct total`() = runTest {
        // GIVEN
        val product1 = Product(1, "P1", "D1", 100.0, "url")
        val product2 = Product(2, "P2", "D2", 50.0, "url")

        // WHEN
        viewModel.addToCart(product1)
        viewModel.addToCart(product2)
        testDispatcher.scheduler.advanceUntilIdle()

        // THEN
        val currentState = viewModel.uiState.value
        currentState.cartItems.size shouldBe 2
        currentState.total shouldBe 150.0
    }

    @Test
    fun `removeFromCart should remove item and update total`() = runTest {
        // GIVEN
        val product1 = Product(1, "P1", "D1", 100.0, "url")
        val product2 = Product(2, "P2", "D2", 50.0, "url")
        viewModel.addToCart(product1)
        viewModel.addToCart(product2)
        testDispatcher.scheduler.advanceUntilIdle()

        // WHEN
        viewModel.removeFromCart(1) // Removemos el producto con ID 1
        testDispatcher.scheduler.advanceUntilIdle()

        // THEN
        val currentState = viewModel.uiState.value
        currentState.cartItems.size shouldBe 1
        currentState.cartItems[0].id shouldBe 2 // Solo queda el producto 2
        currentState.total shouldBe 50.0
    }

    @Test
    fun `checkout should clear cart and set paymentSuccess to true`() = runTest {
        // GIVEN
        val product = Product(1, "P1", "D1", 100.0, "url")
        viewModel.addToCart(product)
        testDispatcher.scheduler.advanceUntilIdle()

        // WHEN
        viewModel.checkout()
        testDispatcher.scheduler.advanceUntilIdle()

        // THEN
        val currentState = viewModel.uiState.value
        currentState.cartItems.shouldBeEmpty()
        currentState.total shouldBe 0.0
        currentState.paymentSuccess shouldBe true
    }

    @Test
    fun `resetPaymentStatus should set paymentSuccess to false`() = runTest {
        // GIVEN
        viewModel.checkout() // Primero hacemos checkout para ponerlo en true
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.uiState.value.paymentSuccess shouldBe true // Verificación previa

        // WHEN
        viewModel.resetPaymentStatus()
        testDispatcher.scheduler.advanceUntilIdle()

        // THEN
        viewModel.uiState.value.paymentSuccess shouldBe false
    }
}