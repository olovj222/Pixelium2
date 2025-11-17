package com.gameverse.util

import android.app.Application // <-- Import necesario
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.test.core.app.ApplicationProvider // <-- Import para el contexto de prueba

// Imports del código principal
import com.gameverse.data.dao.NewsDao
import com.gameverse.data.dao.ProductDao
import com.gameverse.data.dao.UserDao
import com.gameverse.data.model.NewsItem
import com.gameverse.data.model.Product
import com.gameverse.data.model.User
import com.gameverse.data.repository.AppRepository
import com.gameverse.ui.state.CartUiState
import com.gameverse.ui.state.LoginUiState
import com.gameverse.ui.state.MainUiState
import com.gameverse.viewmodel.CartViewModel
import com.gameverse.viewmodel.LoginViewModel
import com.gameverse.viewmodel.MainViewModel
import com.gameverse.viewmodel.UbicacionViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf

// --- DAOs Falsos (Dummies) ---
class DummyUsuarioDAO : UserDao {
    override suspend fun insertUser(user: User) {}
    override suspend fun getUserByUsername(username: String): User? = null
    override suspend fun login(username: String, password: String): User? = null
    override suspend fun getUserById(userId: Int): User? = null
}

class DummyProductoDAO : ProductDao {
    override suspend fun insertAll(products: List<Product>) {}
    override fun getProducts(): Flow<List<Product>> = flowOf(emptyList())
    override suspend fun count(): Int = 0
}

class DummyNewsDAO : NewsDao {
    override suspend fun insertAll(newsItems: List<NewsItem>) {}
    override fun getNews(): Flow<List<NewsItem>> = flowOf(emptyList())
    override suspend fun count(): Int = 0
}

// --- Repositorio Falso ---
open class FakeRepository : AppRepository(
    DummyUsuarioDAO(),
    DummyProductoDAO(),
    DummyNewsDAO()
) {
    override suspend fun login(user: String, pass: String): User? = null
    override suspend fun registerUser(user: User): Result<Unit> = Result.success(Unit)
}

// --- ViewModels Falsos ---

class FakeLoginViewModel : LoginViewModel(FakeRepository()) {
    // IMPORTANTE: Usamos un StateFlow mutable que podemos controlar
    private val _fakeUiState = MutableStateFlow(LoginUiState())

    // Sobreescribimos el uiState del ViewModel original
    override val uiState: StateFlow<LoginUiState> = _fakeUiState.asStateFlow()

    // Métodos override que no hacen nada en el fake
    override fun login(user: String, pass: String) {
        // No hacer nada en tests
    }

    override fun register(user: String, pass: String, email: String) {
        // No hacer nada en tests
    }

    // Función pública para establecer el estado desde tests
    fun setState(newState: LoginUiState) {
        _fakeUiState.value = newState
    }
}

class FakeMainViewModel : MainViewModel(
    repository = FakeRepository(),
    getCurrentUserId = { null }
) {
    private val _fakeUiState = MutableStateFlow(MainUiState(isLoading = true))
    override val uiState = _fakeUiState.asStateFlow()
    fun setState(newState: MainUiState) { _fakeUiState.value = newState }
}

class FakeCartViewModel : CartViewModel() {
    override val _uiState = MutableStateFlow(CartUiState())
    override val uiState = _uiState.asStateFlow()
    override fun addToCart(product: Product) {}
    override fun removeFromCart(productId: Int) {}
    override fun checkout() {}
    override fun resetPaymentStatus() {}
    fun setState(newState: CartUiState) { _uiState.value = newState }
}

/**
 * ¡FAKE DE UBICACIÓN CORREGIDO!
 */
class FakeUbicacionViewModel : UbicacionViewModel(
    // ¡CAMBIO CLAVE! Le pasamos el contexto de Application
    // que nos provee el framework de pruebas de AndroidX.
    application = ApplicationProvider.getApplicationContext() as Application
) {
    // Sobrescribimos la propiedad 'open'
    override var direccion: String? by mutableStateOf("Dirección de Prueba Falsa")
        protected set

    // Sobrescribimos la función 'open'
    override fun actualizarUbicacion(lat: Double, lon: Double) {
        direccion = "Ubicación Falsa: $lat, $lon"
    }
}