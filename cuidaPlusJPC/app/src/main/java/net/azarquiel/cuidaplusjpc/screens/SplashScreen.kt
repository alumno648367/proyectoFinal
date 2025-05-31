package net.azarquiel.cuidaplusjpc.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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

    // Efecto lanzado una sola vez al entrar en la pantalla
    LaunchedEffect(true) {
        delay(1000) // Espera breve para mostrar el splash

        if (currentUser == null) {
            // Si no hay usuario logueado → ir a pantalla Home
            navController.navigate(AppScreens.HomeScreen.route) {
                popUpTo(0)
            }
        } else {
            val uid = currentUser.uid

            // Comprobamos si el usuario existe en la colección "usuarios"
            val doc = viewModel.db.collection("usuarios").document(uid).get().await()

            if (doc.exists()) {
                // Si existe, empezamos a escuchar sus datos
                viewModel.usuarioVM.empezarEscucha(uid)

                // Esperamos a que LiveData se llene (usuario cargado)
                while (viewModel.usuarioVM.usuario.value == null) {
                    delay(100)
                }

                // Cargar grupo y pacientes si tiene grupo asignado
                val grupoId = viewModel.usuarioVM.usuario.value?.grupos?.firstOrNull()
                if (!grupoId.isNullOrEmpty()) {
                    viewModel.grupoVM.cargarGrupo(grupoId)
                    viewModel.pacienteVM.cargarPacientesDelGrupo(grupoId)
                }

                // Ir a la pantalla de inicio principal
                navController.navigate("inicio") {
                    popUpTo(0)
                }

            } else {
                // Si Auth está activo pero el documento no existe → cerramos sesión
                auth.signOut()
                navController.navigate(AppScreens.HomeScreen.route) {
                    popUpTo(0)
                }
            }
        }
    }

    // Parte visual del splash: logo y título centrados
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.fondo_claro))
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.animation.AnimatedVisibility(
            visible = true,
            enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.scaleIn()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.logosinfondo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(300.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Cuida+",
                    style = MaterialTheme.typography.displaySmall,
                    color = colorResource(R.color.texto_principal)
                )
            }
        }
    }

}
