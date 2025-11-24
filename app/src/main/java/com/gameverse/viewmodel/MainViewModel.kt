package com.gameverse.viewmodel

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gameverse.data.model.User
import com.gameverse.data.repository.AppRepository
import com.gameverse.ui.state.MainUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class) // Necesario para flatMapLatest
open class MainViewModel(
    private val repository: AppRepository,
    // RECIBE LA LAMBDA DESDE LA FACTORY
    private val getCurrentUserId: () -> Int?
) : ViewModel() {


    private val productsFlow: Flow<List<com.gameverse.data.model.Product>> = repository.getProducts()
    private val newsFlow: Flow<List<com.gameverse.data.model.NewsItem>> = repository.getNews()
    private val highlightsFlow: Flow<List<com.gameverse.data.model.NewsItem>> = repository.getHomeHighlights()


    private val userProfileFlow: Flow<User?> = snapshotFlow { getCurrentUserId() }
        .flatMapLatest { userId ->
            if (userId != null) {
                // Esto conecta el "cable" directo a la base de datos
                repository.getUserFlow(userId)
            } else {
                flowOf(null)
            }
        }

    init {
        viewModelScope.launch {
            repository.syncProductsFromApi()  // SIEMPRE sincroniza
        }
    }

    // --- Combina todos los flows en el Estado Final para la UI
    open val uiState: StateFlow<MainUiState> = combine(
        productsFlow,
        newsFlow,
        highlightsFlow,
        userProfileFlow
    ) { products, news, highlights, user ->
        // Crea el MainUiState combinado
        MainUiState(
            products = products,
            news = news,
            homeHighlights = highlights,
            userProfile = user,
            isLoading = products.isEmpty() && news.isEmpty()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = MainUiState(isLoading = true)
    )

    fun updateUser(currentUser: User, newName: String, newEmail: String, newPass: String, newFullName: String) {
        viewModelScope.launch {
            try {
                // Creamos una copia del usuario actual con los nuevos datos
                val updatedUser = currentUser.copy(
                    username = newName,
                    email = newEmail,
                    password = newPass, // En una app real, esto debería hashearse de nuevo
                    fullName = newFullName
                )
                // Guardamos en BD. Al hacerlo, el userProfileFlow avisará a toda la app del cambio
                repository.updateUser(updatedUser)
            } catch (e: Exception) {
                // Aquí podrías manejar errores, ej: si el username ya existe
                e.printStackTrace()
            }
        }
    }

}

