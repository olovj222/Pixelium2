package com.gameverse.ui.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4

// Imports de la App (código principal)
import com.gameverse.data.model.User
import com.gameverse.ui.screens.profile.ProfileScreen
import com.gameverse.ui.state.MainUiState
import com.gameverse.ui.theme.GameverseTheme

// Imports de Prueba (desde 'util')
import com.gameverse.util.FakeMainViewModel
import com.gameverse.util.FakeUbicacionViewModel

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date

@RunWith(AndroidJUnit4::class)
class ProfileScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var fakeMainViewModel: FakeMainViewModel
    private lateinit var fakeUbicacionViewModel: FakeUbicacionViewModel

    @Before
    fun setUp() {
        fakeMainViewModel = FakeMainViewModel()
        fakeUbicacionViewModel = FakeUbicacionViewModel()
    }

    @Test
    fun testProfileScreen_WhenLoaded_ShowsUserData() {
        // 1. Creamos un usuario de prueba
        val testUser = User(
            id = 1,
            username = "TestUser",
            fullName = "Nombre Completo de Prueba",
            email = "test@gameverse.com",
            password = "",
            memberSince = Date().toString(),
            avatarUrl = ""
        )

        // 2. Forzamos el estado de "Cargado" con el usuario de prueba
        fakeMainViewModel.setState(
            MainUiState(isLoading = false, userProfile = testUser)
        )

        // 3. Lanzamos la pantalla
        composeTestRule.setContent {
            GameverseTheme {
                ProfileScreen(
                    mainViewModel = fakeMainViewModel,
                    ubicacionViewModel = fakeUbicacionViewModel,
                    onLogout = { } // Pasamos una lambda vacía
                )
            }
        }

        // 4. Validamos que los títulos y contenido del usuario se muestren
        composeTestRule.onNodeWithText("Nombre Completo de Prueba").assertIsDisplayed()
        composeTestRule.onNodeWithText("@TestUser").assertIsDisplayed()
        composeTestRule.onNodeWithText("test@gameverse.com").assertIsDisplayed()

        // 5. Validamos los botones
        composeTestRule.onNodeWithText("Cambiar foto").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cerrar Sesión").assertIsDisplayed()

        // 6. Validamos la sección de ubicación (usando el texto del Fake)
        composeTestRule.onNodeWithText("Ubicación actual:").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dirección de Prueba Falsa").assertIsDisplayed()
    }

    @Test
    fun testProfileScreen_WhenLoading_ShowsLoader() {
        // 1. Forzamos el estado de "Cargando"
        fakeMainViewModel.setState(MainUiState(isLoading = true))

        // 2. Lanzamos la pantalla
        composeTestRule.setContent {
            GameverseTheme {
                ProfileScreen(
                    mainViewModel = fakeMainViewModel,
                    ubicacionViewModel = fakeUbicacionViewModel,
                    onLogout = { }
                )
            }
        }

        // 3. Validamos que los datos del usuario NO se muestren
        composeTestRule.onNodeWithText("Nombre Completo de Prueba").assertDoesNotExist()
        composeTestRule.onNodeWithText("@TestUser").assertDoesNotExist()
    }
}