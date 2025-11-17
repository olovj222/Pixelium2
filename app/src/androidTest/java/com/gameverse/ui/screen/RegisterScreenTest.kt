package com.gameverse.ui.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gameverse.util.FakeLoginViewModel
import com.gameverse.ui.screens.register.RegisterScreen
import com.gameverse.ui.theme.GameverseTheme

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// ¡YA NO HAY CLASES FALSAS DEFINIDAS AQUÍ!

@RunWith(AndroidJUnit4::class)
class RegisterScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var fakeLoginViewModel: FakeLoginViewModel

    @Before
    fun setUp() {
        fakeLoginViewModel = FakeLoginViewModel()
    }

    @Test
    fun testRegisterScreen_ValidateContentAndTitles() {
        composeTestRule.setContent {
            GameverseTheme {
                RegisterScreen(
                    loginViewModel = fakeLoginViewModel,
                    onRegistrationSuccess = { }
                )
            }
        }

        // Validaciones (siguen igual)
        composeTestRule.onNodeWithText("Crear Cuenta").assertIsDisplayed()
        composeTestRule.onNodeWithText("Nombre de Usuario").assertIsDisplayed()
        composeTestRule.onNodeWithText("Correo Electrónico").assertIsDisplayed()
        composeTestRule.onNodeWithText("Contraseña").assertIsDisplayed()
        composeTestRule.onNodeWithText("Registrarse").assertIsDisplayed()
    }

    @Test
    fun testRegisterScreen_ShowError_WhenErrorStateIsSet() {
        // Forzamos un estado de error
        fakeLoginViewModel.setState(com.gameverse.ui.state.LoginUiState(error = "El usuario ya existe"))

        composeTestRule.setContent {
            GameverseTheme {
                RegisterScreen(
                    loginViewModel = fakeLoginViewModel,
                    onRegistrationSuccess = { }
                )
            }
        }

        // Validamos el mensaje de error
        composeTestRule.onNodeWithText("El usuario ya existe").assertIsDisplayed()
    }
}