package org.example.kmpapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.example.kmpapp.ui.home.HomeScreen
import org.example.kmpapp.ui.home.HomeViewModel
import org.example.kmpapp.ui.shop.ShopScreen
import org.example.kmpapp.ui.shop.ShopViewModel

@Composable
@Preview
fun App() {
    val navController = rememberNavController()
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry.value?.destination
    val showBottomBar = currentDestination?.hasRoute<HomeScreenRoute>() == true ||
            currentDestination?.hasRoute<ShopScreenRoute>() == true
    val bottomBar = listOf(
        TopLevelRoute(
            name = "Home",
            route = HomeScreenRoute,
            icon = 1
        ),
        TopLevelRoute(
            name = "shop",
            route = ShopScreenRoute,
            icon = 2
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
            composable<ShopScreenRoute> {
                ShopScreen(
                    viewModel = ShopViewModel()
                )
            }
        }
        if (showBottomBar) {
            NavigationBar{
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
                        icon = {},
                        label = {},
                        alwaysShowLabel = false
                    )
                }
            }
        }
    }
}

data class TopLevelRoute<T>(val name: String, val route: T, val icon: Int)