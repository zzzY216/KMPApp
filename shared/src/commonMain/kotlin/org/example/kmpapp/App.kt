package org.example.kmpapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
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
fun App(isDesktop: Boolean = false) {
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
    if (isDesktop) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            if (showBottomBar) {
                NavigationRail {
                    Spacer(Modifier.weight(1f))
                    bottomBar.forEach { route ->
                        val isSelected = currentDestination.hasRoute(route.route::class)
                        NavigationRailItem(
                            selected = isSelected,
                            onClick = { navigateToTopLevel(navController, route.route) },
                            icon = { Icon(route.icon, contentDescription = route.name) },
                            label = { Text(route.name) }
                        )
                    }
                    Spacer(Modifier.weight(2f))
                }
            }
            Box(modifier = Modifier.weight(1f)) {
                AppNavHost(navController)
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                AppNavHost(navController)
            }

            if (showBottomBar) {
                NavigationBar {
                    bottomBar.forEach { route ->
                        val isSelected = currentDestination.hasRoute(route.route::class)
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { navigateToTopLevel(navController, route.route) },
                            icon = { Icon(route.icon, contentDescription = route.name) },
                            label = { Text(route.name) }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun AppNavHost(
    navController: NavHostController
) {
    NavHost(
        startDestination = HomeScreenRoute,
        navController = navController,
        modifier = Modifier.fillMaxSize(1f)
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
}

private fun navigateToTopLevel(
    navController: NavHostController,
    route: Any
) {
    navController.navigate(route) {
        popUpTo(navController.graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}


data class TopLevelRoute<T>(val name: String, val route: T, val icon: ImageVector)