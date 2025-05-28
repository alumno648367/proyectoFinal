package net.azarquiel.cuidaplus.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import net.azarquiel.cuidaplus.screens.AddPacienteScreen
import net.azarquiel.cuidaplus.screens.CitasScreen
import net.azarquiel.cuidaplus.screens.GestionarEnfermedadesScreen
import net.azarquiel.cuidaplus.screens.GestionarMedicacionScreen
import net.azarquiel.cuidaplus.screens.GestionarTratamientosScreen
import net.azarquiel.cuidaplus.screens.GrupoScreen
import net.azarquiel.cuidaplus.screens.MiCuentaScreen
import net.azarquiel.cuidaplus.screens.PacienteDetailScreen
import net.azarquiel.cuidaplus.screens.PacientesScreen
import net.azarquiel.cuidaplus.screens.PerfilScreen
import net.azarquiel.cuidaplus.viewmodel.MainViewModel

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
        composable("gestionarMedicacion/{enfermedadPacienteId}") { backStackEntry ->
            val enfermedadPacienteId =
                backStackEntry.arguments?.getString("enfermedadPacienteId") ?: return@composable
            GestionarMedicacionScreen(
                enfermedadPacienteId = enfermedadPacienteId,
                navController = navController,
                viewModel = viewModel
            )
        }




    }
}
