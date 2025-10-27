package com.gameverse.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gameverse.data.model.User
import com.gameverse.data.repository.AppRepository
import com.gameverse.ui.state.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

class LoginViewModel(
    private val repository: AppRepository // Recibe el Repositorio conectado a Room
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    /**
     * Intenta iniciar sesión consultando la base de datos.
     */
    fun login(user: String, pass: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            // Llama al repositorio (Room)
            val loginResult: User? = repository.login(user, pass)
            if (loginResult != null) {
                // Éxito
                _uiState.update { it.copy(isLoading = false, loginSuccess = true) }
            } else {
                // Fracaso
                _uiState.update { it.copy(isLoading = false, error = "Usuario o contraseña incorrectos") }
            }
        }
    }

    /**
     * ¡FUNCIÓN DE REGISTRO!
     * Intenta insertar un nuevo usuario en la base de datos.
     */
    fun register(user: String, pass: String, email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Validaciones básicas
            if (user.length < 4 || pass.length < 6 || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _uiState.update { it.copy(isLoading = false, error = "Verifica los datos (Usuario mín 4, Pass mín 6, Email válido).") }
                return@launch
            }

            // Crea el objeto User para la base de datos
            val newUser = User(
                username = user,
                password = pass, // ¡IMPORTANTE! En una app real, hashear la contraseña antes de guardarla.
                email = email,
                fullName = user, // Nombre completo por defecto
                memberSince = Date().toString(), // Fecha actual
                avatarUrl = "https://placehold.co/300x300/212121/00BCD4?text=${user.take(2).uppercase()}" // Avatar genérico
            )

            // Llama al repositorio para intentar insertar
            val registerResult = repository.registerUser(newUser)

            if (registerResult.isSuccess) {
                // Registro exitoso
                _uiState.update { it.copy(isLoading = false, registrationSuccess = true) }
            } else {
                // Error (probablemente el usuario ya existe)
                _uiState.update { it.copy(isLoading = false, error = registerResult.exceptionOrNull()?.message ?: "Error al registrar") }
            }
        }
    }

    /**
     * ¡FUNCIÓN PARA LIMPIAR EL ESTADO!
     * Resetea 'registrationSuccess' a 'false' después de que la UI
     * haya reaccionado (mostrando el Toast y navegando).
     */
    fun resetRegistrationStatus() {
        _uiState.update { it.copy(registrationSuccess = false) }
    }
}

