package com.gameverse.ui.screen

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gameverse.ui.screens.login.LoginScreen
import com.gameverse.ui.state.LoginUiState
import com.gameverse.ui.theme.GameverseTheme
import com.gameverse.util.FakeLoginViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var fakeLoginViewModel: FakeLoginViewModel

    @Before
    fun setUp() {
        fakeLoginViewModel = FakeLoginViewModel()
    }

    @Test
    fun testLoginScreen_ValidateContentAndTitles() {
        composeTestRule.setContent {
            GameverseTheme {
                LoginScreen(
                    loginViewModel = fakeLoginViewModel,
                    onLoginSuccess = { },
                    onNavigateToRegister = { }
                )
            }
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithContentDescription("Logo de Gameverse", useUnmergedTree = true)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Usuario", substring = true, useUnmergedTree = true)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Contraseña", substring = true, useUnmergedTree = true)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Iniciar Sesión", substring = true, useUnmergedTree = true)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("¿No tienes cuenta? Regístrate aquí", substring = true, useUnmergedTree = true)
            .assertIsDisplayed()
    }

    @Test
    fun testLoginScreen_ShowError_WhenErrorStateIsSet() {
        fakeLoginViewModel.setState(LoginUiState(error = "Este es un error de prueba"))

        composeTestRule.setContent {
            GameverseTheme {
                LoginScreen(
                    loginViewModel = fakeLoginViewModel,
                    onLoginSuccess = { },
                    onNavigateToRegister = { }
                )
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule.mainClock.advanceTimeBy(100)

        composeTestRule
            .onNodeWithText("Este es un error de prueba", substring = true, useUnmergedTree = true)
            .assertIsDisplayed()
    }

    @Test
    fun testLoginScreen_ButtonIsDisabled_WhenLoading() {
        fakeLoginViewModel.setState(LoginUiState(isLoading = true))

        composeTestRule.setContent {
            GameverseTheme {
                LoginScreen(
                    loginViewModel = fakeLoginViewModel,
                    onLoginSuccess = { },
                    onNavigateToRegister = { }
                )
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule.mainClock.advanceTimeBy(200)
        composeTestRule.waitForIdle()

        // Usar testTag para encontrar el botón específico
        composeTestRule
            .onNodeWithTag("button_Iniciar Sesión")
            .assertIsNotEnabled()
    }
}