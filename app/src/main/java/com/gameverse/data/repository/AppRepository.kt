package com.gameverse.data.repository

import com.gameverse.data.dao.NewsDao
import com.gameverse.data.dao.ProductDao
import com.gameverse.data.dao.UserDao
import com.gameverse.data.model.NewsItem
import com.gameverse.data.model.Product
import com.gameverse.data.model.User
import kotlinx.coroutines.flow.Flow

/**
 * ¡GRAN CAMBIO!
 * El Repositorio ya no es un 'object'. Ahora es una 'class' que recibe
 * los DAOs de la base de datos como parámetros.
 * Es la única fuente de verdad para los ViewModels.
 */
class AppRepository(
    private val usuarioDAO: UserDao,
    private val productoDAO: ProductDao,
    private val newsDAO: NewsDao
) {

    // --- Funciones de Usuario ---

    /**
     * Intenta registrar un nuevo usuario.
     * Devuelve un 'Result' para que el ViewModel sepa si funcionó o falló
     * (ej. si el nombre de usuario ya existe).
     */
    suspend fun registerUser(user: User): Result<Unit> {
        return try {
            usuarioDAO.insertUser(user)
            Result.success(Unit)
        } catch (e: Exception) {
            // Esto probablemente sea una 'SQLiteConstraintException' si el username ya existe
            Result.failure(Exception("El nombre de usuario ya está en uso."))
        }
    }

    /**
     * Intenta iniciar sesión.
     * Devuelve el objeto 'User' completo si tiene éxito, o 'null' si las credenciales son incorrectas.
     */
    suspend fun login(user: String, pass: String): User? {
        return usuarioDAO.login(user, pass)
    }

    /**
     * Obtiene los datos de un usuario por su ID.
     */
    suspend fun getUserById(userId: Int): User? {
        return usuarioDAO.getUserById(userId)
    }

    // --- Funciones de Productos y Noticias ---

    /**
     * Obtiene la lista de productos como un Flow.
     * La UI se actualizará automáticamente si los datos cambian.
     */
    fun getProducts(): Flow<List<Product>> {
        return productoDAO.getProducts()
    }

    /**
     * Obtiene la lista de noticias como un Flow.
     */
    fun getNews(): Flow<List<NewsItem>> {
        return newsDAO.getNews()
    }

    /**
     * Obtiene los destacados (que son las mismas noticias).
     */
    fun getHomeHighlights(): Flow<List<NewsItem>> {
        return newsDAO.getNews()
    }
}

