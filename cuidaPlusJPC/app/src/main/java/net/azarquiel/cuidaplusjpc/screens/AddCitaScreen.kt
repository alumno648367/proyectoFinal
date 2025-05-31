package net.azarquiel.cuidaplusjpc.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.NotificationAdd
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
import net.azarquiel.cuidaplusjpc.R
import net.azarquiel.cuidaplusjpc.model.CitaMedica
import net.azarquiel.cuidaplusjpc.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCitaScreen(
    navController: NavHostController,
    viewModel: MainViewModel,
    grupoId: String,
    padding: PaddingValues = PaddingValues()
) {
    val grupo = viewModel.grupoVM.grupo.observeAsState().value

    LaunchedEffect(grupo?.grupoFamiliarId) {
        grupo?.let {
            viewModel.usuarioVM.obtenerUsuariosPorIds(it.miembros)
            viewModel.pacienteVM.cargarPacientesDelGrupo(it.grupoFamiliarId)
        }
    }
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationAdd,
                    contentDescription = null,
                    tint = colorResource(R.color.primario),
                    modifier = Modifier
                        .size(100.dp)
                        .padding(top = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Añadir cita",
                    color = colorResource(R.color.texto_principal),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        containerColor = colorResource(R.color.fondo_claro)
    ) { innerPadding ->
        val combinedPadding = PaddingValues(
            top = padding.calculateTopPadding() + innerPadding.calculateTopPadding(),
            bottom = padding.calculateBottomPadding() + innerPadding.calculateBottomPadding(),
            start = 24.dp,
            end = 24.dp
        )
        AddCitaScreenContent(navController, viewModel, grupoId, combinedPadding)
    }
}

fun combinarFechaHora(fecha: Date?, hora: Date?): Date {
    val cal = Calendar.getInstance()
    fecha?.let { cal.time = it }
    hora?.let {
        val h = Calendar.getInstance().apply { time = it }
        cal.set(Calendar.HOUR_OF_DAY, h.get(Calendar.HOUR_OF_DAY))
        cal.set(Calendar.MINUTE,    h.get(Calendar.MINUTE))
    }
    return cal.time
}

