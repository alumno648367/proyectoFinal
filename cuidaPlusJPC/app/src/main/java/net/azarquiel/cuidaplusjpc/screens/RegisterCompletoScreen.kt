package net.azarquiel.cuidaplusjpc.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import net.azarquiel.cuidaplusjpc.model.Usuario
import net.azarquiel.cuidaplusjpc.viewmodel.MainViewModel

@Composable
fun RegisterCompletoScreen(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    Scaffold(
        topBar = { ("Completa tu perfil") },
        content = { padding ->
            CustomRegisterCompletoContent(padding, viewModel)
        }
    )
}

@Composable
fun CustomRegisterCompletoContent(
    padding: PaddingValues,
    viewModel: MainViewModel
) {
    val usuarioVM = viewModel.usuarioVM
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    val email = currentUser?.email ?: ""
    val uid = currentUser?.uid ?: ""

    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp)
    ) {
        OutlinedTextField(value = email, onValueChange = {}, label = { Text("Email") }, enabled = false)
        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
        OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") })
        OutlinedTextField(value = fechaNacimiento, onValueChange = { fechaNacimiento = it }, label = { Text("Fecha de nacimiento") })

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (nombre.isBlank() || telefono.isBlank()) {
                Toast.makeText(context, "Rellena todos los campos", Toast.LENGTH_LONG).show()
            } else {
                val usuario = Usuario(
                    usuarioId = uid, // ✅ usar el UID correcto
                    nombre = nombre,
                    email = email,
                    numTelefono = telefono,
                    fechaNacimiento = fechaNacimiento
                )

                usuarioVM.guardarUsuario(
                    usuario,
                    onSuccess = {
                        Toast.makeText(context, "Usuario guardado", Toast.LENGTH_SHORT).show()

                    },
                    onFailure = {
                        Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }) {
            Text("Guardar")
        }
    }
}
