package com.gameverse.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gameverse.data.repository.AppRepository
import com.gameverse.viewmodel.CartViewModel
import com.gameverse.viewmodel.LoginViewModel
import com.gameverse.viewmodel.MainViewModel

/**
 * Esta es una "constructora" personalizada para nuestros ViewModels.
 * Sabe cómo crear un LoginViewModel y un MainViewModel porque le pasamos
 * el Repositorio que necesitan.
 */
class GameverseViewModelFactory(
    private val repository: AppRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        // CartViewModel no necesita el repositorio (por ahora)
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
