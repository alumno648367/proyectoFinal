package net.azarquiel.cuidaplusjpc.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import net.azarquiel.cuidaplusjpc.R
import net.azarquiel.cuidaplusjpc.viewmodel.MainViewModel
import net.azarquiel.cuidaplusjpc.navigation.AppScreens

@Composable
fun LoginUsuarioScreen(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    Scaffold(
        topBar = { LoginTopBar(viewModel) },
        content = { padding ->
            LoginUsuarioContent(padding, navController, viewModel)
        }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginTopBar(viewModel: MainViewModel) {
    TopAppBar(
        title = { Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "LOGIN",
                modifier = Modifier
                    .padding(0.dp, 0.dp, 10.dp, 0.dp)
                    .weight(1f),
                textAlign = TextAlign.Start
            )
        }
        },
        colors = topAppBarColors(
            containerColor = colorResource(R.color.azul),
            titleContentColor = MaterialTheme.colorScheme.background
        )
    )
}
@Composable
fun LoginUsuarioContent(
    padding: PaddingValues,
    navController: NavHostController,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val usuarioVM = viewModel.usuarioVM

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener { result ->
                            val uid = result.user?.uid ?: ""
                            usuarioVM.empezarEscucha(uid)
                            Toast.makeText(context, "Sesión iniciada", Toast.LENGTH_SHORT).show()
                            navController.navigate(AppScreens.RegisterCompletoScreen.route) // o HomeUsuarioScreen si ya está registrado
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(context, "Introduce email y contraseña", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar sesión")
        }
    }
}
