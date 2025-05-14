package net.azarquiel.cuidaplusjpc.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import net.azarquiel.cuidaplusjpc.model.Usuario
import net.azarquiel.cuidaplusjpc.viewmodel.MainViewModel
import java.util.*

@Composable
fun RegisterUsuarioScreen(navController: NavHostController, viewModel: MainViewModel) {
    Scaffold(
        topBar = { CustomTopBar("Registro de Usuario") },
        content = { padding ->
            CustomRegisterContent(padding, viewModel)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(titulo: String) {
    TopAppBar(
        title = { Text(text = titulo) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
fun CustomRegisterContent(padding: PaddingValues, viewModel: MainViewModel) {
    val usuarioVM = viewModel.usuarioVM
    val context = LocalContext.current

    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var grupoFamiliarId by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp)
    ) {
        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") })
        OutlinedTextField(value = fechaNacimiento, onValueChange = { fechaNacimiento = it }, label = { Text("Fecha de nacimiento") })
        OutlinedTextField(value = grupoFamiliarId, onValueChange = { grupoFamiliarId = it }, label = { Text("Grupo Familiar ID") })

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (nombre.isBlank() || email.isBlank() || telefono.isBlank()) {
                Toast.makeText(context, "Por favor rellena todos los campos", Toast.LENGTH_LONG).show()
            } else {
                val usuario = Usuario(
                    usuarioId = UUID.randomUUID().toString(), // temporal
                    nombre = nombre,
                    email = email,
                    numTelefono = telefono,
                    fechaNacimiento = fechaNacimiento,
                    grupoFamiliarId = grupoFamiliarId
                )
                usuarioVM.guardarUsuario(usuario,
                    onSuccess = {
                        Toast.makeText(context, "Usuario guardado correctamente", Toast.LENGTH_SHORT).show()
                    },
                    onFailure = {
                        Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }) {
            Text("Guardar Usuario")
        }
    }
}
