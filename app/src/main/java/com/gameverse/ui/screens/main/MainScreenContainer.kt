package com.gameverse.ui.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gameverse.ui.navigation.BottomNavItems
import com.gameverse.ui.screens.home.HomeScreen
import com.gameverse.ui.screens.news.NewsScreen
import com.gameverse.ui.screens.products.ProductsScreen
import com.gameverse.ui.screens.profile.ProfileScreen
// Importa el UbicacionViewModel si ProfileScreen lo usa directamente
import com.gameverse.viewmodel.UbicacionViewModel // Asegúrate de tener este ViewModel
import com.gameverse.viewmodel.CartViewModel
import com.gameverse.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContainer(
    mainViewModel: MainViewModel,
    cartViewModel: CartViewModel,
    onNavigateToCart: () -> Unit,
    // Recibe la función de logout desde AppNavigation
    onLogout: () -> Unit
) {
    val bottomNavController = rememberNavController()
    val uiState by mainViewModel.uiState.collectAsState()
    val username = uiState.userProfile?.username
    val titleText = if (username != null) "¡Hola, $username!" else "Gameverse"

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = titleText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.3f),
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    actionIconContentColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    IconButton(onClick = onNavigateToCart) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito de compras")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.Black.copy(alpha = 0.5f)
            ) {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val items = listOf(
                    BottomNavItems.Home,
                    BottomNavItems.Products,
                    BottomNavItems.News,
                    BottomNavItems.Profile,
                )
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            bottomNavController.navigate(screen.route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // NavHost interno para las pantallas de la barra inferior
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavItems.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItems.Home.route) {
                HomeScreen(mainViewModel = mainViewModel,
                    onNavigateToProducts = {
                    bottomNavController.navigate(BottomNavItems.Products.route) {
                        popUpTo(bottomNavController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                })
            }
            composable(BottomNavItems.Products.route) { ProductsScreen(mainViewModel, cartViewModel) }
            composable(BottomNavItems.News.route) { NewsScreen(mainViewModel) }

            // acá pasamos la función 'onLogout' a la ProfileScreen
            composable(BottomNavItems.Profile.route) {

                val ubicacionViewModel: UbicacionViewModel = viewModel()
                ProfileScreen(
                    mainViewModel = mainViewModel,
                    ubicacionViewModel = ubicacionViewModel,
                    onLogout = onLogout
                )
            }
        }
    }
}

