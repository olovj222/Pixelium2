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
    onRegistrationSuccess: () -> Unit
) {
    val uiState by loginViewModel.uiState.collectAsState()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    val context = LocalContext.current

    // Efecto para manejar el éxito del registro
    LaunchedEffect(uiState.registrationSuccess) {
        if (uiState.registrationSuccess) {
            Toast.makeText(context, "¡Registro exitoso! Por favor, inicia sesión.", Toast.LENGTH_LONG).show()
            loginViewModel.resetRegistrationStatus()
            onRegistrationSuccess()
        }
    }

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

            // CAMPO USUARIO con validación
            NeonTextField(
                value = username,
                onValueChange = { username = it },
                label = "Nombre de Usuario",
                errorMessage = uiState.usernameError // <-- Conectamos el error específico
            )

            Spacer(modifier = Modifier.height(16.dp))

            // CAMPO EMAIL con validación
            NeonTextField(
                value = email,
                onValueChange = { email = it },
                label = "Correo Electrónico",
                keyboardType = KeyboardType.Email,
                errorMessage = uiState.emailError // <-- Conectamos el error específico
            )

            Spacer(modifier = Modifier.height(16.dp))

            // CAMPO CONTRASEÑA con validación
            NeonTextField(
                value = password,
                onValueChange = { password = it },
                label = "Contraseña",
                keyboardType = KeyboardType.Password,
                errorMessage = uiState.passwordError // <-- Conectamos el error específico
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Error Genérico (Ej: Error de base de datos o red que no es de validación)
            uiState.error?.let { errorMessage ->
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            NeonButton(
                onClick = { loginViewModel.register(username, password, email) },
                text = "Registrarse",
                enabled = !uiState.isLoading
            )
        }

        if (uiState.isLoading) {
            FullScreenLoader()
        }
    }
}