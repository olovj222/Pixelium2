package com.gameverse.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gameverse.data.location.GeocoderAddressProvider // Asegúrate de importar tu nueva clase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Definición de la interfaz (puede ir en su propio archivo si prefieres)
interface AddressProvider {
    suspend fun getAddress(lat: Double, lon: Double): String
}

// Usamos @JvmOverloads para que Android pueda crearlo sin parámetros extra
open class UbicacionViewModel @JvmOverloads constructor(
    application: Application,
    // Por defecto usa la implementación REAL con Geocoder
    private val addressProvider: AddressProvider = GeocoderAddressProvider(application),
    // Por defecto usa el Dispatcher IO para no bloquear la UI
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AndroidViewModel(application) {

    var latitud by mutableStateOf<Double?>(null)
        private set
    var longitud by mutableStateOf<Double?>(null)
        private set

    open var direccion by mutableStateOf<String?>(null)
        protected set

    open fun actualizarUbicacion(lat: Double, lon: Double) {
        latitud = lat
        longitud = lon

        viewModelScope.launch {
            direccion = "Obteniendo dirección..."

            // Cambiamos al hilo IO para hacer la operación de red/disco del Geocoder
            val addressText = withContext(ioDispatcher) {
                addressProvider.getAddress(lat, lon)
            }

            direccion = addressText
        }
    }
}