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

                flow { emit(repository.getUserById(userId)) }
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


}

