package com.gameverse.ui.state

import com.gameverse.data.model.CartProduct
import com.gameverse.data.model.NewsItem
import com.gameverse.data.model.Product
import com.gameverse.data.model.User // <-- Importa el nuevo 'User' de la base de datos

/**
 * Representa el estado de la UI para la pantalla de Login y Registro.
 */
data class LoginUiState(
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false,
    val registrationSuccess: Boolean = false, // <-- Propiedad añadida para el registro
    val loggedInUserId: Int? = null,
    val error: String? = null
)

/**
 * Representa el estado de la UI para las pantallas principales (Home, Productos, Noticias, Perfil).
 */
data class MainUiState(
    val isLoading: Boolean = true,
    val products: List<Product> = emptyList(),
    val news: List<NewsItem> = emptyList(),
    val homeHighlights: List<NewsItem> = emptyList(),
    val userProfile: User? = null, // <-- Usa 'User' en lugar de 'UserProfile'
    val error: String? = null
)

/**
 * Representa el estado de la UI para la pantalla del Carrito de Compras.
 * (Esta clase no ha cambiado).
 */
data class CartUiState(
    val cartItems: List<CartProduct> = emptyList(),
    val total: Double = 0.0,
    val paymentSuccess: Boolean = false
)

