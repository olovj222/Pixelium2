package com.gameverse.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gameverse.data.model.User
import com.gameverse.data.repository.AppRepository
import com.gameverse.ui.state.MainUiState
import kotlinx.coroutines.flow.*

class MainViewModel(
    // 1. AHORA RECIBE EL REPOSITORIO DESDE LA FACTORY
    private val repository: AppRepository
) : ViewModel() {

    // El ViewModel ahora es MUCHO más simple gracias a los Flows de Room.

    // 2. OBTENEMOS LOS FLOWS DIRECTAMENTE DEL REPOSITORIO
    //    Room se encarga de emitir nuevos valores si los datos cambian.
    private val productsFlow: Flow<List<com.gameverse.data.model.Product>> = repository.getProducts()
    private val newsFlow: Flow<List<com.gameverse.data.model.NewsItem>> = repository.getNews()
    private val highlightsFlow: Flow<List<com.gameverse.data.model.NewsItem>> = repository.getHomeHighlights()

    // 3. OBTENEMOS EL PERFIL DEL USUARIO
    //    (Este es un placeholder. En una app real, guardaríamos el ID del usuario
    //    que inició sesión y lo usaríamos aquí)
    private val userProfileFlow: Flow<User?> = flow {
        // Por ahora, intenta cargar el primer usuario de la base de datos
        // (El usuario que se registró primero tendrá ID 1)
        emit(repository.getUserById(1))
    }

    // 4. COMBINAMOS TODOS LOS FLOWS EN UN ÚNICO ESTADO PARA LA UI
    //    'combine' toma los últimos valores emitidos por cada Flow
    //    y nos permite crear un 'MainUiState' actualizado.
    val uiState: StateFlow<MainUiState> = combine(
        productsFlow,
        newsFlow,
        highlightsFlow,
        userProfileFlow
    ) { products, news, highlights, user ->
        // Creamos el estado combinado
        MainUiState(
            products = products,
            news = news,
            homeHighlights = highlights,
            userProfile = user, // <-- Usa el User de la DB
            isLoading = false // Ya no necesitamos manejar 'isLoading' manualmente aquí
        )
    }.stateIn(
        scope = viewModelScope, // El scope del ViewModel
        started = SharingStarted.WhileSubscribed(5000), // Empieza a colectar cuando la UI es visible
        initialValue = MainUiState(isLoading = true) // Estado inicial mientras cargan los Flows
    )

    // La función de logout sigue igual
    fun logout() {
        // Lógica para cerrar sesión (ej. borrar el ID de usuario guardado)
    }
}

