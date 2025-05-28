package net.azarquiel.cuidaplus.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import net.azarquiel.cuidaplus.viewmodel.MainViewModel


@Composable
fun CitasScreen(navController: NavHostController, viewModel: MainViewModel) {
    Text("Pantalla Citas")
}