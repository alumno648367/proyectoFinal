package net.azarquiel.cuidaplusjpc.screens

import Usuario
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import net.azarquiel.cuidaplusjpc.R
import net.azarquiel.cuidaplusjpc.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PerfilScreen(navController: NavHostController, viewModel: MainViewModel) {
    Scaffold{ paddingValues ->
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
                .background(color = colorResource(R.color.fondo_claro))
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = padding.calculateTopPadding()),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icono de perfil
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

            // Saludo con el nombre del usuario
            item {
                Text("Hola, ${user.nombre}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }

            // Card con los datos del usuario
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

                        // Botón para editar datos
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

            // Card para cerrar sesión
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "¿Quieres cerrar sesión?",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorResource(R.color.texto_principal)
                        )

                        // Botón para cerrar sesión
                        Button(
                            onClick = { viewModel.cerrarSesion(navController) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(R.color.primario),
                                contentColor = colorResource(R.color.fondo_claro)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cerrar sesión", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            // Spacer para dar margen inferior exacto debajo del BottomNavigationBar
            item {
                Spacer(modifier = Modifier.height(padding.calculateBottomPadding()))
            }
        }

        // Diálogo para editar los datos del usuario
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarUsuarioDialog(
    usuario: Usuario,
    onDismiss: () -> Unit,
    onGuardar: (Usuario) -> Unit
) {
    val context = LocalContext.current
    val formato = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    var nombre by remember { mutableStateOf(usuario.nombre) }
    var telefono by remember { mutableStateOf(usuario.numTelefono.toString()) }
    var fechaTexto by remember { mutableStateOf(formato.format(usuario.fechaNacimiento)) }
    var fechaDate by remember { mutableStateOf(usuario.fechaNacimiento) }
    var showDateError by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Lógica para lanzar el selector de fecha
    if (showDatePicker) {
        val calendar = Calendar.getInstance().apply { time = fechaDate }
        android.app.DatePickerDialog(
            context,
            { _, year, month, day ->
                calendar.set(year, month, day, 0, 0, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                fechaDate = calendar.time
                fechaTexto = formato.format(calendar.time)
                showDateError = false
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White,
        title = {
            Text(
                "Editar datos personales",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.texto_principal)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = colorResource(R.color.primario))
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorResource(R.color.primario),
                        unfocusedBorderColor = colorResource(R.color.texto_principal),
                        cursorColor = colorResource(R.color.primario),
                        focusedLabelColor = colorResource(R.color.primario)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Teléfono") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = colorResource(R.color.primario))
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorResource(R.color.primario),
                        unfocusedBorderColor = colorResource(R.color.texto_principal),
                        cursorColor = colorResource(R.color.primario),
                        focusedLabelColor = colorResource(R.color.primario)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = fechaTexto,
                    onValueChange = {
                        fechaTexto = it
                        try {
                            fechaDate = formato.parse(it)!!
                            showDateError = false
                        } catch (e: Exception) {
                            showDateError = true
                        }
                    },
                    label = { Text("Fecha de nacimiento") },
                    placeholder = { Text("dd/MM/yyyy") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    isError = showDateError,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = colorResource(R.color.primario))
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorResource(R.color.primario),
                        unfocusedBorderColor = colorResource(R.color.texto_principal),
                        cursorColor = colorResource(R.color.primario),
                        focusedLabelColor = colorResource(R.color.primario)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )


                if (showDateError) {
                    Text("Introduce una fecha válida (dd/MM/yyyy)", color = Color.Red, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val telefonoLong = telefono.toLongOrNull()
                    if (nombre.isBlank() || telefonoLong == null || fechaDate == null) return@Button
                    val usuarioEditado = usuario.copy(
                        nombre = nombre,
                        numTelefono = telefonoLong,
                        fechaNacimiento = fechaDate!!
                    )
                    onGuardar(usuarioEditado)
                },
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.primario))
            ) {
                Text("Guardar", color = Color.White, fontSize = 16.sp)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colorResource(R.color.primario))
            ) {
                Text("Cancelar", fontSize = 16.sp)
            }
        }
    )
}

