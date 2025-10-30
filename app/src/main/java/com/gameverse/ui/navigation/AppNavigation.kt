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

    isAuthenticated: Boolean,
    onLogout: () -> Unit
) {
    // acá se crean los viewmodels
    val loginViewModel: LoginViewModel = viewModel(factory = viewModelFactory)
    val mainViewModel: MainViewModel = viewModel(factory = viewModelFactory)
    val cartViewModel: CartViewModel = viewModel(factory = viewModelFactory)


    val mainNavController = rememberNavController()


    if (!isAuthenticated) {
        // en caso de no estar autenticado
        // Mostramos un NavHost solo para Login y Registro
        val authNavController = rememberNavController()
        NavHost(navController = authNavController, startDestination = "login") {
            composable("login") {
                LoginScreen(
                    loginViewModel = loginViewModel,

                    onLoginSuccess = { /* MainActivity se encarga vía LaunchedEffect */ },
                    onNavigateToRegister = { authNavController.navigate("register") }
                )
            }
            composable("register") {
                RegisterScreen(
                    loginViewModel = loginViewModel,
                    onRegistrationSuccess = { authNavController.popBackStack() } // acá de devuelve a login
                )
            }
        }
    } else {
        // en caso de estar autenticado
        // se muestra el navhost de la apk
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
                CartScreen(cartViewModel = cartViewModel,
                    onGoBack = { mainNavController.popBackStack()})
            }
        }
    }
}

