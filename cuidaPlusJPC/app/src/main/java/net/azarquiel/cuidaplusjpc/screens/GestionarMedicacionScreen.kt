package net.azarquiel.cuidaplusjpc.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import net.azarquiel.cuidaplusjpc.R
import net.azarquiel.cuidaplusjpc.model.Medicamento
import net.azarquiel.cuidaplusjpc.viewmodel.MainViewModel
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
            .collectLatest { menuAbierto = "" }
    }

    BackHandler {
        menuAbierto = ""
        navController.popBackStack()
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
                    Icons.Default.MedicalServices,
                    contentDescription = null,
                    tint = colorResource(R.color.secundario),
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Gestionar Medicación",
                    color = colorResource(R.color.texto_principal),
                    fontSize = 22.sp
                )
            }
        },
        containerColor = colorResource(R.color.fondo_claro)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                // Solo aplicamos el padding superior, no el bottom
                .padding(top = innerPadding.calculateTopPadding())
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Nuevo medicamento", style = MaterialTheme.typography.titleMedium)

            DropdownMedicacion("Medicamento", medicamentoSeleccionado,
                listaMedicamentosMaestro.map { it.nombre }, menuAbierto) {
                medicamentoSeleccionado = it
                menuAbierto = ""
            }.also {
                if (menuAbierto != "medicamento") menuAbierto = ""
                else menuAbierto = "medicamento"
            }

            DropdownMedicacion("Dosis", dosisSeleccionada, listaDosis, menuAbierto) {
                dosisSeleccionada = it
                menuAbierto = ""
            }.also {
                if (menuAbierto != "dosis") menuAbierto = ""
                else menuAbierto = "dosis"
            }

            DropdownMedicacion("Frecuencia", frecuenciaSeleccionada, listaFrecuencias, menuAbierto) {
                frecuenciaSeleccionada = it
                menuAbierto = ""
            }.also {
                if (menuAbierto != "frecuencia") menuAbierto = ""
                else menuAbierto = "frecuencia"
            }

            DropdownMedicacion("Vía de administración", viaSeleccionada, listaVias, menuAbierto) {
                viaSeleccionada = it
                menuAbierto = ""
            }.also {
                if (menuAbierto != "via") menuAbierto = ""
                else menuAbierto = "via"
            }

            OutlinedTextField(
                value = observaciones,
                onValueChange = { observaciones = it },
                label = { Text("Observaciones") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(R.color.primario),
                    unfocusedBorderColor = colorResource(R.color.texto_principal),
                    cursorColor = colorResource(R.color.primario),
                    focusedLabelColor = colorResource(R.color.primario)
                )
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

            Divider(thickness = 1.dp, color = Color.LightGray)
            Text("Medicamentos añadidos", style = MaterialTheme.typography.titleMedium)

            medicamentos.forEach { medicamento ->
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
                        Text("Nombre: ${medicamento.nombre}")
                        Text("Dosis: ${medicamento.dosis}")
                        Text("Frecuencia: ${medicamento.frecuencia}")
                        Text("Vía: ${medicamento.viaAdministracion}")
                        if (medicamento.observaciones.isNotBlank())
                            Text("Observación: ${medicamento.observaciones}")
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

            // Espacio fijo para no solapar el BottomNav
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMedicacion(
    label: String,
    selected: String,
    options: List<String>,
    menuAbierto: String,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = menuAbierto == label,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
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
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.White)
        ) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt) },
                    onClick = {
                        onSelect(opt)
                        expanded = false
                    },
                    modifier = Modifier.background(colorResource(R.color.white))

                )
            }
        }
    }
}
