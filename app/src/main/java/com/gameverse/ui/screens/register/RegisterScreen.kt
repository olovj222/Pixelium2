package com.gameverse.ui.screens.register

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.gameverse.ui.components.FullScreenLoader
import com.gameverse.ui.components.NeonButton
import com.gameverse.ui.components.NeonTextField
import com.gameverse.viewmodel.LoginViewModel

@Composable
fun RegisterScreen(
    loginViewModel: LoginViewModel,
    onRegistrationSuccess: () -> Unit // Lambda para volver al Login
) {
    // 1. Observa el estado del LoginViewModel
    val uiState by loginViewModel.uiState.collectAsState()

    // 2. Estados locales para los campos de texto
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") } // <-- Nuevo campo
    val context = LocalContext.current

    // 3. Efecto para manejar el éxito del registro
    LaunchedEffect(uiState.registrationSuccess) {
        if (uiState.registrationSuccess) {
            Toast.makeText(context, "¡Registro exitoso! Por favor, inicia sesión.", Toast.LENGTH_LONG).show()
            loginViewModel.resetRegistrationStatus() // Limpia el estado
            onRegistrationSuccess() // Llama a la lambda para volver al Login
        }
    }

    // Usamos un Box para poder mostrar el Loader encima
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Crear Cuenta",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            NeonTextField(
                value = username,
                onValueChange = { username = it },
                label = "Nombre de Usuario"
            )
            Spacer(modifier = Modifier.height(16.dp))
            NeonTextField(
                value = email, // <-- Campo de Email
                onValueChange = { email = it },
                label = "Correo Electrónico",
                keyboardType = KeyboardType.Email // Teclado de email
            )
            Spacer(modifier = Modifier.height(16.dp))
            NeonTextField(
                value = password,
                onValueChange = { password = it },
                label = "Contraseña",
                keyboardType = KeyboardType.Password
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Muestra el mensaje de error si existe
            uiState.error?.let { errorMessage ->
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            NeonButton(
                onClick = { loginViewModel.register(username, password, email) },
                text = "Registrarse",
                enabled = !uiState.isLoading
            )
        }

        // Muestra el loader si está cargando
        if (uiState.isLoading) {
            FullScreenLoader()
        }
    }
}