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
    onNavigateToRegister: () -> Unit
) {
    // Estado del ViewModel
    val uiState by loginViewModel.uiState.collectAsState()

    // Estados locales para los campos de texto
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

        // Logo alineado arriba
        LogoImage(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp)
        )

        // Formulario centrado
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // CAMPO USUARIO con validación
            NeonTextField(
                value = username,
                onValueChange = { username = it },
                label = "Usuario",
                errorMessage = uiState.usernameError // <-- Conectado al error específico
            )

            Spacer(modifier = Modifier.height(16.dp))

            // CAMPO CONTRASEÑA con validación
            NeonTextField(
                value = password,
                onValueChange = { password = it },
                label = "Contraseña",
                keyboardType = KeyboardType.Password,
                errorMessage = uiState.passwordError // <-- Conectado al error específico
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Muestra mensaje de error GENÉRICO (ej: credenciales incorrectas)
            uiState.error?.let { errorMessage ->
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Botón de iniciar sesión
            NeonButton(
                onClick = { loginViewModel.login(username, password) },
                text = "Iniciar Sesión",
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Texto clickeable de Registro
            Text(
                text = "¿No tienes cuenta? Regístrate aquí",
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable(enabled = !uiState.isLoading) {
                    onNavigateToRegister()
                }
            )
        }

        if (uiState.isLoading) {
            FullScreenLoader()
        }
    }
}