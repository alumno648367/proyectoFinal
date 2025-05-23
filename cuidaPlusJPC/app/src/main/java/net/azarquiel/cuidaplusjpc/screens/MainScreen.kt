package net.azarquiel.cuidaplusjpc.screens

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import net.azarquiel.cuidaplusjpc.navigation.BottomNavigationBar
import net.azarquiel.cuidaplusjpc.navigation.MainNavigation
import net.azarquiel.cuidaplusjpc.viewmodel.MainViewModel

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        MainNavigation(navController = navController, viewModel = viewModel, padding = padding)
    }
}
