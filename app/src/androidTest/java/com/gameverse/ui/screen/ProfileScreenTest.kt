package com.gameverse.ui.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule // <-- Importante

import com.gameverse.data.model.User
import com.gameverse.ui.screens.profile.ProfileScreen
import com.gameverse.ui.state.MainUiState
import com.gameverse.ui.theme.GameverseTheme
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

    // ¡NUEVA REGLA! Concede permisos de ubicación automáticamente para la prueba
    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private lateinit var fakeMainViewModel: FakeMainViewModel
    private lateinit var fakeUbicacionViewModel: FakeUbicacionViewModel

    @Before
    fun setUp() {
        fakeMainViewModel = FakeMainViewModel()
        fakeUbicacionViewModel = FakeUbicacionViewModel()
    }

    @Test
    fun testProfileScreen_WhenLoaded_ShowsUserData() {
        // 1. Creamos usuario
        val testUser = User(
            id = 1,
            username = "TestUser",
            fullName = "Nombre Completo de Prueba",
            email = "test@gameverse.com",
            password = "password123",
            memberSince = "Octubre 2025",
            avatarUrl = "https://example.com/avatar.png"
        )

        // 2. Configuramos estado
        fakeMainViewModel.setState(
            MainUiState(
                isLoading = false,
                userProfile = testUser
            )
        )

        // 3. Lanzamos pantalla
        composeTestRule.setContent {
            GameverseTheme {
                ProfileScreen(
                    mainViewModel = fakeMainViewModel,
                    ubicacionViewModel = fakeUbicacionViewModel,
                    onLogout = { }
                )
            }
        }

        composeTestRule.waitForIdle()

        // 4. Validaciones superiores
        composeTestRule.onNodeWithText("Nombre Completo de Prueba").assertIsDisplayed()
        composeTestRule.onNodeWithText("@TestUser").assertIsDisplayed()

        // 5. Validaciones con Scroll
        composeTestRule
            .onNodeWithText("test@gameverse.com")
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Cambiar foto")
            .performScrollTo()
            .assertIsDisplayed()

        // 6. Validamos ubicación (Ahora debería funcionar gracias a GrantPermissionRule)
        // Nota: Asegúrate de que el texto sea EXACTO al de tu UI ("Ubicación actual" o "Ubicación actual:")
        // Usamos substring=true para ser más flexibles
        composeTestRule
            .onNodeWithText("Ubicación actual", substring = true)
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Dirección de Prueba Falsa")
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Cerrar Sesión")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun testProfileScreen_WhenLoading_ShowsLoader() {
        fakeMainViewModel.setState(MainUiState(isLoading = true))

        composeTestRule.setContent {
            GameverseTheme {
                ProfileScreen(
                    mainViewModel = fakeMainViewModel,
                    ubicacionViewModel = fakeUbicacionViewModel,
                    onLogout = { }
                )
            }
        }

        composeTestRule.onNodeWithText("Nombre Completo de Prueba").assertDoesNotExist()
    }
}