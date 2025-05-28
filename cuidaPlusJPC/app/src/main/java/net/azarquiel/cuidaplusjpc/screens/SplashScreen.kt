package net.azarquiel.cuidaplusjpc.screens

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
    val pacientes by viewModel.pacienteVM.pacientes.observeAsState()
    var esperandoPacientes by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        delay(1000)

        if (currentUser != null) {
            val uid = currentUser.uid
            val datosCargados = viewModel.cargarDatosDeUsuario(uid)
            println("¿Datos cargados? $datosCargados")
            println("Grupo usuario: ${viewModel.usuarioVM.usuario.value?.grupos}")

            if (datosCargados) {
                esperandoPacientes = true
            } else {
                currentUser.delete().await()
                auth.signOut()
                navController.navigate(AppScreens.HomeScreen.route) {
                    popUpTo(0)
                }
            }
        } else {
            navController.navigate(AppScreens.HomeScreen.route) {
                popUpTo(0)
            }
        }
    }

    LaunchedEffect(pacientes, esperandoPacientes) {
        println("PACIENTES OBSERVADOS: ${pacientes?.size}")
        if (esperandoPacientes && !pacientes.isNullOrEmpty()) {
            navController.navigate(AppScreens.MainScreen.route) {
                popUpTo(0)
            }
        }
    }

    // Pantalla visual
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
