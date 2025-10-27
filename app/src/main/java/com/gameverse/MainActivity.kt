package com.gameverse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gameverse.ui.navigation.AppNavigation
import com.gameverse.ui.theme.GameverseTheme
import com.gameverse.viewmodel.LoginViewModel
import com.gameverse.viewmodel.factory.GameverseViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Obtiene la instancia de Application y el Repositorio
        val application = application as GameverseApplication
        val repository = application.repository

        setContent {
            GameverseTheme {
                // Obtiene el LoginViewModel (necesita una factory inicial sin userId)
                val loginViewModel: LoginViewModel = viewModel(
                    factory = GameverseViewModelFactory(repository) { null } // Factory inicial
                )
                val loginState by loginViewModel.uiState.collectAsState()

                // --- ESTADO DE AUTENTICACIÓN Y USER ID ---
                // 'currentUserId' guarda el ID del usuario logueado
                var currentUserId by remember { mutableStateOf<Int?>(null) }
                // 'isAuthenticated' controla qué NavHost se muestra
                var isAuthenticated by remember { mutableStateOf(false) }

                // Efecto para actualizar el estado cuando el login cambie
                LaunchedEffect(loginState.loginSuccess, loginState.loggedInUserId) {
                    isAuthenticated = loginState.loginSuccess
                    currentUserId = loginState.loggedInUserId // Actualiza el ID guardado
                }

                // --- Crea la Factory DENTRO de @Composable ---
                // Se recrea si 'currentUserId' cambia, pasando el ID actualizado
                val viewModelFactory = remember(currentUserId) {
                    GameverseViewModelFactory(repository) { currentUserId }
                }

                // --- Acción de Logout ---
                val performLogout = {
                    loginViewModel.resetLoginState() // Resetea el estado del LoginViewModel
                    currentUserId = null // Limpia el ID guardado
                    isAuthenticated = false // Cambia el estado de autenticación
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    // Imagen de fondo global
                    Image(
                        painter = painterResource(id = R.drawable.home_background),
                        contentDescription = "Imagen de fondo global",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        colorFilter = ColorFilter.tint(Color.Black.copy(alpha = 0.6f), blendMode = BlendMode.Darken)
                    )
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color.Transparent // Surface transparente
                    ) {
                        // Pasa la factory, el estado y la acción de logout a AppNavigation
                        AppNavigation(
                            viewModelFactory = viewModelFactory,
                            isAuthenticated = isAuthenticated,
                            onLogout = performLogout
                        )
                    }
                }
            }
        }
    }
}

