package net.azarquiel.cuidaplus.screens

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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import net.azarquiel.cuidaplus.R
import net.azarquiel.cuidaplus.model.Enfermedad
import net.azarquiel.cuidaplus.model.EnfermedadPaciente
import net.azarquiel.cuidaplus.viewmodel.MainViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionarEnfermedadesScreen(
    pacienteId: String,
    navController: NavHostController,
    viewModel: MainViewModel
) {
    LaunchedEffect(true) {
        viewModel.enfermedadVM.cargarEnfermedades()
        viewModel.enfermedadPacienteVM.cargarPorPaciente(pacienteId)
    }

    val enfermedades by viewModel.enfermedadVM.enfermedades.observeAsState(emptyList())
    val relaciones by viewModel.enfermedadPacienteVM.relaciones.observeAsState(emptyList())

    var categoriaSeleccionada by remember { mutableStateOf("-- Selecciona categoría --") }
    var enfermedadSeleccionada by remember { mutableStateOf<Enfermedad?>(null) }
    var estado by remember { mutableStateOf("Activa") }
    var observaciones by remember { mutableStateOf("") }

    val categorias = remember(enfermedades) {
        listOf("-- Selecciona categoría --") + enfermedades.map { it.categoria.trim() }.distinct().sorted()
    }

    val enfermedadesFiltradas = remember(enfermedades, categoriaSeleccionada) {
        if (categoriaSeleccionada == "-- Selecciona categoría --" || categoriaSeleccionada.isBlank()) {
            emptyList()
        } else {
            enfermedades.filter {
                it.categoria.trim().lowercase() == categoriaSeleccionada.trim().lowercase()
            }
        }
    }

    LaunchedEffect(enfermedades) {
        if (enfermedades.isNotEmpty() && categoriaSeleccionada == "-- Selecciona categoría --") {
            categoriaSeleccionada = enfermedades.first().categoria.trim()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Enfermedades", color = MaterialTheme.colorScheme.onPrimary) },
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
            // Dropdown categoría
            var expandedCategoria by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expandedCategoria,
                onExpandedChange = { expandedCategoria = !expandedCategoria }
            ) {
                TextField(
                    value = categoriaSeleccionada,
                    onValueChange = {},
                    label = { Text("Categoría") },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedCategoria,
                    onDismissRequest = { expandedCategoria = false }
                ) {
                    categorias.forEach { categoria ->
                        DropdownMenuItem(
                            text = { Text(categoria) },
                            onClick = {
                                categoriaSeleccionada = categoria
                                enfermedadSeleccionada = null
                                expandedCategoria = false
                            }
                        )
                    }
                }
            }

            // Dropdown enfermedad
            var expandedEnfermedad by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expandedEnfermedad,
                onExpandedChange = { expandedEnfermedad = !expandedEnfermedad }
            ) {
                TextField(
                    value = enfermedadSeleccionada?.nombre ?: "",
                    onValueChange = {},
                    label = { Text("Enfermedad") },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEnfermedad)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedEnfermedad,
                    onDismissRequest = { expandedEnfermedad = false }
                ) {
                    enfermedadesFiltradas.forEach { enfermedad ->
                        DropdownMenuItem(
                            text = { Text(enfermedad.nombre) },
                            onClick = {
                                enfermedadSeleccionada = enfermedad
                                expandedEnfermedad = false
                            }
                        )
                    }
                }
            }

            if (enfermedadesFiltradas.isEmpty() && categoriaSeleccionada != "-- Selecciona categoría --") {
                Text("No hay enfermedades en esta categoría", color = Color.Gray)
            }

            OutlinedTextField(
                value = estado,
                onValueChange = { estado = it },
                label = { Text("Estado") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = observaciones,
                onValueChange = { observaciones = it },
                label = { Text("Observaciones") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    enfermedadSeleccionada?.let {
                        val nuevaRelacion = EnfermedadPaciente(
                            enfermedadPacienteId = UUID.randomUUID().toString(),
                            pacienteId = pacienteId,
                            enfermedadId = it.enfermedadId,
                            nombre = it.nombre,                     // GUARDAMOS NOMBRE
                            categoria = it.categoria,               // GUARDAMOS CATEGORÍA
                            fechaDiagnostico = Date().toString(),
                            estado = estado,
                            observaciones = observaciones
                        )

                        viewModel.enfermedadPacienteVM.guardarRelacion(nuevaRelacion)
                        viewModel.pacienteVM.actualizarEnfermedadesDelPaciente(pacienteId)
                    }
                },
                enabled = enfermedadSeleccionada != null,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.primario))
            ) {
                Text("Guardar enfermedad", color = Color.White)
            }

            Divider()

            Text("Enfermedades asignadas", style = MaterialTheme.typography.titleMedium)

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(relaciones) { ep ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Enfermedad: ${ep.nombre}")
                            Text("Categoría: ${ep.categoria}")
                            Text("Estado: ${ep.estado}")
                            Text("Observaciones: ${ep.observaciones}")
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(onClick = {
                                    viewModel.enfermedadPacienteVM.eliminarRelacion(ep.enfermedadPacienteId)
                                    viewModel.pacienteVM.actualizarEnfermedadesDelPaciente(pacienteId)
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
