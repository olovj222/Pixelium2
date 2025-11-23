package com.gameverse.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * NUEVA ENTIDAD PARA USUARIOS.
 * Almacena tanto los datos de login como los de perfil.
 * Reemplaza al antiguo UserProfile.
 */
@Entity(
    tableName = "users",
    // Asegura que no haya dos usuarios con el mismo nombre de usuario
    indices = [Index(value = ["username"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val password: String,
    val fullName: String,
    val email: String,
    val memberSince: String,
    val avatarUrl: String
)

/**
 * PRODUCTOS - AHORA ES UNA ENTIDAD
 */
@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val details: String,
    val price: Double,
    val imageUrl: String
)

/**
 * NOTICIAS - AHORA ES UNA ENTIDAD
 */
@Entity(tableName = "news")
data class NewsItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val summary: String,
    val imageUrl: String
)


// --- Modelos que NO se guardan en la base de datos ---

/**
 * CartProduct no es una tabla en la base de datos.
 * Es un modelo que solo existe en la memoria mientras la app se ejecuta.
 */
data class CartProduct(
    val id: Int,
    val name: String,
    val price: Double,
    val imageUrl: String
)

