package net.azarquiel.cuidaplusjpc.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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

    // Se ejecuta al entrar por primera vez a la pantalla
    LaunchedEffect(true) {
        delay(1000) // Mostramos el logo al menos 1 segundo

        if (currentUser == null) {
            // Si no hay usuario logueado, vamos a Home
            navController.navigate(AppScreens.HomeScreen.route) {
                popUpTo(0)
            }
        } else {
            val uid = currentUser.uid

            // Verificamos si existe el documento del usuario en Firestore
            val doc = viewModel.db.collection("usuarios").document(uid).get().await()

            if (doc.exists()) {
                // Activamos la escucha del usuario
                viewModel.usuarioVM.empezarEscucha(uid)
            } else {
                // Si el usuario no está en la base de datos, cerramos sesión
                auth.signOut()
                navController.navigate(AppScreens.HomeScreen.route) {
                    popUpTo(0)
                }
            }
        }
    }

    // Este bloque se ejecuta automáticamente cuando el usuario se ha cargado
    LaunchedEffect(usuario) {
        if (usuario != null) {
            val grupoId = usuario!!.grupos.firstOrNull()
            if (!grupoId.isNullOrEmpty()) {
                viewModel.grupoVM.cargarGrupo(grupoId)
                viewModel.pacienteVM.cargarPacientesDelGrupo(grupoId)
            }
            navController.navigate("inicio") {
                popUpTo(0)
            }
        }
    }

    // Parte visual del splash
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
                    painter = painterResource(id = R.drawable.logosinfondoconletras),
                    contentDescription = "Logo",
                    modifier = Modifier.size(350.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
