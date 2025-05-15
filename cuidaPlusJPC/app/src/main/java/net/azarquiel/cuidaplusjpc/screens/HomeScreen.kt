package net.azarquiel.cuidaplusjpc.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import net.azarquiel.cuidaplusjpc.navigation.AppScreens
import net.azarquiel.cuidaplusjpc.viewmodel.MainViewModel

@Composable
fun HomeScreen(navController: NavHostController, viewModel: MainViewModel) {
    Scaffold(
        topBar = {  },
        content = { padding ->
            CustomHomeContent(padding, navController)
        }
    )
}

@Composable
fun CustomHomeContent(padding: PaddingValues, navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { navController.navigate(AppScreens.LoginUsuarioScreen.route) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Iniciar sesión")
        }

        Button(
            onClick = { navController.navigate(AppScreens.RegisterUsuarioScreen.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarse")
        }
    }
}
