package org.example.kmpapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import org.example.kmpapp.ui.home.HomeScreen
import org.example.kmpapp.ui.home.HomeViewModel
import org.example.kmpapp.ui.product.ProductDetailScreen
import org.example.kmpapp.ui.profile.ProfileScreen
import org.example.kmpapp.ui.shop.ShopScreen
import org.example.kmpapp.ui.shop.ShopViewModel
import org.example.kmpapp.ui.video.VideoScreen

@Composable
@Preview
fun App() {
    val navController = rememberNavController()
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry.value?.destination
    val showBottomBar = currentDestination?.hasRoute<HomeScreenRoute>() == true ||
            currentDestination?.hasRoute<ShopScreenRoute>() == true ||
            currentDestination?.hasRoute<ProfileScreenRoute>() == true ||
            currentDestination?.hasRoute<VideoScreenRoute>() == true
    val bottomBar = listOf(
        TopLevelRoute(
            name = "Home",
            route = HomeScreenRoute,
            icon = Icons.Default.Home
        ),
        TopLevelRoute(
            name = "video",
            route = VideoScreenRoute,
            icon = Icons.Default.Videocam
        ),
        TopLevelRoute(
            name = "Shop",
            route = ShopScreenRoute,
            icon = Icons.Default.ShoppingCart
        ),
        TopLevelRoute(
            name = "Profile",
            route = ProfileScreenRoute,
            icon = Icons.Default.Person
        )
    )
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        NavHost(
            startDestination = HomeScreenRoute,
            navController = navController,
            modifier = Modifier.weight(1f)
        ) {
            composable<HomeScreenRoute> {
                HomeScreen(
                    viewModel = HomeViewModel()
                )
            }
            composable<VideoScreenRoute> {
                VideoScreen()
            }
            composable<ShopScreenRoute> {
                ShopScreen(
                    viewModel = ShopViewModel(),
                    onProductClick = { product ->
                        navController.navigate(
                            ProductDetailRoute(
                                name = product.name,
                                price = product.price
                            )
                        )
                    }
                )
            }
            composable<ProductDetailRoute> { backStackEntry ->
                val args = backStackEntry.toRoute<ProductDetailRoute>()
                ProductDetailScreen(
                    name = args.name,
                    price = args.price,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
            composable<ProfileScreenRoute> {
                ProfileScreen()
            }
        }
        if (showBottomBar) {
            NavigationBar {
                bottomBar.forEach { topLevelRoute ->
                    val isSelected = currentDestination.hasRoute(topLevelRoute.route::class)
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            navController.navigate(topLevelRoute.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = topLevelRoute.icon,
                                contentDescription = topLevelRoute.name
                            )
                        },
                        label = {
                            Text(
                                text = topLevelRoute.name
                            )
                        },
                        alwaysShowLabel = false
                    )
                }
            }
        }
    }
}

data class TopLevelRoute<T>(val name: String, val route: T, val icon: ImageVector)