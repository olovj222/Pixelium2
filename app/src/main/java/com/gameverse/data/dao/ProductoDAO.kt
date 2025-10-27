package com.gameverse.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gameverse.data.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    /**
     * Inserta una lista de productos.
     * Si un producto con el mismo ID ya existe, lo reemplaza.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<Product>)

    /**
     * Obtiene todos los productos de la tabla.
     * Devuelve un 'Flow', lo que significa que la UI se actualizará automáticamente
     * si los datos de los productos cambian.
     */
    @Query("SELECT * FROM products")
    fun getProducts(): Flow<List<Product>>

    /**
     * Cuenta cuántos productos hay en la base de datos.
     * Útil para saber si necesitamos cargar los productos iniciales.
     */
    @Query("SELECT COUNT(*) FROM products")
    suspend fun count(): Int
}
