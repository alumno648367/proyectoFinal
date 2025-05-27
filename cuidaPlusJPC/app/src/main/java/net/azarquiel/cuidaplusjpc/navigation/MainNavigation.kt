package net.azarquiel.cuidaplusjpc.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import net.azarquiel.cuidaplusjpc.screens.*
import net.azarquiel.cuidaplusjpc.viewmodel.MainViewModel

@Composable
fun MainNavigation(
    navController: NavHostController,
    viewModel: MainViewModel,
    padding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = "inicio"
    ) {
        composable("inicio") {
            MiCuentaScreen(navController, viewModel)
        }
        composable("perfil") {
            PerfilScreen(navController, viewModel)
        }
        composable("grupo") {
            GrupoScreen(navController, viewModel)
        }
        composable("citas") {
            CitasScreen(navController, viewModel)
        }
        composable("pacientes") {
            PacientesScreen(navController, viewModel, padding)
        }
        composable("addPaciente") {
            AddPacienteScreen(navController, viewModel, padding)
        }
        composable("detailPaciente/{pacienteId}") { backStackEntry ->
            val pacienteId = backStackEntry.arguments?.getString("pacienteId") ?: return@composable
            PacienteDetailScreen(
                pacienteId = pacienteId,
                navController = navController,
                viewModel = viewModel
            )
        }
        composable("gestionarEnfermedades/{pacienteId}") { backStackEntry ->
            val pacienteId = backStackEntry.arguments?.getString("pacienteId") ?: return@composable
            GestionarEnfermedadesScreen(
                pacienteId = pacienteId,
                navController = navController,
                viewModel = viewModel
            )
        }
        composable("gestionarTratamientos/{enfermedadPacienteId}") { backStackEntry ->
            val enfermedadPacienteId = backStackEntry.arguments?.getString("enfermedadPacienteId") ?: return@composable
            GestionarTratamientosScreen(
                enfermedadPacienteId = enfermedadPacienteId,
                navController = navController,
                viewModel = viewModel
            )
        }





    }
}
