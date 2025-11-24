package com.gameverse.ui.screens.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gameverse.data.model.Product
import com.gameverse.viewmodel.AdminViewModel
import com.gameverse.viewmodel.factory.GameverseViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModelFactory: GameverseViewModelFactory,
    onLogout: () -> Unit
) {
    // Obtenemos el AdminViewModel usando la fábrica
    val viewModel: AdminViewModel = viewModel(factory = viewModelFactory)

    // Observamos la lista de productos locales directamente de la base de datos
    val products by viewModel.localProducts.collectAsState(initial = emptyList())

    // Estados para controlar la visibilidad y datos del diálogo de edición
    var showDialog by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administración") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar Sesión")
                    }
                }
            )
        }
    ) { padding ->
        // Lista de productos
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(products) { product ->
                AdminProductItem(product) {
                    // Al hacer clic en un item, guardamos el producto y abrimos el diálogo
                    selectedProduct = it
                    showDialog = true
                }
            }
        }

        // Lógica para mostrar el diálogo de edición
        if (showDialog && selectedProduct != null) {
            EditProductDialog(
                product = selectedProduct!!,
                onDismiss = { showDialog = false },
                onSave = { updatedProduct ->
                    viewModel.updateProduct(updatedProduct)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun AdminProductItem(product: Product, onClick: (Product) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(product) }, // Hacemos toda la tarjeta clickeable
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleMedium)
                Text("$${product.price}", style = MaterialTheme.typography.bodyMedium)
            }
            Icon(Icons.Default.Edit, contentDescription = "Editar")
        }
    }
}

@Composable
fun EditProductDialog(product: Product, onDismiss: () -> Unit, onSave: (Product) -> Unit) {
    // Usamos estados locales para los campos de texto del formulario
    var name by remember { mutableStateOf(TextFieldValue(product.name)) }
    var price by remember { mutableStateOf(TextFieldValue(product.price.toString())) }
    var description by remember { mutableStateOf(TextFieldValue(product.details)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Producto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") }
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Precio") }
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                // Convertimos el precio a Double de forma segura
                val newPrice = price.text.toDoubleOrNull() ?: product.price
                // Llamamos a onSave con una copia del producto actualizada
                onSave(product.copy(name = name.text, price = newPrice, details = description.text))
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}