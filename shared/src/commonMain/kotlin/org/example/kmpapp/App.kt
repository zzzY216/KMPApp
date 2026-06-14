package org.example.kmpapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.BottomAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.example.kmpapp.home.HomeScreen
import org.example.kmpapp.home.HomeViewModel

@Composable
@Preview
fun App() {
    val navController = rememberNavController()
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        NavHost(
            startDestination = HomeScreenRoute,
            navController = navController
        ) {
            composable<HomeScreenRoute> {
                HomeScreen(
                    viewModel = HomeViewModel()
                )
            }
        }
        BottomAppBar(
            modifier = Modifier
        ) {  }
    }
}