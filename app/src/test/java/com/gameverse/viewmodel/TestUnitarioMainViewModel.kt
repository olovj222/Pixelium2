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

    private val repository: AppRepository = mockk(relaxed = true)
    private lateinit var viewModel: MainViewModel
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

        // Mockeos
        every { repository.getProducts() } returns flowOf(testProducts)
        every { repository.getNews() } returns flowOf(testNews)
        every { repository.getHomeHighlights() } returns flowOf(testNews)
        coEvery { repository.getUserById(1) } returns testUser

        // IMPORTANTE: tu VM ahora llama syncProductsFromApi() en init
        coEvery { repository.syncProductsFromApi() } returns Unit

        // WHEN
        viewModel = MainViewModel(repository) { 1 }

        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        advanceUntilIdle()

        // THEN
        val currentState = viewModel.uiState.value

        currentState.isLoading shouldBe false
        currentState.products shouldBe testProducts
        currentState.news shouldBe testNews
        currentState.homeHighlights shouldBe testNews
        currentState.userProfile shouldBe testUser

        collectJob.cancel()
    }

    @Test
    fun `uiState should have null user profile if no user is logged in`() = runTest {
        // GIVEN
        every { repository.getProducts() } returns flowOf(emptyList())
        every { repository.getNews() } returns flowOf(emptyList())
        every { repository.getHomeHighlights() } returns flowOf(emptyList())

        coEvery { repository.syncProductsFromApi() } returns Unit

        // WHEN
        viewModel = MainViewModel(repository) { null }

        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        advanceUntilIdle()

        // THEN
        viewModel.uiState.value.userProfile shouldBe null
        viewModel.uiState.value.isLoading shouldBe true

        collectJob.cancel()
    }
}
