package com.gameverse.viewmodel

import android.content.Context
import android.location.Geocoder
import java.io.IOException
import java.util.Locale





suspend fun getAddressFromCoordinates(context: Context, latitude: Double, longitude: Double): String {

    val geocoder = Geocoder(context, Locale.getDefault())

    return try {

        val addresses = geocoder.getFromLocation(latitude, longitude, 1)

        if (addresses != null && addresses.isNotEmpty()) {
            val address = addresses[0]
            // Extrae la ciudad (locality) y el país (countryName)
            val city = address.locality ?: "Ciudad no encontrada"
            val country = address.countryName ?: "País no encontrado"
            "$city, $country"
        } else {
            "Dirección no encontrada"
        }
    } catch (e: IOException) {
        // Este error ocurre si no hay conexión a internet o el servicio no está disponible.
        "Error al obtener la dirección: ${e.message}"
    }
}