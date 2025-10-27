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
            Product(id = 1, name = "Controlador Inalámbrico Élite", details = "Controlador de alto rendimiento para gaming competitivo.", price = 99000.00, imageUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwxMTc3M3wwfDF8c2VhcmNofDEwfHxnYW1pbmclMjBjb250cm9sbGVyfGVufDB8fHx8MTY3OTM2ODU2Nw&ixlib=rb-4.0.3&q=80&w=600"),
            Product(id = 2, name = "Auriculares Gaming 7.1", details = "Sonido envolvente para una inmersión total.", price = 79000.00, imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwxMTc3M3wwfDF8c2VhcmNofDF8fGdhbWluZyUyMGhlYWRzZXR8ZW58MHx8fHwxNjc5MzY4NjE0&ixlib=rb-4.0.3&q=80&w=600"),
            Product(id = 3, name = "Teclado Mecánico RGB", details = "Respuesta táctil y retroiluminación personalizable.", price = 120000.00, imageUrl = "https://images.unsplash.com/photo-1563297007-0686b7003af7?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwxMTc3M3wwfDF8c2VhcmNofDEyfHxtZWNoYW5pY2FsJTIwa2V5Ym9hcmR8ZW58MHx8fHwxNjc5MzY4NjQz&ixlib=rb-4.0.3&q=80&w=600"),
            Product(id = 4, name = "Mouse Gamer Programable", details = "Sensor de alta precisión y botones configurables.", price = 45000.00, imageUrl = "https://images.unsplash.com/photo-1629429408209-1f912261dbd9?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwxMTc3M3wwfDF8c2VhcmNofDE1fHxnYW1pbmclMjBtb3VzZXxlbnwwfHx8fDE2NzkzYzgxNDQ&ixlib=rb-4.0.3&q=80&w=600"),
            Product(id = 5, name = "Silla Gamer Ergonómica", details = "Soporte lumbar y diseño de carreras.", price = 189000.00, imageUrl = "https://images.unsplash.com/photo-1586023492125-27b2c045efd7?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwxMTc3M3wwfDF8c2VhcmNofDF8fGdhbWluZyUyMGNoYWlyfGVufDB8fHx8MTY3OTM2ODcwMA&ixlib=rb-4.0.3&q=80&w=600"),
            Product(id = 7, name = "Webcam Pro 4K", details = "Videollamadas y streaming en ultra alta definición.", price = 130000.00, imageUrl = "https://images.unsplash.com/photo-1616348496622-13361a2936a7?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwxMTc3M3wwfDF8c2VhcmNofDI0fHxwYyUyMHdlYmNhbXxlbnwwfHx8fDE2NzkzYzgxMTM&ixlib=rb-4.0.3&q=80&w=600")
        )
        dao.insertAll(products)
    }

    suspend fun prePopulateNews(dao: NewsDao) {
        val news = listOf(
            NewsItem(id = 1, title = "Elden Ring: Shadow of the Erdtree", summary = "La esperada expansión de Elden Ring recibe sus primeras imágenes...", imageUrl = "https://images.unsplash.com/photo-1678848415526-9a64f5d637d7?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwxMTc3M3wwfDF8c2VhcmNofDF8fGVsZGVuJTIwcmluZ3xlbnwwfHx8fDE2NzkzNjg3Mjg&ixlib=rb-4.0.3&q=80&w=400"),
            NewsItem(id = 2, title = "Star Wars Outlaws Revelado", summary = "Ubisoft sorprende con un nuevo juego de mundo abierto de Star Wars...", imageUrl = "https://images.unsplash.com/photo-1608346128025-1896b97c6596?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwxMTc3M3wwfDF8c2VhcmNofDd8fHN0YXIlMjB3YXJzfGVufDB8fHx8MTY3OTM2ODc1Nw&ixlib=rb-4.0.3&q=80&w=400"),
            NewsItem(id = 3, title = "Resumen del Nintendo Direct", summary = "Anunciado un nuevo Zelda 2D y el regreso de un clásico de culto...", imageUrl = "https://images.unsplash.com/photo-1554106132-15f1681710d0?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwxMTc3M3wwfDF8c2VhcmNofDEwfHxuaW50ZW5kb3xlbnwwfHx8fDE2NzkzNjg3ODM&ixlib=rb-4.0.3&q=80&w=400")
        )
        dao.insertAll(news)
    }
}
