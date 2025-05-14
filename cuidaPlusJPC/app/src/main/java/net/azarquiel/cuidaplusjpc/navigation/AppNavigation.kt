package net.azarquiel.cuidaplusjpc.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import net.azarquiel.cuidaplusjpc.screens.RegisterUsuarioScreen
import net.azarquiel.cuidaplusjpc.viewmodel.MainViewModel


@Composable
fun AppNavigation(viewModel: MainViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController,
        startDestination = AppScreens.RegisterUsuarioScreen.route){
        composable(AppScreens.RegisterUsuarioScreen.route){
            RegisterUsuarioScreen(navController, viewModel)
        }
    }
}
sealed class AppScreens(val route: String) {
    object RegisterUsuarioScreen: AppScreens(route = "RegisterUsuarioScreen")
}
