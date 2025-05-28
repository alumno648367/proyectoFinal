package net.azarquiel.cuidaplus.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import net.azarquiel.cuidaplus.viewmodel.MainViewModel


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
    object MainScreen: AppScreens(route = "MainScreen")

}
