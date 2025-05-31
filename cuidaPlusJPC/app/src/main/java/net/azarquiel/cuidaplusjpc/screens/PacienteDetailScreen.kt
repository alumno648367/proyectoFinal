package net.azarquiel.cuidaplusjpc.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import net.azarquiel.cuidaplusjpc.R
import net.azarquiel.cuidaplusjpc.model.Paciente
import net.azarquiel.cuidaplusjpc.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PacienteDetailScreen(
    pacienteId: String,
    viewModel: MainViewModel,
    navController: NavHostController,
    padding: PaddingValues = PaddingValues()
) {
    var paciente by remember { mutableStateOf<Paciente?>(null) }

    LaunchedEffect(pacienteId) {
        viewModel.pacienteVM.escucharPaciente(pacienteId) {
            paciente = it
        }
    }

    val paddingTop = padding.calculateTopPadding()
    val paddingBottom = padding.calculateBottomPadding()

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = colorResource(R.color.secundario),
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = paciente?.nombreCompleto ?: "Paciente",
                    color = colorResource(R.color.texto_principal),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        containerColor = colorResource(R.color.fondo_claro)
    ) { innerPadding ->
        paciente?.let {
            PacienteDetailScreenContent(
                paciente = it,
                viewModel = viewModel,
                navController = navController,
                paddingTop = paddingTop + innerPadding.calculateTopPadding(),
                paddingBottom = paddingBottom + innerPadding.calculateBottomPadding()
            )
        } ?: Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun PacienteDetailScreenContent(
    paciente: Paciente,
    viewModel: MainViewModel,
    navController: NavHostController,
    paddingTop: Dp,
    paddingBottom: Dp
) {
    var showDialogPaciente by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val relaciones by viewModel.enfermedadPacienteVM.relaciones.observeAsState(emptyList())

    LaunchedEffect(paciente.pacienteId) {
        viewModel.enfermedadPacienteVM.cargarPorPaciente(paciente.pacienteId)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = paddingTop, start = 24.dp, end = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = paddingBottom + 80.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.cardColors(containerColor = colorResource(R.color.white))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Datos del paciente", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Divider()
                    DatoConIcono(Icons.Default.Person, paciente.nombreCompleto, colorResource(R.color.primario))
                    DatoConIcono(Icons.Default.Home, paciente.direccion, colorResource(R.color.secundario))
                    DatoConIcono(Icons.Default.Group, paciente.nombreGrupo, colorResource(R.color.terciario))
                    DatoConIcono(
                        Icons.Default.CalendarToday,
                        SimpleDateFormat("dd/MM/yyyy").format(paciente.fechaNacimiento),
                        colorResource(R.color.icono_secundario)
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

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.cardColors(containerColor = colorResource(R.color.white))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Historial médico", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = colorResource(R.color.texto_principal))
                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    if (relaciones.isEmpty()) {
                        Text("No tiene enfermedades asignadas.", fontSize = 16.sp, color = Color.Gray)
                    } else {
                        relaciones.forEach { ep ->
                            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                                Text("• ${ep.nombre}", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                                Text("Categoría: ${ep.categoria}", fontSize = 14.sp, color = Color.DarkGray)

                                LaunchedEffect(ep.enfermedadPacienteId) {
                                    viewModel.tratamientoVM.cargarTratamientos(ep.enfermedadPacienteId)
                                    viewModel.medicamentoVM.cargarMedicamentos(ep.enfermedadPacienteId)
                                }

                                val tratamientos = viewModel.tratamientoVM.tratamientosPorEnfermedad[ep.enfermedadPacienteId] ?: emptyList()
                                val medicamentos = viewModel.medicamentoVM.medicamentosPorEnfermedad[ep.enfermedadPacienteId] ?: emptyList()

                                Spacer(Modifier.height(6.dp))
                                Text("Tratamientos:", fontWeight = FontWeight.Medium, color = colorResource(R.color.secundario))
                                if (tratamientos.isEmpty()) {
                                    Text("No tiene tratamientos asignados.", fontSize = 14.sp, color = Color.Gray)
                                } else {
                                    tratamientos.forEach { t ->
                                        Text("• ${t.nombre} (${t.tipo})", fontSize = 14.sp)
                                    }
                                }

                                Button(
                                    onClick = { navController.navigate("gestionarTratamientos/${ep.enfermedadPacienteId}") },
                                    modifier = Modifier.align(Alignment.End).padding(top = 4.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.primario))
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                                    Spacer(Modifier.width(6.dp))
                                    Text("Añadir tratamiento", color = Color.White)
                                }

                                Spacer(Modifier.height(8.dp))
                                Text("Medicación:", fontWeight = FontWeight.Medium, color = colorResource(R.color.terciario))
                                if (medicamentos.isEmpty()) {
                                    Text("No tiene medicación registrada.", fontSize = 14.sp, color = Color.Gray)
                                } else {
                                    medicamentos.forEach {
                                        Text("• ${it.nombre} (${it.dosis}, ${it.frecuencia}, ${it.viaAdministracion})", fontSize = 14.sp)
                                    }
                                }

                                Button(
                                    onClick = { navController.navigate("gestionarMedicacion/${ep.enfermedadPacienteId}") },
                                    modifier = Modifier.align(Alignment.End).padding(top = 4.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.primario))
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                                    Spacer(Modifier.width(6.dp))
                                    Text("Añadir medicación", color = Color.White)
                                }

                                Divider(modifier = Modifier.padding(top = 16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { navController.navigate("gestionarEnfermedades/${paciente.pacienteId}") },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.primario))
                    ) {
                        Icon(Icons.Default.EditNote, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gestionar enfermedades", color = Color.White)
                    }
                }
            }
        }
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
fun DatoConIcono(icon: ImageVector, texto: String, tint: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(texto, fontSize = 16.sp, color = colorResource(R.color.texto_principal))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPacienteDialog(
    paciente: Paciente,
    onDismiss: () -> Unit,
    onGuardar: (Paciente) -> Unit
) {
    val context = LocalContext.current
    val formato = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    var nombre by remember { mutableStateOf(paciente.nombreCompleto) }
    var direccion by remember { mutableStateOf(paciente.direccion) }
    var fechaTexto by remember { mutableStateOf(formato.format(paciente.fechaNacimiento)) }
    var fechaDate by remember { mutableStateOf(paciente.fechaNacimiento) }
    var showDateError by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

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
                "Editar datos del paciente",
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
                    label = { Text("Nombre completo") },
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
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección") },
                    leadingIcon = {
                        Icon(Icons.Default.Home, contentDescription = null, tint = colorResource(R.color.primario))
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
                    if (nombre.isBlank() || direccion.isBlank() || fechaDate == null) return@Button
                    val pacienteEditado = paciente.copy(
                        nombreCompleto = nombre,
                        direccion = direccion,
                        fechaNacimiento = fechaDate!!
                    )
                    onGuardar(pacienteEditado)
                },
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.primario))
            ) {
                Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Guardar", color = Color.White)
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
