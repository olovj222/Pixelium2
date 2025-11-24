package com.gameverse.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gameverse.data.model.User

@Dao
interface UserDao {

    /**
     * Inserta un nuevo usuario en la base de datos.
     * Si el usuario (basado en el 'username' único) ya existe, aborta la transacción.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User)

    /**
     * Busca un usuario por su 'username'.
     * Útil para la pantalla de registro, para comprobar si el nombre de usuario ya está en uso.
     */
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    /**
     * Busca un usuario por 'username' y 'password'.
     * Esta es la función que usará nuestro 'login'.
     */
    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    suspend fun login(username: String, password: String): User?

    /**
     * Busca un usuario por su ID.
     * (Será útil en el futuro si guardamos la sesión del usuario por su ID).
     */
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Int): User?

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserFlow(userId: Int): kotlinx.coroutines.flow.Flow<User?>
}
