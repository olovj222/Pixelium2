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
    open fun login(user: String, pass: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            delay(2500L)
            // Llama al repositorio (Room)

            val loginResult: User? = repository.login(user, pass)
            if (loginResult != null) {
                val isAdminUser = loginResult.username == "admin"
                // Guardamos el ID del usuario en el estado
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        loginSuccess = true,
                        isAdmin = isAdminUser,
                        loggedInUserId = loginResult.id // GUARDAMOS EL ID
                    )
                }
            } else {
                // en caso de tener un fracaso
                _uiState.update { it.copy(isLoading = false, error = "Usuario o contraseña incorrectos") }
            }
        }
    }

    // aca intenta de insertar un nuevo usuario a la BD

    open fun register(user: String, pass: String, email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Validaciones basicas
            if (user.length < 4 || pass.length < 6 || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _uiState.update { it.copy(isLoading = false, error = "Verifica los datos (Usuario mín 4, Pass mín 6, Email válido).") }
                return@launch
            }

            // Crea el objeto User para la base de datos
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
                // Registro exitoso
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

