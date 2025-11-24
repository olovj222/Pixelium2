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
// ViewModel de Ubicación
import com.gameverse.viewmodel.UbicacionViewModel

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun ProfileScreen(
    mainViewModel: MainViewModel = viewModel(),
    ubicacionViewModel: UbicacionViewModel = viewModel(),
    onLogout: () -> Unit
) {
    val uiState by mainViewModel.uiState.collectAsState()
    val user = uiState.userProfile

    // --- ESTADOS PARA LA EDICIÓN ---
    var isEditing by remember { mutableStateOf(false) }

    // Usamos remember(user) para que si el usuario cambia (ej: después de guardar),
    // los campos se actualicen con la nueva info.
    var editUsername by remember(user) { mutableStateOf(user?.username ?: "") }
    var editEmail by remember(user) { mutableStateOf(user?.email ?: "") }
    var editFullName by remember(user) { mutableStateOf(user?.fullName ?: "") }
    var editPassword by remember(user) { mutableStateOf(user?.password ?: "") }

    var selectedImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    // --- LOGICA DE UBICACIÓN ---
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

    val lanzadorGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
    }

    // --- UI ---
    if (uiState.isLoading) {
        FullScreenLoader()
    } else if (user != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // 1. IMAGEN DE PERFIL (Siempre visible)
            AsyncImage(
                model = selectedImageUri ?: user.avatarUrl,
                contentDescription = "Avatar de ${user.username}",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Button(onClick = { lanzadorGaleria.launch("image/*") }) {
                Text("Cambiar foto")
            }

            // 2. SECCIÓN DE DATOS (Editable vs Visualización)
            if (isEditing) {
                // --- MODO EDICIÓN ---
                OutlinedTextField(
                    value = editFullName,
                    onValueChange = { editFullName = it },
                    label = { Text("Nombre Completo") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = editUsername,
                    onValueChange = { editUsername = it },
                    label = { Text("Usuario (@)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = editEmail,
                    onValueChange = { editEmail = it },
                    label = { Text("Correo") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = editPassword,
                    onValueChange = { editPassword = it },
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Botones Guardar / Cancelar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            // Llamamos a la función del ViewModel para guardar en BD
                            mainViewModel.updateUser(
                                currentUser = user,
                                newName = editUsername,
                                newEmail = editEmail,
                                newPass = editPassword,
                                newFullName = editFullName
                            )
                            isEditing = false // Volver a modo visualización
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Guardar")
                    }

                    OutlinedButton(
                        onClick = {
                            // Cancelar: reseteamos valores y salimos
                            editUsername = user.username
                            editEmail = user.email
                            editFullName = user.fullName
                            editPassword = user.password
                            isEditing = false
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }
                }

            } else {
                // --- MODO VISUALIZACIÓN (Solo Lectura) ---
                Text(
                    text = user.fullName,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "@${user.username}",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = user.email,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )

                // Botón para entrar en modo edición
                Button(
                    onClick = { isEditing = true },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Editar Perfil")
                }
            }

            // 3. SECCIÓN UBICACIÓN
            Divider(modifier = Modifier.padding(vertical = 16.dp))

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

            // 4. BOTÓN CERRAR SESIÓN
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Cerrar Sesión")
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            Text("No se pudo cargar el perfil del usuario.")
        }
    }
}