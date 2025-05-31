package net.azarquiel.cuidaplusjpc.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import net.azarquiel.cuidaplusjpc.R
import net.azarquiel.cuidaplusjpc.navigation.AppScreens
import net.azarquiel.cuidaplusjpc.viewmodel.MainViewModel

@Composable
fun SplashScreen(navController: NavHostController, viewModel: MainViewModel) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val usuario by viewModel.usuarioVM.usuario.observeAsState()

    LaunchedEffect(true) {
        delay(1000)

        if (currentUser == null) {
            // No logueado → ir a Home
            navController.navigate(AppScreens.HomeScreen.route) {
                popUpTo(0)
            }
        } else {
            val uid = currentUser.uid
            val doc = viewModel.db.collection("usuarios").document(uid).get().await()

            if (doc.exists()) {
                viewModel.usuarioVM.empezarEscucha(uid)

                // Esperar hasta que usuarioVM tenga valor
                while (viewModel.usuarioVM.usuario.value == null) {
                    delay(100)
                }

                // Cargar datos relacionados
                val grupoId = viewModel.usuarioVM.usuario.value?.grupos?.firstOrNull()
                if (!grupoId.isNullOrEmpty()) {
                    viewModel.grupoVM.cargarGrupo(grupoId)
                    viewModel.pacienteVM.cargarPacientesDelGrupo(grupoId)
                }

                // Ir a inicio
                navController.navigate("inicio") {
                    popUpTo(0)
                }

            } else {
                // El usuario de Auth existe, pero no tiene documento → cerrar sesión
                auth.signOut()
                navController.navigate(AppScreens.HomeScreen.route) {
                    popUpTo(0)
                }
            }
        }
    }

    // UI splash
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.logosinletras),
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Cuida+",
                style = MaterialTheme.typography.headlineMedium,
                color = colorResource(R.color.texto_principal)
            )
        }
    }
}
