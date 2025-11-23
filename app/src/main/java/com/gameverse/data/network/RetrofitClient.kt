package com.gameverse.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://www.nexarda.com/api/v3/"

    // Creamos la instancia de la API una sola vez (Singleton)
    val apiService: GameApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GameApiService::class.java)
    }
}