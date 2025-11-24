package com.gameverse.data.repository

import com.gameverse.data.dao.NewsDao
import com.gameverse.data.dao.ProductDao
import com.gameverse.data.dao.UserDao
import com.gameverse.data.model.NewsItem
import com.gameverse.data.model.Product
import com.gameverse.data.model.User
import com.gameverse.data.network.RetrofitClient
import com.gameverse.data.network.model.toProduct
import kotlinx.coroutines.flow.Flow

/**
 * ¡GRAN CAMBIO!
 * El Repositorio ya no es un 'object'. Ahora es una 'class' que recibe
 * los DAOs de la base de datos como parámetros.
 * Es la única fuente de verdad para los ViewModels.
 */
open class AppRepository(
    private val usuarioDAO: UserDao,
    private val productoDAO: ProductDao,
    private val newsDAO: NewsDao
) {

    // --- Funciones de Usuario ---

    /**
     * Intenta registrar un nuevo usuario.
     * Devuelve un 'Result' para que el ViewModel sepa si funcionó o falló
     * (si el nombre de usuario ya existe).
     */
    suspend fun syncProductsFromApi() {
        val response = RetrofitClient.apiService.searchGames(query = "")

        if (response.isSuccessful) {
            val body = response.body()
            val apiItems = body?.results?.items ?: emptyList()
            val products = apiItems.map { it.toProduct() }

            // Guarda los productos en Room
            productoDAO.insertAll(products)
        }
    }

    suspend fun countProducts(): Int = productoDAO.count()


    open suspend fun registerUser(user: User): Result<Unit> {
        return try {
            usuarioDAO.insertUser(user)
            Result.success(Unit)
        } catch (e: Exception) {
            // Manda un 'SQLiteConstraintException' si el username ya existe
            Result.failure(Exception("El nombre de usuario ya está en uso."))
        }
    }

    /**
     * Intenta iniciar sesión.
     * Devuelve el objeto 'User' completo si tiene éxito, o 'null' si las credenciales son incorrectas.
     */
    open suspend fun login(user: String, pass: String): User? {
        return usuarioDAO.login(user, pass)
    }

    /**
     * Obtiene los datos de un usuario por su ID.
     */
    open suspend fun getUserById(userId: Int): User? {
        return usuarioDAO.getUserById(userId)
    }

    // --- Funciones de Productos y Noticias

    /**
     * Obtiene la lista de productos como un Flow.
     * La UI se actualizará automáticamente si los datos cambian.
     */
    open fun getProducts(): Flow<List<Product>> {
        return productoDAO.getProducts()
    }

    /**
     * Obtiene la lista de noticias como un Flow.
     */
    open fun getNews(): Flow<List<NewsItem>> {
        return newsDAO.getNews()
    }

    /**
     * Obtiene los destacados (que son las mismas noticias).
     */
    open fun getHomeHighlights(): Flow<List<NewsItem>> {
        return newsDAO.getNews()
    }

    suspend fun updateProduct(product: Product) {
        // Necesitas asegurarte de tener @Update en tu ProductoDAO
        // Si no lo tienes, agrégalo en el paso siguiente.
        productoDAO.updateProduct(product)
    }

    fun getLocalProducts(): Flow<List<Product>> {
        return productoDAO.getProducts()
    }

    // 1. Función para escuchar cambios (conecta con el DAO nuevo)
    fun getUserFlow(userId: Int): Flow<User?> = usuarioDAO.getUserFlow(userId)

    // 2. Función para guardar la edición
    suspend fun updateUser(user: User) = usuarioDAO.updateUser(user)

    // Necesitamos buscar solo por nombre para validar existencia
    suspend fun getUserByUsername(username: String): User? {
        return usuarioDAO.getUserByUsername(username)
    }
}

