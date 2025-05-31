package net.azarquiel.cuidaplusjpc.navigation

sealed class AppScreens(val route: String) {
    object SplashScreen: AppScreens(route = "SplashScreen")
    object HomeScreen: AppScreens(route = "HomeScreen")
    object RegisterUsuarioScreen: AppScreens(route = "RegisterUsuarioScreen")
    object RegisterCompletoScreen: AppScreens(route = "RegisterCompletoScreen")
    object LoginUsuarioScreen: AppScreens(route = "LoginUsuarioScreen")

}
