package net.azarquiel.cuidaplusjpc.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
                    modifier = Modifier.size(100.dp)
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
            start = 24.dp,
            end = 24.dp
        )
        AddCitaScreenContent(navController, viewModel, grupoId, combinedPadding)
    }
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
        if (pacientes.isNotEmpty()) {
            SelectorElemento(
                titulo = "Selecciona un paciente",
                elementoActual = pacienteSeleccionado?.nombreCompleto ?: "",
                onAnterior = { if (indicePaciente > 0) indicePaciente-- },
                onSiguiente = { if (indicePaciente < pacientes.lastIndex) indicePaciente++ }
            )
        } else {
            Text("No hay pacientes disponibles", color = Color.Gray)
        }

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
                    fechaDate = try { formato.parse(it) } catch (e: Exception) { null }
                },
                label = { Text("Fecha") },
                modifier = Modifier.weight(1f),
                trailingIcon = {
                    IconButton(onClick = { datePicker.show() }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Seleccionar fecha", tint = colorResource(R.color.primario))
                    }
                },
                colors = defaultTextFieldColors()
            )

            OutlinedTextField(
                value = horaTexto,
                onValueChange = {
                    horaTexto = it
                    val formato = SimpleDateFormat("HH:mm", Locale.getDefault())
                    horaDate = try { formato.parse(it) } catch (e: Exception) { null }
                },
                label = { Text("Hora") },
                modifier = Modifier.weight(1f),
                trailingIcon = {
                    IconButton(onClick = { timePicker.show() }) {
                        Icon(Icons.Default.AccessTime, contentDescription = "Seleccionar hora", tint = colorResource(R.color.primario))
                    }
                },
                colors = defaultTextFieldColors()
            )
        }

        CampoTexto(valor = especialidad, onChange = { especialidad = it }, label = "Especialidad", modifier = Modifier.fillMaxWidth())
        CampoTexto(valor = medico, onChange = { medico = it }, label = "Médico", modifier = Modifier.fillMaxWidth())
        CampoTexto(valor = ubicacion, onChange = { ubicacion = it }, label = "Ubicación", modifier = Modifier.fillMaxWidth())
        CampoTexto(valor = motivo, onChange = { motivo = it }, label = "Motivo", modifier = Modifier.fillMaxWidth())
        CampoTexto(valor = observaciones, onChange = { observaciones = it }, label = "Observaciones", modifier = Modifier.fillMaxWidth())

        if (usuarios.isNotEmpty()) {
            SelectorElemento(
                titulo = "Selecciona el usuario que acude",
                elementoActual = usuarioSeleccionado?.nombre ?: "",
                onAnterior = { if (indiceUsuario > 0) indiceUsuario-- },
                onSiguiente = { if (indiceUsuario < usuarios.lastIndex) indiceUsuario++ }
            )
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
                val fechaHoraFinal = combinarFechaHora(fechaDate, horaDate)
                val cita = CitaMedica(
                    citaMedicaId = UUID.randomUUID().toString(),
                    grupoFamiliarId = viewModel.grupoVM.grupo.value?.grupoFamiliarId ?: "",
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

fun combinarFechaHora(fecha: Date?, hora: Date?): Date {
    val cal = Calendar.getInstance()
    fecha?.let { cal.time = it }
    hora?.let {
        val h = Calendar.getInstance().apply { time = it }
        cal.set(Calendar.HOUR_OF_DAY, h.get(Calendar.HOUR_OF_DAY))
        cal.set(Calendar.MINUTE, h.get(Calendar.MINUTE))
    }
    return cal.time
}

@Composable
fun defaultTextFieldColors() = OutlinedTextFieldDefaults.colors(
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

@Composable
fun CampoTexto(valor: String, onChange: (String) -> Unit, label: String, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = valor,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = modifier,
        colors = defaultTextFieldColors()
    )
}

@Composable
fun SelectorElemento(titulo: String, elementoActual: String, onAnterior: () -> Unit, onSiguiente: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(titulo, style = MaterialTheme.typography.titleMedium, color = colorResource(R.color.texto_principal))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onAnterior) {
                Icon(Icons.Default.ArrowBackIos, contentDescription = "Anterior")
            }
            Text(elementoActual, style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = onSiguiente) {
                Icon(Icons.Default.ArrowForwardIos, contentDescription = "Siguiente")
            }
        }
    }
}
