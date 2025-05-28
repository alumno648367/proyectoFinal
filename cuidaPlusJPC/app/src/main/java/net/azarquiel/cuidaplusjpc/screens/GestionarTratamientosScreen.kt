package net.azarquiel.cuidaplusjpc.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
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

    var inicio by remember { mutableStateOf(Date()) }
    var fin by remember { mutableStateOf(Date()) }

    val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val inicioTexto = remember { mutableStateOf(formatoFecha.format(inicio)) }
    val finTexto = remember { mutableStateOf(formatoFecha.format(fin)) }

    val datePickerInicio = DatePickerDialog(
        context,
        { _, y, m, d ->
            val cal = Calendar.getInstance()
            cal.set(y, m, d)
            inicio = cal.time
            inicioTexto.value = formatoFecha.format(inicio)
        },
        Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH),
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    )

    val datePickerFin = DatePickerDialog(
        context,
        { _, y, m, d ->
            val cal = Calendar.getInstance()
            cal.set(y, m, d)
            fin = cal.time
            finTexto.value = formatoFecha.format(fin)
        },
        Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH),
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    )

    LaunchedEffect(true) {
        viewModel.tratamientoVM.cargarTratamientos(enfermedadPacienteId)
        viewModel.tratamientoMaestroVM.cargarTratamientos()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Tratamientos", color = MaterialTheme.colorScheme.onPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorResource(R.color.primario))
            )
        },
        containerColor = colorResource(R.color.fondo_claro)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Nuevo tratamiento", style = MaterialTheme.typography.titleMedium)

            var expandedTratamiento by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expandedTratamiento,
                onExpandedChange = { expandedTratamiento = !expandedTratamiento }
            ) {
                TextField(
                    value = tratamientoSeleccionado?.nombre ?: "",
                    onValueChange = {},
                    label = { Text("Tratamiento") },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTratamiento)
                    },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedTratamiento,
                    onDismissRequest = { expandedTratamiento = false }
                ) {
                    tratamientosMaestro.forEach { t ->
                        DropdownMenuItem(
                            text = { Text("${t.nombre} (${t.tipo})") },
                            onClick = {
                                tratamientoSeleccionado = t
                                expandedTratamiento = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = inicioTexto.value,
                    onValueChange = {},
                    label = { Text("Inicio") },
                    readOnly = true,
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = { datePickerInicio.show() }) {
                    Text("Elegir")
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = finTexto.value,
                    onValueChange = {},
                    label = { Text("Fin") },
                    readOnly = true,
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = { datePickerFin.show() }) {
                    Text("Elegir")
                }
            }

            Button(
                onClick = {
                    if (tratamientoSeleccionado != null) {
                        val nuevo = Tratamiento(
                            tratamientoId = UUID.randomUUID().toString(),
                            enfermedadPacienteId = enfermedadPacienteId,
                            nombre = tratamientoSeleccionado!!.nombre,
                            tipo = tratamientoSeleccionado!!.tipo,
                            inicio = inicio,
                            fin = fin,
                            descripcion = descripcion
                        )
                        viewModel.tratamientoVM.guardarTratamiento(nuevo)
                        viewModel.tratamientoVM.cargarTratamientos(enfermedadPacienteId)
                        tratamientoSeleccionado = null
                        descripcion = ""
                        Toast.makeText(context, "Tratamiento guardado", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.primario))
            ) {
                Text("Guardar", color = Color.White)
            }

            Divider()

            Text("Tratamientos añadidos", style = MaterialTheme.typography.titleMedium)

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(tratamientos) { tratamiento ->
                    Card {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Nombre: ${tratamiento.nombre}")
                            Text("Tipo: ${tratamiento.tipo}")
                            Text("Inicio: ${formatoFecha.format(tratamiento.inicio)}")
                            Text("Fin: ${formatoFecha.format(tratamiento.fin)}")
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
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
