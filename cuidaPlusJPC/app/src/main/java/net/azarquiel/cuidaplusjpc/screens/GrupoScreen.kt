package net.azarquiel.cuidaplusjpc.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import net.azarquiel.cuidaplusjpc.viewmodel.MainViewModel

@Composable
fun GrupoScreen(navController: NavHostController, viewModel: MainViewModel) {
    Text("Pantalla Grupo")
}

@Composable
fun InicioScreen(navController: NavHostController, viewModel: MainViewModel) {
    Text("Pantalla Inicio")
}

@Composable
fun CitasScreen(navController: NavHostController, viewModel: MainViewModel) {
    Text("Pantalla Citas")
}

@Composable
fun PacientesScreen(navController: NavHostController, viewModel: MainViewModel) {
    Text("Pantalla Pacientes")
}

@Composable
fun NotificacionesScreen(navController: NavHostController, viewModel: MainViewModel) {
    Text("Pantalla Notificaciones")
}
