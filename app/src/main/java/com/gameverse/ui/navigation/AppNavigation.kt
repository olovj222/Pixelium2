package com.gameverse.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gameverse.ui.screens.cart.CartScreen // Sigue siendo necesario si MainScreen navega a él
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
    // 1. Recibe el estado de autenticación y la acción de logout
    isAuthenticated: Boolean,
    onLogout: () -> Unit
) {
    // Los ViewModels se crean aquí, pero solo se usan donde se necesiten
    val loginViewModel: LoginViewModel = viewModel(factory = viewModelFactory)
    val mainViewModel: MainViewModel = viewModel(factory = viewModelFactory)
    val cartViewModel: CartViewModel = viewModel(factory = viewModelFactory)

    // Necesitamos un NavController para la navegación DENTRO de la app principal (Main -> Cart)
    val mainNavController = rememberNavController()

    // --- CAMBIO PRINCIPAL: Decisión basada en el estado ---
    if (!isAuthenticated) {
        // --- ESTADO NO AUTENTICADO ---
        // Mostramos un NavHost solo para Login y Registro
        val authNavController = rememberNavController()
        NavHost(navController = authNavController, startDestination = "login") {
            composable("login") {
                LoginScreen(
                    loginViewModel = loginViewModel,
                    // onLoginSuccess ya no navega, solo actualiza el estado en MainActivity
                    onLoginSuccess = { /* MainActivity se encarga vía LaunchedEffect */ },
                    onNavigateToRegister = { authNavController.navigate("register") }
                )
            }
            composable("register") {
                RegisterScreen(
                    loginViewModel = loginViewModel,
                    onRegistrationSuccess = { authNavController.popBackStack() } // Vuelve al login
                )
            }
        }
    } else {
        // --- ESTADO AUTENTICADO ---
        // Mostramos el NavHost principal de la app
        NavHost(navController = mainNavController, startDestination = "main") {
            composable("main") {
                MainScreenContainer(
                    mainViewModel = mainViewModel,
                    cartViewModel = cartViewModel,
                    // La navegación al carrito ahora usa mainNavController
                    onNavigateToCart = { mainNavController.navigate("cart") },
                    // Pasamos la acción de logout recibida desde MainActivity
                    onLogout = onLogout
                )
            }
            composable("cart") {
                CartScreen(cartViewModel = cartViewModel)
            }
            // Aquí podrías añadir más destinos si tu app crece (ej: "detalle_producto/{id}")
        }
    }
}

