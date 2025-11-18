package com.gameverse.viewmodel

import com.gameverse.data.model.User
import com.gameverse.data.repository.AppRepository
import com.gameverse.ui.state.LoginUiState
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
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
class LoginViewModelTest {

    // 1. Mock del Repositorio
    // 'relaxed = true' permite que responda valores por defecto si no definimos nada
    private val repository: AppRepository = mockk(relaxed = true)

    private lateinit var viewModel: LoginViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        // Configurar Coroutines para tests
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login success should update state to success`() = runTest {
        // GIVEN (Dado un escenario)
        val user = "testUser"
        val pass = "password123"
        val mockUser = User(1, user, pass, "Test User", "test@test.com", "2023", "")

        // Enseñamos al mock qué responder:
        // "Cuando llamen a login con cualquier string, devuelve el mockUser"
        coEvery { repository.login(any(), any()) } returns mockUser

        // WHEN (Cuando ocurre la acción)
        viewModel.login(user, pass)

        // Avanzamos el tiempo para que las corutinas se ejecuten
        testDispatcher.scheduler.advanceUntilIdle()

        // THEN (Entonces verificamos el resultado)
        // Usamos Kotest para una aserción legible
        viewModel.uiState.value.loginSuccess shouldBe true
        viewModel.uiState.value.loggedInUserId shouldBe 1
        viewModel.uiState.value.isLoading shouldBe false

        // Verificamos que el repositorio fue llamado
        coVerify { repository.login(user, pass) }
    }

    @Test
    fun `login failure should update state with error`() = runTest {
        // GIVEN
        // El repositorio devuelve null (login fallido)
        coEvery { repository.login(any(), any()) } returns null

        // WHEN
        viewModel.login("wrongUser", "wrongPass")
        testDispatcher.scheduler.advanceUntilIdle()

        // THEN
        viewModel.uiState.value.loginSuccess shouldBe false
        viewModel.uiState.value.error shouldBe "Usuario o contraseña incorrectos"
    }
}