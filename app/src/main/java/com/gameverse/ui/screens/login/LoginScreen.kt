package com.gameverse.ui.screens.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.gameverse.ui.components.FullScreenLoader
import com.gameverse.ui.components.LogoImage
import com.gameverse.ui.components.NeonButton
import com.gameverse.ui.components.NeonTextField
import com.gameverse.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    // 1. AÑADIMOS EL PARÁMETRO para la navegación a Registro
    onNavigateToRegister: () -> Unit
) {
    val uiState by loginViewModel.uiState.collectAsState()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
    ) {
        LogoImage(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp)
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NeonTextField(
                value = username,
                onValueChange = { username = it },
                label = "Usuario"
            )
            Spacer(modifier = Modifier.height(16.dp))
            NeonTextField(
                value = password,
                onValueChange = { password = it },
                label = "Contraseña",
                keyboardType = KeyboardType.Password
            )
            Spacer(modifier = Modifier.height(16.dp))

            uiState.error?.let { errorMessage ->
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            NeonButton(
                onClick = { loginViewModel.login(username, password) },
                text = "Iniciar Sesión",
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(24.dp)) // Espacio extra

            // 2. AÑADIMOS EL TEXTO CLICKEABLE PARA REGISTRARSE
            Text(
                text = "¿No tienes cuenta? Regístrate aquí",
                color = MaterialTheme.colorScheme.primary, // Color neón
                textDecoration = TextDecoration.Underline, // Subrayado
                modifier = Modifier.clickable(enabled = !uiState.isLoading) { // Habilita el click
                    onNavigateToRegister() // Llama a la función de navegación
                }
            )
        }

        if (uiState.isLoading) {
            FullScreenLoader()
        }
    }
}

