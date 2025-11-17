package com.gameverse.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope // ¡Importante!
import kotlinx.coroutines.Dispatchers // ¡Importante!
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class UbicacionViewModel(application: Application) : AndroidViewModel(application) {

    var latitud by mutableStateOf<Double?>(null)
        private set
    var longitud by mutableStateOf<Double?>(null)
        private set

    // Agregamos un nuevo estado para guardar la dirección
    open var direccion by mutableStateOf<String?>(null)
        protected set

    open fun actualizarUbicacion(lat: Double, lon: Double) {
        latitud = lat
        longitud = lon


        viewModelScope.launch {
            // Ponemos un valor temporal mientras se carga
            direccion = "Buscando dirección..."


            val addressText = withContext(Dispatchers.IO) {

                getAddressFromCoordinates(getApplication(), lat, lon)
            }


            direccion = addressText
            println("La dirección es: $addressText")
        }
    }
}