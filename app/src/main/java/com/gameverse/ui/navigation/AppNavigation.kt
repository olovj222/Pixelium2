package com.gameverse.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gameverse.ui.screens.admin.AdminScreen // Importar AdminScreen
import com.gameverse.ui.screens.cart.CartScreen
import com.gameverse.ui.screens.login.LoginScreen
import com.gameverse.ui.screens.main.MainScreenContainer
import com.gameverse.ui.screens.register.RegisterScreen
import com.gameverse.viewmodel.CartViewModel
import com.gameverse.viewmodel.LoginViewModel
import com.gameverse.viewmodel.MainViewModel
import com.gameverse.viewmodel.factory.GameverseViewModelFactory

@Composable
fun AppNavigation(
    viewModelFactory: GameverseViewModelFactory,
    isAuthenticated: Boolean,
    onLogout: () -> Unit
) {
    // Los ViewModels se crean aquí usando la Factory
    val loginViewModel: LoginViewModel = viewModel(factory = viewModelFactory)
    val mainViewModel: MainViewModel = viewModel(factory = viewModelFactory)
    val cartViewModel: CartViewModel = viewModel(factory = viewModelFactory)

    // Necesitamos observar el estado del login para saber si es admin
    // Esto se usa SOLO para decidir qué mostrar cuando 'isAuthenticated' es true
    val loginState = loginViewModel.uiState.collectAsState().value

    if (!isAuthenticated) {
        // --- ESTADO NO AUTENTICADO (Login / Registro) ---
        val authNavController = rememberNavController()

        NavHost(navController = authNavController, startDestination = "login") {
            composable("login") {
                LoginScreen(
                    loginViewModel = loginViewModel,
                    // La navegación al éxito la maneja MainActivity al observar el estado
                    onLoginSuccess = { },
                    onNavigateToRegister = { authNavController.navigate("register") }
                )
            }
            composable("register") {
                RegisterScreen(
                    loginViewModel = loginViewModel,
                    onRegistrationSuccess = { authNavController.popBackStack() }
                )
            }
        }
    } else {
        // --- ESTADO AUTENTICADO (App Principal o Admin) ---

        if (loginState.isAdmin) {
            // --- MOSTRAR PANEL DE ADMIN ---
            // Pasamos la viewModelFactory para que AdminScreen pueda obtener su propio ViewModel
            AdminScreen(
                viewModelFactory = viewModelFactory,
                onLogout = onLogout
            )
        } else {
            // --- MOSTRAR APP DE USUARIO NORMAL ---
            val mainNavController = rememberNavController()

            NavHost(navController = mainNavController, startDestination = "main") {
                composable("main") {
                    MainScreenContainer(
                        mainViewModel = mainViewModel,
                        cartViewModel = cartViewModel,
                        onNavigateToCart = { mainNavController.navigate("cart") },
                        onLogout = onLogout
                    )
                }
                composable("cart") {
                    CartScreen(
                        cartViewModel = cartViewModel,
                        onGoBack = { mainNavController.popBackStack() }
                    )
                }
            }
        }
    }
}