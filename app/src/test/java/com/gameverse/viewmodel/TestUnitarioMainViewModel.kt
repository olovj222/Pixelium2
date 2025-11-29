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
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val repository: AppRepository = mockk(relaxed = true)
    private lateinit var viewModel: MainViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        // Establecer el TestDispatcher como el Dispatcher principal
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        // Restaurar el Dispatcher principal
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState should emit combined data when flows emit`() = runTest {
        // GIVEN
        val testUserId = 1
        val testUser = User(testUserId, "test", "pass", "Test User", "email", "", "")
        val testProducts = listOf(Product(1, "Prod 1", "Desc", 100.0, ""))
        val testNews = listOf(NewsItem(1, "News 1", "Summary", ""))

        // Mockeos para el repositorio:
        every { repository.getProducts() } returns flowOf(testProducts)
        every { repository.getNews() } returns flowOf(testNews)
        every { repository.getHomeHighlights() } returns flowOf(testNews)

        // CORRECCIÓN CLAVE 1: Mockear getUserFlow(userId) para que devuelva un Flow.
        // El VM usa getUserFlow, no getUserById.
        every { repository.getUserFlow(testUserId) } returns flowOf(testUser)

        // Mock para la corrutina que se ejecuta en init
        coEvery { repository.syncProductsFromApi() } returns Unit

        // WHEN
        // Inicializar el ViewModel con un usuario logeado (ID = 1)
        viewModel = MainViewModel(repository) { testUserId }

        // CORRECCIÓN CLAVE 2: Iniciar la colección sin UnconfinedTestDispatcher.
        // runTest y StandardTestDispatcher ya controlan el tiempo.
        val collectJob = launch {
            // El .collect {} fuerza la activación del stateIn(SharingStarted.Lazily)
            viewModel.uiState.collect {}
        }

        // Avanzar el tiempo para que el VM inicie los flows, combine los valores
        // y el stateIn emita el resultado final (donde isLoading pasa a false).
        advanceUntilIdle()

        // THEN
        val currentState = viewModel.uiState.value

        // Con datos mockeados no vacíos, isLoading debe ser false.
        currentState.isLoading shouldBe false // ¡Debe ser false ahora!
        currentState.products shouldBe testProducts
        currentState.news shouldBe testNews
        currentState.homeHighlights shouldBe testNews
        currentState.userProfile shouldBe testUser

        collectJob.cancel()
    }

    @Test
    fun `uiState should have null user profile if no user is logged in`() = runTest {
        // GIVEN
        // Para este test, la lógica del VM establece isLoading = true si products E news están vacíos.
        every { repository.getProducts() } returns flowOf(emptyList())
        every { repository.getNews() } returns flowOf(emptyList())
        every { repository.getHomeHighlights() } returns flowOf(emptyList())

        // Mockear getUserFlow para el caso de ID nulo: el VM ya devuelve flowOf(null) internamente
        // pero incluimos este mock de ser necesario (aunque en este caso no lo es, es buena práctica).
        every { repository.getUserFlow(any()) } returns flowOf(null)

        coEvery { repository.syncProductsFromApi() } returns Unit

        // WHEN
        // Inicializar el ViewModel sin usuario logeado (ID = null)
        viewModel = MainViewModel(repository) { null }

        // Iniciar colección
        val collectJob = launch {
            viewModel.uiState.collect {}
        }

        advanceUntilIdle()

        // THEN
        // Ya que Products y News están vacías, isLoading debe ser true
        viewModel.uiState.value.userProfile shouldBe null
        viewModel.uiState.value.isLoading shouldBe true

        collectJob.cancel()
    }
}