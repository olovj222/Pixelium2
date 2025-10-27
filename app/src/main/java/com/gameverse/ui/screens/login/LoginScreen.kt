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
    // onLoginSuccess ya no es estrictamente necesario para navegar,
    // pero lo mantenemos por si quieres hacer algo más al lograr el login.
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    // Observa el estado del ViewModel.
    val uiState by loginViewModel.uiState.collectAsState()

    // Estados locales para los campos de texto.
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Este LaunchedEffect ahora solo llama a la lambda onLoginSuccess.
    // La navegación real ocurre en MainActivity basada en uiState.loginSuccess.
    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            onLoginSuccess() // Puedes usar esto para limpiar campos, mostrar un mensaje, etc.
        }
    }

    // Usamos Box para alinear el logo arriba y el formulario en el centro.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
    ) {

        // Logo alineado arriba y centrado.
        LogoImage(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp)
        )

        // Formulario (campos y botón) centrado.
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

            // Muestra mensaje de error si existe.
            uiState.error?.let { errorMessage ->
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Botón de Iniciar Sesión.
            NeonButton(
                onClick = { loginViewModel.login(username, password) },
                text = "Iniciar Sesión",
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Texto clickeable para ir a Registro.
            Text(
                text = "¿No tienes cuenta? Regístrate aquí",
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable(enabled = !uiState.isLoading) {
                    onNavigateToRegister() // Llama a la función de navegación a Registro.
                }
            )
        }

        // Loader encima de todo si está cargando.
        if (uiState.isLoading) {
            FullScreenLoader()
        }
    }
}

