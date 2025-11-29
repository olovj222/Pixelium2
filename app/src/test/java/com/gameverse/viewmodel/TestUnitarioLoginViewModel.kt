package com.gameverse.viewmodel

import com.gameverse.data.model.User
import com.gameverse.data.repository.AppRepository
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

    private val repository: AppRepository = mockk(relaxed = true)

    private lateinit var viewModel: LoginViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val mockUser = User(1, "testUser", "password123", "Test User", "test@test.com", "2023", "")

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- TEST DE ÉXITO ---

    @Test
    fun `login success should update state to success`() = runTest {
        // GIVEN
        coEvery { repository.getUserByUsername(mockUser.username) } returns mockUser
        // La lógica del login ya no llama a repository.login(), sino a getUserByUsername()

        // WHEN
        viewModel.login(mockUser.username, mockUser.password)

        // Avanzamos el tiempo para que el delay(2000L) y la lógica se ejecuten
        testDispatcher.scheduler.advanceUntilIdle()

        // THEN
        viewModel.uiState.value.loginSuccess shouldBe true
        viewModel.uiState.value.loggedInUserId shouldBe 1
        viewModel.uiState.value.isLoading shouldBe false
        viewModel.uiState.value.usernameError shouldBe null // Sin errores de campo
        viewModel.uiState.value.passwordError shouldBe null // Sin errores de campo

        // Verificamos que el repositorio fue llamado correctamente
        coVerify { repository.getUserByUsername(mockUser.username) }
    }

    // --- TESTS DE FALLO (Credenciales) ---

    @Test
    fun `login failure when user does not exist should set usernameError`() = runTest {
        // GIVEN
        // Simulamos que getUserByUsername devuelve null (Usuario no encontrado)
        coEvery { repository.getUserByUsername(any()) } returns null

        // WHEN
        viewModel.login("nonExistentUser", "anyPass")
        testDispatcher.scheduler.advanceUntilIdle()

        // THEN: Debería fallar por usuario no registrado
        viewModel.uiState.value.loginSuccess shouldBe false
        viewModel.uiState.value.isLoading shouldBe false
        viewModel.uiState.value.usernameError shouldBe "Usuario no registrado" // ¡Nueva aserción!
        viewModel.uiState.value.passwordError shouldBe null
        viewModel.uiState.value.error shouldBe null
    }

    @Test
    fun `login failure when wrong password should set passwordError`() = runTest {
        // GIVEN
        // Simulamos que el usuario existe, pero tiene una contraseña diferente
        coEvery { repository.getUserByUsername(mockUser.username) } returns mockUser

        // WHEN
        viewModel.login(mockUser.username, "wrongPassword")
        testDispatcher.scheduler.advanceUntilIdle()

        // THEN: Debería fallar por contraseña incorrecta
        viewModel.uiState.value.loginSuccess shouldBe false
        viewModel.uiState.value.isLoading shouldBe false
        viewModel.uiState.value.usernameError shouldBe null
        viewModel.uiState.value.passwordError shouldBe "Contraseña incorrecta" // ¡Nueva aserción!
        viewModel.uiState.value.error shouldBe null
    }

    // --- TEST DE FALLO (Campos Vacíos) ---

    @Test
    fun `login failure when fields are empty should set both username and password errors`() = runTest {
        // GIVEN: No necesitamos mockear el repositorio, el error ocurre antes.

        // WHEN
        viewModel.login("", "") // Campos vacíos
        testDispatcher.scheduler.advanceUntilIdle()

        // THEN: Debería fallar por campos vacíos
        viewModel.uiState.value.loginSuccess shouldBe false
        viewModel.uiState.value.isLoading shouldBe false
        viewModel.uiState.value.usernameError shouldBe "Debes ingresar tu usuario"
        viewModel.uiState.value.passwordError shouldBe "Debes ingresar tu contraseña"

        // Verificamos que el repositorio NO fue llamado, ya que falló en la validación local.
        coVerify(exactly = 0) { repository.getUserByUsername(any()) }
    }
}