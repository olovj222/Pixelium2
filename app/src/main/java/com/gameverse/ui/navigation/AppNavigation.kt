package com.gameverse.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gameverse.ui.screens.cart.CartScreen
import com.gameverse.ui.screens.login.LoginScreen
import com.gameverse.ui.screens.main.MainScreenContainer
// Importa la nueva pantalla de registro
import com.gameverse.ui.screens.register.RegisterScreen
import com.gameverse.viewmodel.CartViewModel
import com.gameverse.viewmodel.LoginViewModel
import com.gameverse.viewmodel.MainViewModel
import com.gameverse.viewmodel.factory.GameverseViewModelFactory

@Composable
fun AppNavigation(viewModelFactory: GameverseViewModelFactory) { // <-- 1. Recibe la Fábrica
    val navController = rememberNavController()

    // 2. Usa la Fábrica para crear los ViewModels
    //    Compose se asegura de que sea la misma instancia en toda la app
    val loginViewModel: LoginViewModel = viewModel(factory = viewModelFactory)
    val mainViewModel: MainViewModel = viewModel(factory = viewModelFactory)
    val cartViewModel: CartViewModel = viewModel(factory = viewModelFactory) // Aunque no use repo, lo creamos con la factory

    NavHost(navController = navController, startDestination = "login") {

        // --- Pantalla de Login ---
        composable("login") {
            LoginScreen(
                loginViewModel = loginViewModel,
                onLoginSuccess = {
                    // Navega a main y limpia la pila para no volver al login
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                // Navega a la pantalla de registro
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        // --- ¡NUEVA Pantalla de Registro! ---
        composable("register") {
            RegisterScreen(
                loginViewModel = loginViewModel, // Reutiliza el LoginViewModel para la lógica de registro
                onRegistrationSuccess = {
                    // Después de registrarse con éxito, vuelve a la pantalla de login
                    navController.popBackStack()
                }
            )
        }

        // --- Pantalla Principal (Contenedor con NavBar) ---
        composable("main") {
            MainScreenContainer(
                mainViewModel = mainViewModel,
                cartViewModel = cartViewModel,
                onNavigateToCart = {
                    navController.navigate("cart")
                }
            )
        }

        // --- Pantalla del Carrito ---
        composable("cart") {
            CartScreen(cartViewModel = cartViewModel)
        }
    }
}

