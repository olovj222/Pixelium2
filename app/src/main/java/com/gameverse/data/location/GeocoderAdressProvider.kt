package com.gameverse.data.location

import android.content.Context
import android.location.Geocoder
import com.gameverse.viewmodel.AddressProvider
import java.util.Locale

class GeocoderAddressProvider(private val context: Context) : AddressProvider {

    @Suppress("DEPRECATION")
    override suspend fun getAddress(lat: Double, lon: Double): String {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lon, 1)

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]

                // Lista para guardar solo las partes que nos interesan
                val partesDireccion = mutableListOf<String>()

                // 1. Calle y Número (Ej: Domingo Tocornal 1626)
                val calle = address.thoroughfare
                val numero = address.subThoroughfare

                if (!calle.isNullOrEmpty()) {
                    if (!numero.isNullOrEmpty()) {
                        partesDireccion.add("$calle $numero")
                    } else {
                        partesDireccion.add(calle)
                    }
                }

                // 2. Comuna / Localidad (Ej: Puente Alto)
                if (!address.locality.isNullOrEmpty()) {
                    partesDireccion.add(address.locality)
                } else if (!address.subAdminArea.isNullOrEmpty()) {
                    // A veces la comuna viene aquí si locality es null
                    partesDireccion.add(address.subAdminArea)
                }

                // 3. País (Ej: Chile)
                if (!address.countryName.isNullOrEmpty()) {
                    partesDireccion.add(address.countryName)
                }

                // Unimos las partes con una coma y espacio
                if (partesDireccion.isNotEmpty()) {
                    partesDireccion.joinToString(", ")
                } else {
                    // Si no se encontró info detallada, mostramos coords
                    "Ubicación ($lat, $lon)"
                }

            } else {
                "Dirección no encontrada"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Error al obtener dirección"
        }
    }
}