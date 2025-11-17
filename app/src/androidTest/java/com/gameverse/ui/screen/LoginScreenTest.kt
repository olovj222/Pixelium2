package com.gameverse.ui.screen

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gameverse.ui.screens.login.LoginScreen
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

        // Esperamos a que la UI se renderice completamente
        composeTestRule.waitForIdle()

        // Debug: Imprime el árbol de nodos para ver qué hay
        composeTestRule.onRoot().printToLog("LoginScreenTest")

        // Validamos el logo - intenta con diferentes formas
        composeTestRule
            .onNodeWithContentDescription("Logo de Gameverse", useUnmergedTree = true)
            .assertExists("El logo no existe en el árbol de composición")
            .assertIsDisplayed()

        // Validamos los campos de texto
        composeTestRule
            .onNodeWithText("Usuario", useUnmergedTree = true)
            .assertExists("El campo Usuario no existe")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Contraseña", useUnmergedTree = true)
            .assertExists("El campo Contraseña no existe")
            .assertIsDisplayed()

        // Validamos el botón
        composeTestRule
            .onNodeWithText("Iniciar Sesión", useUnmergedTree = true)
            .assertExists("El botón Iniciar Sesión no existe")
            .assertIsDisplayed()

        // Validamos el enlace
        composeTestRule
            .onNodeWithText("¿No tienes cuenta? Regístrate aquí", useUnmergedTree = true)
            .assertExists("El enlace de registro no existe")
            .assertIsDisplayed()
    }
}