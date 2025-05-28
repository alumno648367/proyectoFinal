package net.azarquiel.cuidaplus.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import net.azarquiel.cuidaplus.R
import net.azarquiel.cuidaplus.model.Medicamento
import net.azarquiel.cuidaplus.viewmodel.MainViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionarMedicacionScreen(
    enfermedadPacienteId: String,
    navController: NavHostController,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val medicamentos by remember(enfermedadPacienteId) {
        derivedStateOf {
            viewModel.medicamentoVM.medicamentosPorEnfermedad[enfermedadPacienteId] ?: emptyList()
        }
    }
    val listaMedicamentosMaestro = viewModel.medicamentoMaestroVM.medicamentos.observeAsState(emptyList()).value
    val listaDosis = viewModel.medicamentoVM.dosisDisponibles
    val listaFrecuencias = viewModel.medicamentoVM.frecuenciasDisponibles
    val listaVias = viewModel.medicamentoVM.viasAdministracion

    var medicamentoSeleccionado by rememberSaveable { mutableStateOf("") }
    var dosisSeleccionada by rememberSaveable { mutableStateOf("") }
    var frecuenciaSeleccionada by rememberSaveable { mutableStateOf("") }
    var viaSeleccionada by rememberSaveable { mutableStateOf("") }
    var observaciones by rememberSaveable { mutableStateOf("") }

    var menuAbierto by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(enfermedadPacienteId) {
        viewModel.medicamentoVM.cargarMedicamentos(enfermedadPacienteId)
        viewModel.medicamentoMaestroVM.cargarMedicamentos()
    }

    LaunchedEffect(Unit) {
        snapshotFlow { navController.currentBackStackEntry }
            .filterNotNull()
            .collectLatest {
                menuAbierto = ""
            }
    }

    BackHandler {
        menuAbierto = ""
        navController.popBackStack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Medicación", color = MaterialTheme.colorScheme.onPrimary) },
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
            Text("Nuevo medicamento", style = MaterialTheme.typography.titleMedium)

            DropdownField("Medicamento", medicamentoSeleccionado, listaMedicamentosMaestro.map { it.nombre }, menuAbierto) {
                medicamentoSeleccionado = it
                menuAbierto = ""
            }.also { if (menuAbierto != "medicamento") menuAbierto = "" else menuAbierto = "medicamento" }

            DropdownField("Dosis", dosisSeleccionada, listaDosis, menuAbierto) {
                dosisSeleccionada = it
                menuAbierto = ""
            }.also { if (menuAbierto != "dosis") menuAbierto = "" else menuAbierto = "dosis" }

            DropdownField("Frecuencia", frecuenciaSeleccionada, listaFrecuencias, menuAbierto) {
                frecuenciaSeleccionada = it
                menuAbierto = ""
            }.also { if (menuAbierto != "frecuencia") menuAbierto = "" else menuAbierto = "frecuencia" }

            DropdownField("Vía de administración", viaSeleccionada, listaVias, menuAbierto) {
                viaSeleccionada = it
                menuAbierto = ""
            }.also { if (menuAbierto != "via") menuAbierto = "" else menuAbierto = "via" }

            OutlinedTextField(
                value = observaciones,
                onValueChange = { observaciones = it },
                label = { Text("Observaciones") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (medicamentoSeleccionado.isNotBlank()) {
                        val nuevo = Medicamento(
                            medicamentoId = UUID.randomUUID().toString(),
                            enfermedadPacienteId = enfermedadPacienteId,
                            nombre = medicamentoSeleccionado,
                            dosis = dosisSeleccionada,
                            frecuencia = frecuenciaSeleccionada,
                            viaAdministracion = viaSeleccionada,
                            observaciones = observaciones
                        )
                        viewModel.medicamentoVM.guardarMedicamento(nuevo)
                        viewModel.medicamentoVM.cargarMedicamentos(enfermedadPacienteId)

                        medicamentoSeleccionado = ""
                        dosisSeleccionada = ""
                        frecuenciaSeleccionada = ""
                        viaSeleccionada = ""
                        observaciones = ""
                        menuAbierto = ""

                        Toast.makeText(context, "Medicamento guardado", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.primario))
            ) {
                Text("Guardar", color = Color.White)
            }

            Divider()
            Text("Medicamentos añadidos", style = MaterialTheme.typography.titleMedium)

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(medicamentos, key = { it.medicamentoId }) { medicamento ->
                    Card {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Nombre: ${medicamento.nombre}")
                            Text("Dosis: ${medicamento.dosis}")
                            Text("Frecuencia: ${medicamento.frecuencia}")
                            Text("Vía: ${medicamento.viaAdministracion}")
                            if (medicamento.observaciones.isNotBlank())
                                Text("Obs: ${medicamento.observaciones}")
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(onClick = {
                                    viewModel.medicamentoVM.eliminarMedicamento(medicamento.medicamentoId)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    selected: String,
    options: List<String>,
    menuAbierto: String,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = menuAbierto == label,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        TextField(
            value = selected,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt) },
                    onClick = {
                        onSelect(opt)
                        expanded = false
                    }
                )
            }
        }
    }
}
