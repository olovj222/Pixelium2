package com.gameverse

import android.app.Application
import com.gameverse.data.AppDatabase
import com.gameverse.data.repository.AppRepository


class GameverseApplication : Application() {

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
