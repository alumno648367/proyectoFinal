package net.azarquiel.cuidaplusjpc.screens

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import net.azarquiel.cuidaplusjpc.R
import net.azarquiel.cuidaplusjpc.navigation.AppScreens
import net.azarquiel.cuidaplusjpc.viewmodel.MainViewModel

@Composable
fun SplashScreen(navController: NavHostController, viewModel: MainViewModel) {
    // Retraso para simular splash y comprobar usuario
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    LaunchedEffect(true) {
        kotlinx.coroutines.delay(1000)

        if (currentUser != null) {
            val uid = currentUser.uid

            // Verificar si el usuario existe
            val doc = viewModel.db.collection("usuarios").document(uid).get().await()
            if (doc.exists()) {
                viewModel.usuarioVM.empezarEscucha(uid)

                // Espera a que el usuario cargue
                viewModel.usuarioVM.usuario.observeForever { usuario ->
                    val grupoId = usuario?.grupos?.firstOrNull()
                    if (!grupoId.isNullOrEmpty()) {
                        viewModel.grupoVM.cargarGrupo(grupoId)
                        viewModel.pacienteVM.cargarPacientesDelGrupo(grupoId)
                    }
                }

                navController.navigate(AppScreens.MainScreen.route) {
                    popUpTo(0)
                }
            } else {
                // Eliminar usuario si no tiene documento
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
