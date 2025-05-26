package net.azarquiel.cuidaplusjpc.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import net.azarquiel.cuidaplusjpc.R
import net.azarquiel.cuidaplusjpc.model.Enfermedad
import net.azarquiel.cuidaplusjpc.model.EnfermedadPaciente
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionarEnfermedadesScreen(
    pacienteId: String,
    enfermedades: List<Enfermedad>,
    enfermedadesAsignadas: List<EnfermedadPaciente>,
    onGuardar: (EnfermedadPaciente) -> Unit,
    onEliminar: (EnfermedadPaciente) -> Unit,
    navController: NavHostController
) {
    var categoriaSeleccionada by remember { mutableStateOf("") }
    var enfermedadSeleccionada by remember { mutableStateOf<Enfermedad?>(null) }
    var estado by remember { mutableStateOf("Activa") }
    var observaciones by remember { mutableStateOf("") }

    val categorias = enfermedades.map { it.categoria }.distinct()
    val enfermedadesFiltradas = enfermedades.filter { it.categoria == categoriaSeleccionada }

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
            ExposedDropdownMenuBox(
                expanded = false,
                onExpandedChange = {}
            ) {
                var expanded by remember { mutableStateOf(false) }
                TextField(
                    value = categoriaSeleccionada,
                    onValueChange = {},
                    label = { Text("Categoría") },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categorias.forEach { categoria ->
                        DropdownMenuItem(
                            text = { Text(categoria) },
                            onClick = {
                                categoriaSeleccionada = categoria
                                enfermedadSeleccionada = null
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Dropdown enfermedad
            ExposedDropdownMenuBox(
                expanded = false,
                onExpandedChange = {}
            ) {
                var expanded by remember { mutableStateOf(false) }
                TextField(
                    value = enfermedadSeleccionada?.nombre ?: "",
                    onValueChange = {},
                    label = { Text("Enfermedad") },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    enfermedadesFiltradas.forEach { enfermedad ->
                        DropdownMenuItem(
                            text = { Text(enfermedad.nombre) },
                            onClick = {
                                enfermedadSeleccionada = enfermedad
                                expanded = false
                            }
                        )
                    }
                }
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
                            fechaDiagnostico = Date().toString(),
                            estado = estado,
                            observaciones = observaciones
                        )
                        onGuardar(nuevaRelacion)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.primario))
            ) {
                Text("Guardar enfermedad", color = Color.White)
            }

            Divider()

            Text("Enfermedades asignadas", style = MaterialTheme.typography.titleMedium)

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(enfermedadesAsignadas) { ep ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("ID enfermedad: ${ep.enfermedadId}")
                            Text("Estado: ${ep.estado}")
                            Text("Observaciones: ${ep.observaciones}")
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(onClick = { onEliminar(ep) }) {
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
