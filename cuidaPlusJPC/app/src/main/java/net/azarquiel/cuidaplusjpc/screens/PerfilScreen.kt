package net.azarquiel.cuidaplusjpc.screens

import Usuario
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import net.azarquiel.cuidaplusjpc.R
import net.azarquiel.cuidaplusjpc.navigation.AppScreens
import net.azarquiel.cuidaplusjpc.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PerfilScreen(navController: NavHostController, viewModel: MainViewModel) {
    Scaffold(
        containerColor = colorResource(R.color.fondo_claro)
    ) { paddingValues ->
        PerfilScreenContent(
            padding = paddingValues,
            viewModel = viewModel,
            navController = navController
        )
    }
}

@Composable
fun PerfilScreenContent(
    padding: PaddingValues,
    viewModel: MainViewModel,
    navController: NavHostController
) {
    val usuario = viewModel.usuarioVM.usuario.observeAsState()
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    usuario.value?.let { user ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .padding(top = 15.dp),
                    tint = colorResource(R.color.primario)
                )
            }

            item {
                Text("Hola, ${user.nombre}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("Datos personales", fontSize = 20.sp, fontWeight = FontWeight.Bold)

                        FieldItem("Nombre", user.nombre)
                        FieldItem("Email", user.email)
                        FieldItem("Teléfono", user.numTelefono.toString())
                        FieldItem(
                            "Fecha nacimiento",
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(user.fechaNacimiento)
                        )

                        Button(
                            onClick = { showDialog = true },
                            modifier = Modifier.align(Alignment.End),
                            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.primario))
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Editar", color = Color.White)
                        }
                    }
                }
            }

            item {
                OutlinedButton(
                    onClick = { viewModel.cerrarSesion(navController) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.primario),
                        contentColor = colorResource(R.color.texto_principal)
                    )
                ) {
                    Text("Cerrar sesión", fontSize = 16.sp)
                }


            }

            item {
                Spacer(modifier = Modifier.height(100.dp)) // Espacio extra para evitar que el contenido quede tapado
            }
        }

        if (showDialog) {
            EditarUsuarioDialog(
                usuario = user,
                onDismiss = { showDialog = false },
                onGuardar = { usuarioEditado ->
                    viewModel.usuarioVM.guardarUsuario(
                        usuarioEditado,
                        onSuccess = {
                            Toast.makeText(context, "Datos actualizados", Toast.LENGTH_SHORT).show()
                            showDialog = false
                        },
                        onFailure = {
                            Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            )
        }

    } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = colorResource(R.color.primario))
    }
}


@Composable
fun FieldItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = colorResource(R.color.fondo_claro), shape = RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Text(text = label, fontSize = 14.sp, color = Color.Gray)
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun EditarUsuarioDialog(
    usuario: Usuario,
    onDismiss: () -> Unit,
    onGuardar: (Usuario) -> Unit
) {
    var nombre by remember { mutableStateOf(usuario.nombre) }
    var telefono by remember { mutableStateOf(usuario.numTelefono.toString()) }
    var fechaTexto by remember {
        mutableStateOf(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(usuario.fechaNacimiento))
    }
    var fechaDate: Date? = usuario.fechaNacimiento
    var showDateError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val telefonoLong = telefono.toLongOrNull()
                    if (nombre.isBlank() || telefonoLong == null || fechaDate == null) return@TextButton
                    val usuarioEditado = usuario.copy(
                        nombre = nombre,
                        numTelefono = telefonoLong,
                        fechaNacimiento = fechaDate!!
                    )
                    onGuardar(usuarioEditado)
                }
            ) {
                Text("Guardar", fontSize = 16.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", fontSize = 16.sp)
            }
        },
        title = {
            Text("Editar datos personales", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Teléfono") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = fechaTexto,
                    onValueChange = {
                        fechaTexto = it
                        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
                            isLenient = false
                        }
                        try {
                            fechaDate = formato.parse(it)
                            showDateError = false
                        } catch (e: Exception) {
                            fechaDate = null
                            showDateError = true
                        }
                    },
                    label = { Text("Fecha de nacimiento") },
                    isError = showDateError,
                    modifier = Modifier.fillMaxWidth()
                )
                if (showDateError) {
                    Text(
                        text = "Introduce una fecha válida (dd/MM/yyyy)",
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}
