package net.azarquiel.cuidaplusjpc.screens

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import net.azarquiel.cuidaplusjpc.R
import net.azarquiel.cuidaplusjpc.navigation.AppScreens
import net.azarquiel.cuidaplusjpc.viewmodel.MainViewModel

@Composable
fun SplashScreen(navController: NavHostController, viewModel: MainViewModel) {
    // Retraso para simular splash y comprobar usuario
    Handler(Looper.getMainLooper()).postDelayed({
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val uid = currentUser.uid

            // Verificar si el documento del usuario existe
            viewModel.db.collection("usuarios").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        viewModel.usuarioVM.empezarEscucha(uid)
                        navController.navigate(AppScreens.MainScreen.route) {
                            popUpTo(0)
                        }
                    } else {
                        // Usuario autenticado pero sin documento → eliminar cuenta
                        currentUser.delete()
                            .addOnCompleteListener {
                                FirebaseAuth.getInstance().signOut()
                                navController.navigate(AppScreens.HomeScreen.route) {
                                    popUpTo(0)
                                }
                            }
                    }

                }
        } else {
            // No logueado → ir al inicio
            navController.navigate(AppScreens.HomeScreen.route) {
                popUpTo(0)
            }
        }
    }, 1000)

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
