package com.gameverse.ui.screens.cart

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf // Necesario para mutableStateOf
import androidx.compose.runtime.remember // Necesario para remember
import androidx.compose.runtime.setValue // Necesario para 'by mutableStateOf'
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import java.text.NumberFormat
import java.util.Locale
import com.gameverse.data.model.CartProduct
import com.gameverse.ui.components.NeonButton
import com.gameverse.viewmodel.CartViewModel

@Composable
fun CartScreen(
    cartViewModel: CartViewModel = viewModel(),
    onGoBack: () -> Unit
) {
    val uiState by cartViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val clpFormatter = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }

    //acá se recuerda el estado despues del chekout (simular pago)
    var showPostCheckoutMessage by remember { mutableStateOf(false) }


    LaunchedEffect(uiState.paymentSuccess) {
        if (uiState.paymentSuccess) {
            Toast.makeText(context, "¡Gracias por tu compra!", Toast.LENGTH_LONG).show()
            showPostCheckoutMessage = true
            cartViewModel.resetPaymentStatus()
        }
    }


    if (uiState.cartItems.isEmpty()) {
        // Carrito Vacío
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                if (showPostCheckoutMessage) {
                    // Si el carrito está vacío DESPUÉS de un pago
                    Text(
                        "¡Compra realizada con éxito!",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    NeonButton(
                        text = "Seguir Comprando",
                        onClick = {
                            showPostCheckoutMessage = false // Reseteamos el estado local al salir
                            onGoBack()
                        }
                    )
                } else {
                    // Si el carrito está vacío (y no hubo pago reciente)
                    Text(
                        "Tu carrito está vacío",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 20.sp
                    )
                }
            }
        }
    } else {
        // Carrito con Items (sin cambios aquí)

        LaunchedEffect(uiState.cartItems.isNotEmpty()) {
            if(uiState.cartItems.isNotEmpty()) {
                showPostCheckoutMessage = false
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(top = 60.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.cartItems, key = { it.id }) { product ->
                    CartItem(
                        product = product,
                        clpFormatter = clpFormatter,
                        onRemove = { cartViewModel.removeFromCart(product.id) }
                    )
                }
            }

            // Sección del Total y Botón de Pago (sin cambios)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 60.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Total:",
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        clpFormatter.format(uiState.total),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                NeonButton(
                    text = "Simular Pago",
                    onClick = {
                        cartViewModel.checkout()
                    }
                )
            }
        }
    }
}


@Composable
private fun CartItem(
    product: CartProduct,
    clpFormatter: NumberFormat,
    onRemove: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(product.name, fontWeight = FontWeight.Bold, color = Color(0xFFFEFCF9))
                Text(
                    clpFormatter.format(product.price),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }
            IconButton(onClick = onRemove) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_delete),
                    contentDescription = "Eliminar del carrito",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

