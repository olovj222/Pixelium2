package com.gameverse.data.network

import com.gameverse.data.network.model.NexardaSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GameApiService {

    // La URL base será: https://www.nexarda.com/api/v3/
    // Aquí definimos el resto de la ruta: "search"

    @GET("search")
    suspend fun searchGames(
        @Query("type") type: String = "games", // Siempre buscaremos juegos
        @Query("q") query: String // El texto a buscar (ej. "Mario", "Zelda", o vacío)
    ): Response<NexardaSearchResponse>
}