package com.gameverse.ui.state

import com.gameverse.data.model.CartProduct
import com.gameverse.data.model.NewsItem
import com.gameverse.data.model.Product
import com.gameverse.data.model.User // <-- Importa el nuevo 'User' de la base de datos


data class LoginUiState(
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false,
    val isAdmin: Boolean = false,
    val registrationSuccess: Boolean = false,
    val loggedInUserId: Int? = null,
    val error: String? = null,
    val usernameError: String? = null,
    val passwordError: String? = null,
    val emailError: String? = null
)

//estado de la UI para cada pantalla
data class MainUiState(
    val isLoading: Boolean = true,
    val products: List<Product> = emptyList(),
    val news: List<NewsItem> = emptyList(),
    val homeHighlights: List<NewsItem> = emptyList(),
    val userProfile: User? = null, // <-- Usa 'User' en lugar de 'UserProfile'
    val error: String? = null
)

//estado de UI en la pantalla de carrito de compra
data class CartUiState(
    val cartItems: List<CartProduct> = emptyList(), // <-- Esta es la clave
    val total: Double = 0.0,
    val paymentSuccess: Boolean = false
)