@Composable
fun AddCitaScreenContent(
    navController: NavHostController,
    viewModel: MainViewModel,
    grupoId: String,
    padding: PaddingValues
) {
    val pacientes by viewModel.pacienteVM.pacientesDelGrupo.observeAsState(emptyList())
    val usuarios by viewModel.usuarioVM.usuariosGrupo.observeAsState(emptyList())

    val context = LocalContext.current

    var indicePaciente by remember { mutableStateOf(0) }
    val pacienteSeleccionado = pacientes.getOrNull(indicePaciente)

    var indiceUsuario by remember { mutableStateOf(0) }
    val usuarioSeleccionado = usuarios.getOrNull(indiceUsuario)

    var fechaTexto by remember { mutableStateOf("") }
    var fechaDate by remember { mutableStateOf<Date?>(null) }

    var horaTexto by remember { mutableStateOf("") }
    var horaDate by remember { mutableStateOf<Date?>(null) }

    var especialidad by remember { mutableStateOf("") }
    var medico by remember { mutableStateOf("") }
    var motivo by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }
    var realizada by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()

    // DatePicker para fecha
    val datePicker = DatePickerDialog(
        context,
        { _, year, month, day ->
            calendar.set(year, month, day)
            fechaDate = calendar.time
            val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            fechaTexto = formatoFecha.format(fechaDate!!)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // TimePicker para hora
    val timePicker = TimePickerDialog(
        context,
        { _, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            horaDate = calendar.time
            val formatoHora = SimpleDateFormat("HH:mm", Locale.getDefault())
            horaTexto = formatoHora.format(horaDate!!)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Carrusel de pacientes
        if (pacientes.isNotEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Selecciona un paciente",
                    style = MaterialTheme.typography.titleMedium,
                    color = colorResource(R.color.texto_principal),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = {
                        if (indicePaciente > 0) indicePaciente--
                    }) {
                        Icon(Icons.Default.ArrowBackIos, contentDescription = "Anterior")
                    }
                    Text(
                        text = pacienteSeleccionado?.nombreCompleto ?: "",
                        style = MaterialTheme.typography.titleMedium
                    )
                    IconButton(onClick = {
                        if (indicePaciente < pacientes.lastIndex) indicePaciente++
                    }) {
                        Icon(Icons.Default.ArrowForwardIos, contentDescription = "Siguiente")
                    }
                }
            }
        } else {
            Text("No hay pacientes disponibles", color = Color.Gray)
        }


        // Fecha y hora
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = fechaTexto,
                onValueChange = {
                    fechaTexto = it
                    val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    fechaDate = try {
                        formato.parse(it)
                    } catch (e: Exception) {
                        null
                    }
                },
                label = { Text("Fecha") },
                modifier = Modifier.weight(1f),
                trailingIcon = {
                    IconButton(onClick = { datePicker.show() }) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Seleccionar fecha",
                            tint = colorResource(R.color.primario)
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(R.color.primario),
                    unfocusedBorderColor = colorResource(R.color.texto_principal),
                    cursorColor = colorResource(R.color.primario),
                    focusedLabelColor = colorResource(R.color.primario)
                )
            )

            OutlinedTextField(
                value = horaTexto,
                onValueChange = {
                    horaTexto = it
                    val formato = SimpleDateFormat("HH:mm", Locale.getDefault())
                    horaDate = try {
                        formato.parse(it)
                    } catch (e: Exception) {
                        null
                    }
                },
                label = { Text("Hora") },
                modifier = Modifier.weight(1f),
                trailingIcon = {
                    IconButton(onClick = { timePicker.show() }) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Seleccionar hora",
                            tint = colorResource(R.color.primario)
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(R.color.primario),
                    unfocusedBorderColor = colorResource(R.color.texto_principal),
                    cursorColor = colorResource(R.color.primario),
                    focusedLabelColor = colorResource(R.color.primario)
                )
            )
        }
        OutlinedTextField(
            value = especialidad,
            onValueChange = { especialidad = it },
            label = { Text("Especialidad") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(R.color.primario),
                unfocusedBorderColor = colorResource(R.color.secundario),
                cursorColor = colorResource(R.color.primario),
                focusedLabelColor = colorResource(R.color.primario),
                unfocusedLabelColor = colorResource(R.color.texto_principal),
                focusedLeadingIconColor = colorResource(R.color.primario),
                unfocusedLeadingIconColor = colorResource(R.color.secundario),
                focusedTrailingIconColor = colorResource(R.color.primario),
                unfocusedTrailingIconColor = colorResource(R.color.secundario),
                focusedPlaceholderColor = colorResource(R.color.texto_principal),
                unfocusedPlaceholderColor = colorResource(R.color.texto_principal),
                focusedTextColor = colorResource(R.color.texto_principal),
                unfocusedTextColor = colorResource(R.color.texto_principal)
            )
        )
        OutlinedTextField(
            value = medico,
            onValueChange = { medico = it },
            label = { Text("Médico") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(R.color.primario),
                unfocusedBorderColor = colorResource(R.color.secundario),
                cursorColor = colorResource(R.color.primario),
                focusedLabelColor = colorResource(R.color.primario),
                unfocusedLabelColor = colorResource(R.color.texto_principal),
                focusedLeadingIconColor = colorResource(R.color.primario),
                unfocusedLeadingIconColor = colorResource(R.color.secundario),
                focusedTrailingIconColor = colorResource(R.color.primario),
                unfocusedTrailingIconColor = colorResource(R.color.secundario),
                focusedPlaceholderColor = colorResource(R.color.texto_principal),
                unfocusedPlaceholderColor = colorResource(R.color.texto_principal),
                focusedTextColor = colorResource(R.color.texto_principal),
                unfocusedTextColor = colorResource(R.color.texto_principal)
            )
        )
        OutlinedTextField(
            value = ubicacion,
            onValueChange = { ubicacion = it },
            label = { Text("Ubicación") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(R.color.primario),
                unfocusedBorderColor = colorResource(R.color.secundario),
                cursorColor = colorResource(R.color.primario),
                focusedLabelColor = colorResource(R.color.primario),
                unfocusedLabelColor = colorResource(R.color.texto_principal),
                focusedLeadingIconColor = colorResource(R.color.primario),
                unfocusedLeadingIconColor = colorResource(R.color.secundario),
                focusedTrailingIconColor = colorResource(R.color.primario),
                unfocusedTrailingIconColor = colorResource(R.color.secundario),
                focusedPlaceholderColor = colorResource(R.color.texto_principal),
                unfocusedPlaceholderColor = colorResource(R.color.texto_principal),
                focusedTextColor = colorResource(R.color.texto_principal),
                unfocusedTextColor = colorResource(R.color.texto_principal)
            )
        )
        OutlinedTextField(
            value = motivo,
            onValueChange = { motivo = it },
            label = { Text("Motivo") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(R.color.primario),
                unfocusedBorderColor = colorResource(R.color.secundario),
                cursorColor = colorResource(R.color.primario),
                focusedLabelColor = colorResource(R.color.primario),
                unfocusedLabelColor = colorResource(R.color.texto_principal),
                focusedLeadingIconColor = colorResource(R.color.primario),
                unfocusedLeadingIconColor = colorResource(R.color.secundario),
                focusedTrailingIconColor = colorResource(R.color.primario),
                unfocusedTrailingIconColor = colorResource(R.color.secundario),
                focusedPlaceholderColor = colorResource(R.color.texto_principal),
                unfocusedPlaceholderColor = colorResource(R.color.texto_principal),
                focusedTextColor = colorResource(R.color.texto_principal),
                unfocusedTextColor = colorResource(R.color.texto_principal)
            )
        )
        OutlinedTextField(
            value = observaciones,
            onValueChange = { observaciones = it },
            label = { Text("Observaciones") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(R.color.primario),
                unfocusedBorderColor = colorResource(R.color.secundario),
                cursorColor = colorResource(R.color.primario),
                focusedLabelColor = colorResource(R.color.primario),
                unfocusedLabelColor = colorResource(R.color.texto_principal),
                focusedLeadingIconColor = colorResource(R.color.primario),
                unfocusedLeadingIconColor = colorResource(R.color.secundario),
                focusedTrailingIconColor = colorResource(R.color.primario),
                unfocusedTrailingIconColor = colorResource(R.color.secundario),
                focusedPlaceholderColor = colorResource(R.color.texto_principal),
                unfocusedPlaceholderColor = colorResource(R.color.texto_principal),
                focusedTextColor = colorResource(R.color.texto_principal),
                unfocusedTextColor = colorResource(R.color.texto_principal)
            )
        )

        if (usuarios.isNotEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Selecciona el usuario que acude",
                    style = MaterialTheme.typography.titleMedium,
                    color = colorResource(R.color.texto_principal),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = {
                        if (indiceUsuario > 0) indiceUsuario--
                    }) {
                        Icon(Icons.Default.ArrowBackIos, contentDescription = "Anterior")
                    }
                    Text(
                        text = usuarioSeleccionado?.nombre ?: "",
                        style = MaterialTheme.typography.titleMedium
                    )
                    IconButton(onClick = {
                        if (indiceUsuario < usuarios.lastIndex) indiceUsuario++
                    }) {
                        Icon(Icons.Default.ArrowForwardIos, contentDescription = "Siguiente")
                    }
                }
            }
        } else {
            Text("No hay usuarios disponibles", color = Color.Gray)
        }
        val formularioValido = pacienteSeleccionado != null &&
                usuarioSeleccionado != null &&
                fechaDate != null &&
                horaDate != null &&
                especialidad.isNotBlank() &&
                medico.isNotBlank() &&
                motivo.isNotBlank() &&
                ubicacion.isNotBlank()

        Button(

            onClick = {
                // Crear la cita solo si todo es válido
                val fechaHoraFinal = combinarFechaHora(fechaDate, horaDate)

                val cita = CitaMedica(
                    citaMedicaId = UUID.randomUUID().toString(),
                    grupoFamiliarId = grupoId,
                    pacienteId = pacienteSeleccionado!!.pacienteId,
                    usuarioAcompananteNombre = usuarioSeleccionado!!.nombre,
                    fechaHora = fechaHoraFinal,
                    especialidad = especialidad,
                    medico = medico,
                    ubicacion = ubicacion,
                    motivo = motivo,
                    observaciones = observaciones,
                    realizada = realizada
                )

                viewModel.citaVM.guardarCita(
                    cita,
                    onSuccess = {
                        Toast.makeText(context, "Cita guardada", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    },
                    onFailure = {
                        Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                    }
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = formularioValido,
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.primario))
        ) {
            Icon(Icons.Default.Add, contentDescription = "Guardar", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Guardar", color = Color.White)
        }
    }
}