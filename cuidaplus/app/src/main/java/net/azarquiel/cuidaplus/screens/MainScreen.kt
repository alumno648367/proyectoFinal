package net.azarquiel.cuidaplus.screens

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import net.azarquiel.cuidaplus.navigation.BottomNavigationBar
import net.azarquiel.cuidaplus.navigation.MainNavigation
import net.azarquiel.cuidaplus.viewmodel.MainViewModel

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        MainNavigation(navController = navController, viewModel = viewModel, padding = padding)
    }
}
