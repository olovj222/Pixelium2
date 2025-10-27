package com.gameverse.viewmodel

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gameverse.data.model.User
import com.gameverse.data.repository.AppRepository
import com.gameverse.ui.state.MainUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class) // Necesario para flatMapLatest
class MainViewModel(
    private val repository: AppRepository,
    // 1. RECIBE LA LAMBDA DESDE LA FACTORY
    private val getCurrentUserId: () -> Int?
) : ViewModel() {

    // --- Flows base desde el Repositorio (Room) ---
    private val productsFlow: Flow<List<com.gameverse.data.model.Product>> = repository.getProducts()
    private val newsFlow: Flow<List<com.gameverse.data.model.NewsItem>> = repository.getNews()
    private val highlightsFlow: Flow<List<com.gameverse.data.model.NewsItem>> = repository.getHomeHighlights()

    // --- Flow dinámico para el Perfil del Usuario ---
    // Usamos 'flatMapLatest' para reaccionar a cambios en el ID del usuario
    private val userProfileFlow: Flow<User?> = snapshotFlow { getCurrentUserId() } // Convierte la lambda en un Flow
        .flatMapLatest { userId ->
            if (userId != null) {
                // Si hay un ID, crea un Flow para buscar ese usuario
                flow { emit(repository.getUserById(userId)) }
            } else {
                // Si no hay ID (logout), emite null inmediatamente
                flowOf(null)
            }
        }

    // --- Combina todos los Flows en el Estado Final para la UI ---
    val uiState: StateFlow<MainUiState> = combine(
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
            userProfile = user, // El usuario correcto obtenido por ID
            isLoading = false // Ya no manejamos isLoading manualmente aquí
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = MainUiState(isLoading = true) // Estado inicial mientras cargan los Flows
    )

    // Función de logout (sigue siendo responsabilidad de MainActivity/AppNavigation)
    fun logout() {
        // Podrías añadir lógica aquí si necesitas limpiar algo específico del MainViewModel
    }
}

