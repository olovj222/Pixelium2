package com.gameverse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.gameverse.ui.navigation.AppNavigation
import com.gameverse.ui.theme.GameverseTheme
import com.gameverse.viewmodel.factory.GameverseViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Obtén la instancia de tu Application personalizada
        val application = application as GameverseApplication

        // 2. Obtén la instancia única del Repositorio desde la Application
        val repository = application.repository

        // 3. Crea la fábrica de ViewModels usando el Repositorio
        val viewModelFactory = GameverseViewModelFactory(repository)

        setContent {
            GameverseTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Mantiene la imagen de fondo global
                    Image(
                        painter = painterResource(id = R.drawable.home_background),
                        contentDescription = "Imagen de fondo global",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        colorFilter = ColorFilter.tint(Color.Black.copy(alpha = 0.6f), blendMode = BlendMode.Darken)
                    )
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color.Transparent // Surface transparente para ver el fondo
                    ) {
                        // 4. Pasa la fábrica de ViewModels a la Navegación
                        AppNavigation(viewModelFactory = viewModelFactory)
                    }
                }
            }
        }
    }
}

