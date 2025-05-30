package net.azarquiel.cuidaplusjpc.screens

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.compose.rememberNavController
import net.azarquiel.cuidaplusjpc.navigation.BottomNavigationBar
import net.azarquiel.cuidaplusjpc.navigation.MainNavigation
import net.azarquiel.cuidaplusjpc.viewmodel.MainViewModel

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val navController = rememberNavController()

    // 1. Obtenemos el grupo actual del ViewModel
    val grupo = viewModel.grupoVM.grupo.observeAsState().value
    val grupoId = grupo?.grupoFamiliarId

    Scaffold(
        // 2. Pasamos el grupoId real (que será null hasta que cargue)
        bottomBar = { BottomNavigationBar(navController, grupoId) }
    ) { padding ->
        MainNavigation(
            navController = navController,
            viewModel     = viewModel,
            padding       = padding
        )
    }
}
