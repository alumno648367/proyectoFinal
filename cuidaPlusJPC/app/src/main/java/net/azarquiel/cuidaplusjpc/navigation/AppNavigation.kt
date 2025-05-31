package net.azarquiel.cuidaplusjpc.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import net.azarquiel.cuidaplusjpc.screens.*
import net.azarquiel.cuidaplusjpc.viewmodel.MainViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    // Obtiene la entrada actual del backstack de navegación
    val backStackEntry by navController.currentBackStackEntryAsState()

    // Ruta actual visible en la pantalla
    val currentRoute = backStackEntry?.destination?.route

    // Obtiene el parámetro del grupo familiar si existe
    val grupoFamiliarId = backStackEntry?.arguments?.getString("grupoFamiliarId")

    // Rutas donde NO debe mostrarse la BottomNavigationBar
    val noBottomBarRoutes = listOf(
        AppScreens.SplashScreen.route,
        AppScreens.HomeScreen.route,
        AppScreens.RegisterUsuarioScreen.route,
        AppScreens.RegisterCompletoScreen.route,
        AppScreens.LoginUsuarioScreen.route
    )

    // Determina si se debe mostrar la barra inferior
    val showBottomBar = currentRoute != null && currentRoute !in noBottomBarRoutes

    // Contenedor principal con barra inferior condicional
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController, grupoFamiliarId)
            }
        }
    ) { innerPadding ->
        // Navegación principal entre pantallas
        NavHost(
            navController = navController,
            startDestination = AppScreens.SplashScreen.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Pantalla inicial de carga
            composable(AppScreens.SplashScreen.route) {
                SplashScreen(navController, viewModel)
            }

            // Pantalla principal tras splash
            composable(AppScreens.HomeScreen.route) {
                HomeScreen(navController, viewModel)
            }

            // Registro paso 1: datos personales
            composable(AppScreens.RegisterUsuarioScreen.route) {
                RegisterUsuarioScreen(navController, viewModel)
            }

            // Registro paso 2: grupo familiar
            composable(AppScreens.RegisterCompletoScreen.route) {
                RegisterCompletoScreen(navController, viewModel)
            }

            // Pantalla de login con email o Google
            composable(AppScreens.LoginUsuarioScreen.route) {
                LoginUsuarioScreen(navController, viewModel)
            }

            // Pantalla principal: información de usuario
            composable("inicio") {
                MiCuentaScreen(navController, viewModel)
            }

            // Perfil del usuario logueado
            composable("perfil") {
                PerfilScreen(navController, viewModel)
            }

            // Gestión del grupo familiar
            composable("grupo") {
                GrupoScreen(navController, viewModel)
            }

            // Lista de pacientes del grupo
            composable("pacientes") {
                PacientesScreen(navController, viewModel)
            }

            // Lista de citas médicas del grupo
            composable("citas/{grupoFamiliarId}") { backStackEntry ->
                val grupoId = backStackEntry.arguments?.getString("grupoFamiliarId") ?: return@composable
                CitasScreen(navController, viewModel.citaVM, viewModel, grupoId)
            }

            // Añadir nueva cita médica
            composable("addCita/{grupoFamiliarId}") { backStackEntry ->
                val grupoId = backStackEntry.arguments?.getString("grupoFamiliarId") ?: return@composable
                AddCitaScreen(navController, viewModel, grupoId)
            }

            // Añadir nuevo paciente al grupo
            composable("addPaciente") {
                AddPacienteScreen(navController, viewModel)
            }

            // Detalles de un paciente (acceso a todo su historial)
            composable("detailPaciente/{pacienteId}") { backStackEntry ->
                val pacienteId = backStackEntry.arguments?.getString("pacienteId") ?: return@composable
                PacienteDetailScreen(pacienteId, viewModel, navController)
            }

            // Gestión de enfermedades asociadas al paciente
            composable("gestionarEnfermedades/{pacienteId}") { backStackEntry ->
                val pacienteId = backStackEntry.arguments?.getString("pacienteId") ?: return@composable
                GestionarEnfermedadesScreen(pacienteId, navController, viewModel)
            }

            // Gestión de tratamientos de una enfermedad concreta
            composable("gestionarTratamientos/{enfermedadPacienteId}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("enfermedadPacienteId") ?: return@composable
                GestionarTratamientosScreen(id, viewModel, navController)
            }

            // Gestión de medicación de una enfermedad concreta
            composable("gestionarMedicacion/{enfermedadPacienteId}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("enfermedadPacienteId") ?: return@composable
                GestionarMedicacionScreen(id, navController, viewModel)
            }
        }
    }
}

// Definición centralizada de las rutas principales de navegación
sealed class AppScreens(val route: String) {
    object SplashScreen: AppScreens(route = "SplashScreen")
    object HomeScreen: AppScreens(route = "HomeScreen")
    object RegisterUsuarioScreen: AppScreens(route = "RegisterUsuarioScreen")
    object RegisterCompletoScreen: AppScreens(route = "RegisterCompletoScreen")
    object LoginUsuarioScreen: AppScreens(route = "LoginUsuarioScreen")
}
