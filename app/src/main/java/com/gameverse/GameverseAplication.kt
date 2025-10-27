package com.gameverse

import android.app.Application
import com.gameverse.data.AppDatabase
import com.gameverse.data.repository.AppRepository

/**
 * Clase Application personalizada.
 * Se inicia una vez cuando se abre la app.
 * La usaremos como un "Singleton" central para proveer la base de datos
 * y el repositorio al resto de la aplicación.
 */
class GameverseApplication : Application() {

    // 'by lazy' significa que la base de datos se creará
    // la primera vez que se acceda a ella, y no antes.
    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    // El repositorio se crea usando los DAOs de la base de datos
    val repository: AppRepository by lazy {
        AppRepository(
            database.usuarioDAO(),
            database.productoDAO(),
            database.newsDAO()
        )
    }
}
