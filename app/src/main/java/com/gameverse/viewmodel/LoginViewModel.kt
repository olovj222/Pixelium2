package com.gameverse.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gameverse.data.model.User
import com.gameverse.data.repository.AppRepository
import com.gameverse.ui.state.LoginUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

open class LoginViewModel(
    private val repository: AppRepository // recibe el repositorio conectado a Room
) : ViewModel() {

    protected open val _uiState = MutableStateFlow(LoginUiState())
    open val uiState = _uiState.asStateFlow()


    //aca parte el intento de inicio de sesion con la base de datos
    // En LoginViewModel.kt

    open fun login(user: String, pass: String) {
        viewModelScope.launch {
            // 1. Limpiamos errores previos y activamos carga
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    usernameError = null,
                    passwordError = null
                )
            }

            // 2. Validaciones visuales (Campos vacíos)
            var hasEmptyFields = false
            if (user.isBlank()) {
                _uiState.update { it.copy(usernameError = "Debes ingresar tu usuario") }
                hasEmptyFields = true
            }
            if (pass.isBlank()) {
                _uiState.update { it.copy(passwordError = "Debes ingresar tu contraseña") }
                hasEmptyFields = true
            }

            if (hasEmptyFields) {
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }

            // Simulación de espera (opcional, puedes quitarlo si quieres mas velocidad)
            delay(2000L)

            // 3. LÓGICA DE VALIDACIÓN ESPECÍFICA

            // A. Buscamos si el usuario existe en la BD
            val existingUser = repository.getUserByUsername(user)

            if (existingUser == null) {
                // CASO 1: El usuario no existe -> Marcamos error en el campo Usuario
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        usernameError = "Usuario no registrado"
                    )
                }
            } else if (existingUser.password != pass) {
                // CASO 2: El usuario existe, pero la contraseña no coincide -> Marcamos error en Password
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        passwordError = "Contraseña incorrecta"
                    )
                }
            } else {
                // CASO 3: Usuario existe y contraseña coincide -> ÉXITO
                val isAdminUser = existingUser.username == "admin"
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        loginSuccess = true,
                        isAdmin = isAdminUser,
                        loggedInUserId = existingUser.id
                    )
                }
            }
        }
    }

    // aca intenta de insertar un nuevo usuario a la BD

    open fun register(user: String, pass: String, email: String) {
        viewModelScope.launch {
            // 1. Limpiamos errores previos y activamos carga
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    usernameError = null,
                    passwordError = null,
                    emailError = null
                )
            }

            // 2. Validaciones Individuales
            val usernameError = if (user.length < 4) "Mínimo 4 caracteres" else null
            val passwordError = if (pass.length < 6) "Mínimo 6 caracteres" else null
            val emailError = if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) "Email inválido" else null

            // 3. Si hay ALGÚN error, actualizamos el estado y detenemos el proceso
            if (usernameError != null || passwordError != null || emailError != null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        usernameError = usernameError,
                        passwordError = passwordError,
                        emailError = emailError
                    )
                }
                return@launch // Salimos de la corrutina, no vamos a la BD
            }

            // --- Si llegamos aquí, todo es válido ---

            val newUser = User(
                username = user,
                password = pass,
                email = email,
                fullName = user,
                memberSince = Date().toString(),
                avatarUrl = "https://placehold.co/300x300/212121/00BCD4?text=${user.take(2).uppercase()}"
            )

            val registerResult = repository.registerUser(newUser)

            if (registerResult.isSuccess) {
                _uiState.update { it.copy(isLoading = false, registrationSuccess = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = registerResult.exceptionOrNull()?.message ?: "Error al registrar") }
            }
        }
    }


    open fun resetRegistrationStatus() {
        _uiState.update { it.copy(registrationSuccess = false) }
    }


    // Crea un estado nuevo y limpio
    open fun resetLoginState() {
        _uiState.value = LoginUiState()
    }
}

