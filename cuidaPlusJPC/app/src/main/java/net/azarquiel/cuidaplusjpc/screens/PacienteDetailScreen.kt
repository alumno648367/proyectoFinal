package net.azarquiel.cuidaplusjpc.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import net.azarquiel.cuidaplusjpc.R
import net.azarquiel.cuidaplusjpc.model.Paciente
import net.azarquiel.cuidaplusjpc.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PacienteDetailScreen(
    pacienteId: String,
    viewModel: MainViewModel,
    navController: NavHostController
) {
    var paciente by remember { mutableStateOf<Paciente?>(null) }

    // Escucha el paciente
    LaunchedEffect(pacienteId) {
        viewModel.pacienteVM.escucharPaciente(pacienteId) {
            paciente = it
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = paciente?.nombreCompleto ?: "Paciente", color = MaterialTheme.colorScheme.onPrimary)
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorResource(R.color.primario))
            )
        },
        containerColor = colorResource(R.color.fondo_claro)
    ) { padding ->
        paciente?.let { PacienteDetailScreenContent(it, padding, viewModel) }
            ?: Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(alignment = androidx.compose.ui.Alignment.Center))
            }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun PacienteDetailScreenContent(
    paciente: Paciente,
    padding: PaddingValues,
    viewModel: MainViewModel
) {
    var expandedEnfermedades by remember { mutableStateOf(true) } // abierto por defecto
    val context = LocalContext.current
    var showDialogPaciente by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {


            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.cardColors(containerColor = colorResource(R.color.white))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Datos del paciente", style = MaterialTheme.typography.titleMedium)

                    Divider()

                    DatoConIcono(Icons.Default.Person, paciente.nombreCompleto)
                    DatoConIcono(Icons.Default.Home, paciente.direccion)
                    DatoConIcono(Icons.Default.Group, paciente.nombreGrupo)
                    DatoConIcono(
                        Icons.Default.CalendarToday,
                        java.text.SimpleDateFormat("dd/MM/yyyy").format(paciente.fechaNacimiento)
                    )
                    Button(
                        onClick = { showDialogPaciente = true },
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


        // Enfermedades (implementada)
        item {
            Card(
                onClick = { expandedEnfermedades = !expandedEnfermedades },
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Enfermedades", style = MaterialTheme.typography.titleMedium)
                    if (expandedEnfermedades) {
                        Spacer(Modifier.height(8.dp))
                        if (paciente.enfermedades.isEmpty()) {
                            Text("No tiene enfermedades asignadas.")
                        } else {
                            paciente.enfermedades.forEach { id ->
                                Text("- $id") // Más adelante puedes mostrar el nombre real
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                // Navegar a pantalla de gestión de enfermedades
                                 //navController.navigate("gestionarEnfermedades/${paciente.pacienteId}")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.primario))
                        ) {
                            Text("Gestionar enfermedades", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }
        }

        // Secciones aún no implementadas
        val secciones = listOf("Tratamientos", "Medicación", "Citas médicas", "Historial clínico")
        items(secciones.size) { i ->
            var expanded by remember { mutableStateOf(false) }
            Card(
                onClick = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(secciones[i], style = MaterialTheme.typography.titleMedium)
                    if (expanded) {
                        Spacer(Modifier.height(8.dp))
                        Text("No implementado")
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
    if (showDialogPaciente) {
        EditarPacienteDialog(
            paciente = paciente,
            onDismiss = { showDialogPaciente = false },
            onGuardar = { editado ->
                viewModel.pacienteVM.guardarPaciente(
                    editado,
                    onSuccess = {
                        Toast.makeText(context, "Datos actualizados", Toast.LENGTH_SHORT).show()
                        showDialogPaciente = false
                    },
                    onFailure = {
                        Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        )
    }
}
@Composable
fun DatoConIcono(icon: ImageVector, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colorResource(R.color.primario),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(texto)
    }
}
@Composable
fun EditarPacienteDialog(
    paciente: Paciente,
    onDismiss: () -> Unit,
    onGuardar: (Paciente) -> Unit
) {
    var nombre by remember { mutableStateOf(paciente.nombreCompleto) }
    var direccion by remember { mutableStateOf(paciente.direccion) }
    var fechaTexto by remember {
        mutableStateOf(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(paciente.fechaNacimiento))
    }
    var fechaDate: Date? = paciente.fechaNacimiento
    var showDateError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    if (nombre.isBlank() || direccion.isBlank() || fechaDate == null) return@TextButton
                    val pacienteEditado = paciente.copy(
                        nombreCompleto = nombre,
                        direccion = direccion,
                        fechaNacimiento = fechaDate!!
                    )
                    onGuardar(pacienteEditado)
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
            Text("Editar paciente", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre completo") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección") },
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
                        text = "Formato inválido (dd/MM/yyyy)",
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

