package net.azarquiel.cuidaplusjpc.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
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
import net.azarquiel.cuidaplusjpc.model.Tratamiento
import net.azarquiel.cuidaplusjpc.model.TratamientoMaestro
import net.azarquiel.cuidaplusjpc.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionarTratamientosScreen(
    enfermedadPacienteId: String,
    viewModel: MainViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    val tratamientosMap = viewModel.tratamientoVM.tratamientosPorEnfermedad
    val tratamientos = tratamientosMap[enfermedadPacienteId] ?: emptyList()
    val tratamientosMaestro by viewModel.tratamientoMaestroVM.tratamientos.observeAsState(emptyList())

    var tratamientoSeleccionado by remember { mutableStateOf<TratamientoMaestro?>(null) }
    var descripcion by remember { mutableStateOf("") }

    val calendar = remember { Calendar.getInstance() }
    val formato = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    var inicioTexto by remember { mutableStateOf("") }
    var finTexto by remember { mutableStateOf("") }
    var inicioDate by remember { mutableStateOf<Date?>(null) }
    var finDate by remember { mutableStateOf<Date?>(null) }

    val datePickerInicio = remember {
        DatePickerDialog(context, { _, year, month, day ->
            val cal = Calendar.getInstance()
            cal.set(year, month, day, 0, 0, 0)
            cal.set(Calendar.MILLISECOND, 0)
            inicioDate = cal.time
            inicioTexto = formato.format(cal.time)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
    }

    val datePickerFin = remember {
        DatePickerDialog(context, { _, year, month, day ->
            val cal = Calendar.getInstance()
            cal.set(year, month, day, 0, 0, 0)
            cal.set(Calendar.MILLISECOND, 0)
            finDate = cal.time
            finTexto = formato.format(cal.time)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
    }

    LaunchedEffect(true) {
        viewModel.tratamientoVM.cargarTratamientos(enfermedadPacienteId)
        viewModel.tratamientoMaestroVM.cargarTratamientos()
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = colorResource(R.color.secundario),
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Gestionar Tratamientos",
                    color = colorResource(R.color.texto_principal),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        containerColor = colorResource(R.color.fondo_claro)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                // Solo aplicamos el padding superior para no incluir el bottom
                .padding(top = innerPadding.calculateTopPadding())
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Nuevo tratamiento", style = MaterialTheme.typography.titleMedium)

            var expandedTratamiento by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expandedTratamiento,
                onExpandedChange = { expandedTratamiento = !expandedTratamiento }
            ) {
                OutlinedTextField(
                    value = tratamientoSeleccionado?.nombre ?: "",
                    onValueChange = {},
                    label = { Text("Tratamiento") },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTratamiento)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorResource(R.color.primario),
                        unfocusedBorderColor = colorResource(R.color.texto_principal),
                        cursorColor = colorResource(R.color.primario),
                        focusedLabelColor = colorResource(R.color.primario)
                    )
                )
                ExposedDropdownMenu(
                    expanded = expandedTratamiento,
                    onDismissRequest = { expandedTratamiento = false },
                    modifier = Modifier
                        .background(Color.White)
                ) {
                    tratamientosMaestro.forEach { t ->
                        DropdownMenuItem(
                            text = { Text("${t.nombre} (${t.tipo})") },
                            onClick = {
                                tratamientoSeleccionado = t
                                expandedTratamiento = false
                            },
                            modifier = Modifier
                                .background(Color.White)
                        )
                    }
                }
            }

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(R.color.primario),
                    unfocusedBorderColor = colorResource(R.color.texto_principal),
                    cursorColor = colorResource(R.color.primario),
                    focusedLabelColor = colorResource(R.color.primario)
                )
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = inicioTexto,
                    onValueChange = {
                        inicioTexto = it
                        try { inicioDate = formato.parse(it) } catch (_: Exception) {}
                    },
                    label = { Text("Inicio") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorResource(R.color.primario),
                        unfocusedBorderColor = colorResource(R.color.texto_principal),
                        cursorColor = colorResource(R.color.primario),
                        focusedLabelColor = colorResource(R.color.primario)
                    )
                )
                IconButton(onClick = { datePickerInicio.show() }) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = colorResource(R.color.primario)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = finTexto,
                    onValueChange = {
                        finTexto = it
                        try { finDate = formato.parse(it) } catch (_: Exception) {}
                    },
                    label = { Text("Fin") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorResource(R.color.primario),
                        unfocusedBorderColor = colorResource(R.color.texto_principal),
                        cursorColor = colorResource(R.color.primario),
                        focusedLabelColor = colorResource(R.color.primario)
                    )
                )
                IconButton(onClick = { datePickerFin.show() }) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = colorResource(R.color.primario)
                    )
                }
            }

            Button(
                onClick = {
                    if (tratamientoSeleccionado != null && inicioDate != null && finDate != null) {
                        val nuevo = Tratamiento(
                            tratamientoId = UUID.randomUUID().toString(),
                            enfermedadPacienteId = enfermedadPacienteId,
                            nombre = tratamientoSeleccionado!!.nombre,
                            tipo = tratamientoSeleccionado!!.tipo,
                            inicio = inicioDate!!,
                            fin = finDate!!,
                            descripcion = descripcion
                        )
                        viewModel.tratamientoVM.guardarTratamiento(nuevo)
                        viewModel.tratamientoVM.cargarTratamientos(enfermedadPacienteId)
                        tratamientoSeleccionado = null
                        descripcion = ""
                        inicioTexto = ""
                        finTexto = ""
                        inicioDate = null
                        finDate = null
                        Toast.makeText(context, "Tratamiento guardado", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.primario))
            ) {
                Text("Guardar", color = Color.White)
            }

            Divider(thickness = 1.dp, color = Color.LightGray)

            Text("Tratamientos añadidos", style = MaterialTheme.typography.titleMedium)

            tratamientos.forEach { tratamiento ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colorResource(R.color.color_tarjeta)),
                    elevation = CardDefaults.cardElevation(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "${tratamiento.nombre} (${tratamiento.tipo})",
                            fontWeight = FontWeight.Bold
                        )
                        Text("Inicio: ${formato.format(tratamiento.inicio)}")
                        Text("Fin: ${formato.format(tratamiento.fin)}")
                        Text("Descripción: ${tratamiento.descripcion}")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(onClick = {
                                viewModel.tratamientoVM.eliminarTratamiento(tratamiento.tratamientoId)
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                            }
                        }
                    }
                }
            }

            // Espacio fijo para que no pise el BottomNav
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}
