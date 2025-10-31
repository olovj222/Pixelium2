package com.gameverse.viewmodel

import androidx.lifecycle.ViewModel
import com.gameverse.data.model.CartProduct
import com.gameverse.data.model.Product
import com.gameverse.ui.state.CartUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class CartViewModel : ViewModel() {

    // Estado interno mutable. Solo el ViewModel puede modificarlo.
    private val _uiState = MutableStateFlow(CartUiState())
    // Estado público inmutable para que la UI lo observe.
    val uiState = _uiState.asStateFlow()

    // aca se añade un producto al carrito que es llamado desde Productos

    fun addToCart(product: Product) {
        // Convierte un Product en un CartProduct
        val cartProduct = CartProduct(
            id = product.id,
            name = product.name,
            price = product.price,
            imageUrl = product.imageUrl
        )
        // Actualiza el estado de forma segura
        _uiState.update { currentState ->
            // Crea una nueva lista añadiendo el nuevo producto
            val updatedItems = currentState.cartItems + cartProduct
            // Devuelve una copia del estado con la lista y el total actualizados
            currentState.copy(
                cartItems = updatedItems,
                total = recalculateTotal(updatedItems)
            )
        }
    }

    //elimina un producto del carrito usando el ID

    fun removeFromCart(productId: Int) {
        _uiState.update { currentState ->
            // Crea una nueva lista filtrando el producto a eliminar
            val updatedItems = currentState.cartItems.filterNot { it.id == productId }
            currentState.copy(
                cartItems = updatedItems,
                total = recalculateTotal(updatedItems)
            )
        }
    }

    //aca se simula el proceso dee pago y se limpia el carrito

    fun checkout() {
        // Resetea el estado a uno nuevo, vacío, pero con paymentSuccess = true
        _uiState.value = CartUiState(paymentSuccess = true)
    }

    //acá se reseta el estado del paymente a false


    fun resetPaymentStatus() {
        _uiState.update { currentState ->
            currentState.copy(paymentSuccess = false)
        }
    }

    //funcion para calcular el total del carrito

    private fun recalculateTotal(items: List<CartProduct>): Double {
        return items.sumOf { it.price }
    }
}

