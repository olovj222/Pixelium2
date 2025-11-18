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
    // Sobrescribimos el '_uiState' protegido original para tener control total
    public override val _uiState = MutableStateFlow(LoginUiState())
    override val uiState = _uiState.asStateFlow()

    override fun login(user: String, pass: String) {}
    override fun register(user: String, pass: String, email: String) {}

    // Función pública para establecer el estado desde tests
    fun setState(newState: LoginUiState) {
        _uiState.value = newState
    }
}

class FakeMainViewModel : MainViewModel(
    repository = FakeRepository(),
    getCurrentUserId = { null }
) {
    // Aquí sí creamos uno nuevo porque MainViewModel no tiene _uiState protegido expuesto igual
    private val _fakeUiState = MutableStateFlow(MainUiState(isLoading = true))
    override val uiState = _fakeUiState.asStateFlow()

    fun setState(newState: MainUiState) { _fakeUiState.value = newState }
}

class FakeCartViewModel : CartViewModel() {
    // Sobrescribimos el _uiState protegido original
    public override val _uiState = MutableStateFlow(CartUiState())
    override val uiState = _uiState.asStateFlow()

    override fun addToCart(product: Product) {}
    override fun removeFromCart(productId: Int) {}
    override fun checkout() {}
    override fun resetPaymentStatus() {}

    fun setState(newState: CartUiState) { _uiState.value = newState }
}

/**
 * FAKE DE UBICACIÓN
 */
class FakeUbicacionViewModel : UbicacionViewModel(
    application = ApplicationProvider.getApplicationContext() as Application
) {
    // Sobrescribimos la propiedad 'open' manteniendo el 'protected set'
    override var direccion: String? by mutableStateOf("Dirección de Prueba Falsa")
        protected set

    override fun actualizarUbicacion(lat: Double, lon: Double) {
        direccion = "Ubicación Falsa: $lat, $lon"
    }
}