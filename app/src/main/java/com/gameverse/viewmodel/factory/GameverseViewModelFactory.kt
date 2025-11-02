package com.gameverse.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gameverse.data.repository.AppRepository
import com.gameverse.viewmodel.CartViewModel
import com.gameverse.viewmodel.LoginViewModel
import com.gameverse.viewmodel.MainViewModel


class GameverseViewModelFactory(
    private val repository: AppRepository,
    // Lambda que devuelve el ID del usuario actual (o null si no hay nadie logueado)
    private val getCurrentUserId: () -> Int?
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Si se pide un LoginViewModel, lo crea pasandole el repositorio.
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository) as T
        }
        // Si se pide un MainViewModel, lo crea pasandole el repositorio Y la lambda.
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository, getCurrentUserId) as T // <-- Pasa la lambda
        }
        // Si se pide un CartViewModel, lo crea (no necesita nada extra por ahora).
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel() as T
        }
        // Si se pide un ViewModel desconocido, lanza una excepción.
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

