package com.gameverse.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gameverse.data.dao.NewsDao
import com.gameverse.data.dao.ProductDao
import com.gameverse.data.dao.UserDao
import com.gameverse.data.model.NewsItem
import com.gameverse.data.model.Product
import com.gameverse.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [User::class, Product::class, NewsItem::class], // 1. Lista de todas las tablas
    version = 1, // 2. Si cambias la estructura de las tablas, debes subir este número
    exportSchema = false // No exportar el esquema
)
abstract class AppDatabase : RoomDatabase() {

    // 3. Funciones abstractas para cada DAO (¡con tus nombres!)
    abstract fun usuarioDAO(): UserDao
    abstract fun productoDAO(): ProductDao
    abstract fun newsDAO(): NewsDao

    // 4. Companion object para crear el Singleton de la base de datos
    companion object {
        // @Volatile asegura que el valor de INSTANCE esté siempre actualizado
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Si INSTANCE no es nulo, lo retorna.
            // Si es nulo, crea la base de datos en un bloque 'synchronized'.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gameverse_database.db" // 5. ¡Este es el nombre de tu archivo .db!
                )
                    .addCallback(DatabaseCallback(context)) // 6. Añade el callback para precargar datos
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

/**
 * Callback para precargar la base de datos con datos iniciales
 * la primera vez que se crea.
 */
private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        // Usamos una corutina para insertar los datos en un hilo de fondo
        CoroutineScope(Dispatchers.IO).launch {
            val database = AppDatabase.getDatabase(context)
            prePopulateProducts(database.productoDAO())
            prePopulateNews(database.newsDAO())
        }
    }

    suspend fun prePopulateProducts(dao: ProductDao) {
        val products = listOf(
            Product(id = 1, name = "Controlador Inalámbrico Élite", details = "Controlador de alto rendimiento para gaming competitivo.", price = 99000.00, imageUrl = "https://i.postimg.cc/9frjQb6Y/OIP-1.webp"),
            Product(id = 2, name = "Auriculares Gaming 7.1", details = "Sonido envolvente para una inmersión total.", price = 79000.00, imageUrl = "https://i.postimg.cc/6QyNpckr/81Sn-Vtk-Isy-L-AC-SL1500.jpg"),
            Product(id = 3, name = "Teclado Mecánico GK 100", details = "Respuesta táctil y retroiluminación personalizable.", price = 120000.00, imageUrl = "https://i.postimg.cc/yNkz8jMT/Teclado-Mecanico-Hp-Gaming-Gk100.png"),
            Product(id = 4, name = "Mouse Gamer Programable G502", details = "Sensor de alta precisión y botones configurables.", price = 45000.00, imageUrl = "https://i.postimg.cc/6QyNpckC/g502-heroe.jpg"),
            Product(id = 5, name = "Silla Gamer Ergonómica", details = "Soporte lumbar y diseño de carreras.", price = 189000.00, imageUrl = "https://i.postimg.cc/7LfyZ9FV/Silla-Gamer-Copia-Copia-Copia-Copia-Copia-Copia-Homen-133541884.webp"),
            Product(id = 7, name = "Webcam Pro 4K", details = "Videollamadas y streaming en ultra alta definición.", price = 130000.00, imageUrl = "https://i.postimg.cc/bvsjwg7b/413Nn-Hz-V0CL.jpg")
        )
        dao.insertAll(products)
    }

    suspend fun prePopulateNews(dao: NewsDao) {
        val news = listOf(
            NewsItem(id = 1, title = "Elden Ring: Shadow of the Erdtree", summary = "La esperada expansión de Elden Ring recibe sus primeras imágenes, los jugadores esperan con ansias la proxima fecha de lanzamiento del titulo...", imageUrl = "https://i.postimg.cc/d0LKV6b8/1719357444-elden-ring-shadow-of-the-erdtree-dlc-xbox-series-xs-0.webp"),
            NewsItem(id = 2, title = "Star Wars Outlaws Revelado", summary = "Ubisoft sorprende con un nuevo juego de mundo abierto de Star Wars, que mantiene espectante al mundo gaming, que sorpresas nos deparara esta entrega?...", imageUrl = "https://i.postimg.cc/BvXGnprc/1724372809-star-wars-outlaws-xbox-series-xs-pre-orden-0.webp"),
            NewsItem(id = 3, title = "Resumen del Nintendo Direct", summary = "Anunciado un nuevo Zelda 2D y el regreso de un clásico de culto, se hara realidad la integracion de Mario Informatico en Smash bros?, quedate atento a las proximas noticias!...", imageUrl = "https://i.postimg.cc/Dz8Kw69b/500-333.jpg")
        )
        dao.insertAll(news)
    }
}
