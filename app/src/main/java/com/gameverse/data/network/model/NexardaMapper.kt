package com.gameverse.data.network.model

import com.gameverse.data.model.Product
import java.util.Locale
import java.text.NumberFormat

fun NexardaItem.toProduct(): Product {

    val usdPrice = game_info?.lowest_price ?: 0.0

    // Conversión a CLP (aprox)
    val clpRate = 950
    val clpPrice = usdPrice * clpRate

    return Product(
        id = game_info?.id ?: 0,
        name = game_info?.name ?: title ?: "Producto sin nombre",
        details = game_info?.short_desc ?: text ?: "Sin descripción",
        price = clpPrice,  // 👈 AHORA EN CLP CORRECTAMENTE
        imageUrl = image ?: ""
    )
}
