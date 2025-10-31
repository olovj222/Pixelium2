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
    var email by remember { mutableStateOf("") } // <-- Nuevo campo
    val context = LocalContext.current


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


        if (uiState.isLoading) {
            FullScreenLoader()
        }
    }
}