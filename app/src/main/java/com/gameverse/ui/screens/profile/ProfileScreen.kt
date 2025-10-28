package com.gameverse.ui.screens.profile

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.gameverse.ui.components.FullScreenLoader
import com.gameverse.viewmodel.MainViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
// ViewModel de Ubicación (asumiendo que lo tienes)
import com.gameverse.viewmodel.UbicacionViewModel

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun ProfileScreen(
    mainViewModel: MainViewModel = viewModel(),
    ubicacionViewModel: UbicacionViewModel = viewModel(),
    // 1. AÑADIMOS EL PARÁMETRO PARA EL LOGOUT
    onLogout: () -> Unit
) {
    val uiState by mainViewModel.uiState.collectAsState()
    // ¡CAMBIO IMPORTANTE! Ahora usamos el 'User' de la DB
    val user = uiState.userProfile

    // Estado local para la URI de la imagen seleccionada
    var selectedImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    // --- Lógica de Permisos y Ubicación (sin cambios) ---
    val contexto = LocalContext.current
    val permisoUbicacion = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val tienePermiso = permisoUbicacion.status is PermissionStatus.Granted

    if (tienePermiso) {
        DisposableEffect(Unit) {
            val proveedorUbicacion = LocationServices.getFusedLocationProviderClient(contexto)
            val callbackUbicacion = object : LocationCallback() {
                override fun onLocationResult(resultado: LocationResult) {
                    resultado.lastLocation?.let {
                        ubicacionViewModel.actualizarUbicacion(it.latitude, it.longitude)
                    }
                }
            }
            val solicitudUbicacion = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 10000L).build()

            proveedorUbicacion.requestLocationUpdates(solicitudUbicacion, callbackUbicacion, null)

            onDispose {
                proveedorUbicacion.removeLocationUpdates(callbackUbicacion)
            }
        }
    }

    // --- Lógica del Selector de Imágenes (sin cambios) ---
    val lanzadorGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
        // Aquí podrías guardar la URI en el ViewModel si quisieras persistirla,
        // o incluso subirla a un servidor y guardar la URL en la base de datos.
    }

    // --- UI Principal ---
    if (uiState.isLoading) {
        FullScreenLoader()
    } else if (user != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 16.dp) // Añadido padding vertical
                .verticalScroll(rememberScrollState()), // Permite scroll
            horizontalAlignment = Alignment.CenterHorizontally,
            // Cambiado Arrangement para espaciar los elementos
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Imagen (usa la URI seleccionada o la del perfil)
            AsyncImage(
                model = selectedImageUri ?: user.avatarUrl,
                contentDescription = "Avatar de ${user.username}",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            // Botón Cambiar Foto
            Button(onClick = { lanzadorGaleria.launch("image/*") }) {
                Text("Cambiar foto")
            }

            // Nombre Completo
            Text(
                text = user.fullName,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Nombre de Usuario
            Text(
                text = "@${user.username}",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant // Usar color del tema
            )

            // 2. AÑADIMOS EL CORREO ELECTRÓNICO
            Text(
                text = user.email,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f) // Un poco más tenue
            )

            // --- Sección de Ubicación ---
            Divider(modifier = Modifier.padding(vertical = 16.dp)) // Separador

            if (!tienePermiso) {
                Button(onClick = { permisoUbicacion.launchPermissionRequest() }) {
                    Text("Activar ubicación")
                }
            } else {
                Text(
                    text = "¡Mantén tu ubicación actualizada!",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ubicación actual:",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = ubicacionViewModel.direccion ?: "Obteniendo...",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // 3. AÑADIMOS EL BOTÓN DE CERRAR SESIÓN
            Spacer(modifier = Modifier.weight(1f)) // Empuja el botón hacia abajo
            Button(
                onClick = onLogout, // Llama a la función lambda
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error // Color rojo para logout
                )
            ) {
                Text("Cerrar Sesión")
            }
        }
    } else {
        // Muestra un mensaje si el usuario no se pudo cargar
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            Text("No se pudo cargar el perfil del usuario.")
        }
    }
}