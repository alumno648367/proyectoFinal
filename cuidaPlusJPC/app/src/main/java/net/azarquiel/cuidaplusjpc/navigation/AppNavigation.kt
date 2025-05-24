package net.azarquiel.cuidaplusjpc.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import net.azarquiel.cuidaplusjpc.screens.HomeScreen
import net.azarquiel.cuidaplusjpc.screens.LoginUsuarioScreen
import net.azarquiel.cuidaplusjpc.screens.MainScreen
import net.azarquiel.cuidaplusjpc.screens.MiCuentaScreen
import net.azarquiel.cuidaplusjpc.screens.RegisterCompletoScreen
import net.azarquiel.cuidaplusjpc.screens.RegisterUsuarioScreen
import net.azarquiel.cuidaplusjpc.screens.SplashScreen
import net.azarquiel.cuidaplusjpc.viewmodel.MainViewModel


@Composable
fun AppNavigation(viewModel: MainViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController,
        startDestination = AppScreens.SplashScreen.route){
        composable(AppScreens.SplashScreen.route){
            SplashScreen(navController, viewModel)
        }
        composable(AppScreens.HomeScreen.route){
            HomeScreen(navController, viewModel)
        }
        composable(AppScreens.RegisterUsuarioScreen.route){
            RegisterUsuarioScreen(navController, viewModel)
        }
        composable(AppScreens.RegisterCompletoScreen.route){
            RegisterCompletoScreen(navController, viewModel)
        }
        composable(AppScreens.LoginUsuarioScreen.route){
            LoginUsuarioScreen(navController, viewModel)
        }
        composable(AppScreens.MiCuentaScreen.route){
            MiCuentaScreen(navController, viewModel)
        }
        composable(AppScreens.MainScreen.route) {
            MainScreen(viewModel)
        }
    }
}
sealed class AppScreens(val route: String) {
    object SplashScreen: AppScreens(route = "SplashScreen")
    object HomeScreen: AppScreens(route = "HomeScreen")
    object RegisterUsuarioScreen: AppScreens(route = "RegisterUsuarioScreen")
    object RegisterCompletoScreen: AppScreens(route = "RegisterCompletoScreen")
    object LoginUsuarioScreen: AppScreens(route = "LoginUsuarioScreen")
    object MiCuentaScreen: AppScreens(route = "MiCuentaScreen")
    object MainScreen: AppScreens(route = "MainScreen")

}
