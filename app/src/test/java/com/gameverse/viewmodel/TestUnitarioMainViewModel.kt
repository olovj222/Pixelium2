package com.gameverse.viewmodel

import com.gameverse.data.model.NewsItem
import com.gameverse.data.model.Product
import com.gameverse.data.model.User
import com.gameverse.data.repository.AppRepository
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val repository: AppRepository = mockk()
    private lateinit var viewModel: MainViewModel
    // Usamos UnconfinedTestDispatcher para que los cambios de estado sean inmediatos cuando sea posible
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState should emit combined data when flows emit`() = runTest {
        // GIVEN
        val testUser = User(1, "test", "pass", "Test User", "email", "", "")
        val testProducts = listOf(Product(1, "Prod 1", "Desc", 100.0, ""))
        val testNews = listOf(NewsItem(1, "News 1", "Summary", ""))

        // Mocks
        every { repository.getProducts() } returns flowOf(testProducts)
        every { repository.getNews() } returns flowOf(testNews)
        every { repository.getHomeHighlights() } returns flowOf(testNews)
        coEvery { repository.getUserById(1) } returns testUser

        // WHEN
        viewModel = MainViewModel(repository) { 1 }

        // ¡TRUCO CLAVE!
        // Los StateFlows con 'WhileSubscribed' necesitan un colector activo para empezar a emitir.
        // Creamos un "background job" que colecte el estado.
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        // Avanzamos el tiempo para permitir que los flows emitan y el combine procese
        advanceUntilIdle()

        // THEN
        val currentState = viewModel.uiState.value

        // Verificamos propiedad por propiedad para ver cuál falla si hay error
        currentState.isLoading shouldBe false
        currentState.products shouldBe testProducts
        currentState.news shouldBe testNews
        currentState.userProfile shouldBe testUser

        // Limpieza
        collectJob.cancel()
    }

    @Test
    fun `uiState should have null user profile if no user is logged in`() = runTest {
        // GIVEN
        every { repository.getProducts() } returns flowOf(emptyList())
        every { repository.getNews() } returns flowOf(emptyList())
        every { repository.getHomeHighlights() } returns flowOf(emptyList())

        // WHEN
        viewModel = MainViewModel(repository) { null }

        // ¡TRUCO CLAVE! Arrancamos el flujo
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        advanceUntilIdle()

        // THEN
        viewModel.uiState.value.userProfile shouldBe null
        viewModel.uiState.value.isLoading shouldBe false

        collectJob.cancel()
    }
}