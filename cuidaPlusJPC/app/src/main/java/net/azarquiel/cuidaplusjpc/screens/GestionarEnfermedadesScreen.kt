package net.azarquiel.cuidaplusjpc.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import net.azarquiel.cuidaplusjpc.R
import net.azarquiel.cuidaplusjpc.model.Enfermedad
import net.azarquiel.cuidaplusjpc.model.EnfermedadPaciente
import net.azarquiel.cuidaplusjpc.viewmodel.MainViewModel
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
        if (categoriaSeleccionada == "-- Selecciona categoría --") emptyList()
        else enfermedades.filter { it.categoria.trim().equals(categoriaSeleccionada.trim(), true) }
    }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.MedicalServices, contentDescription = null, tint = colorResource(R.color.primario), modifier = Modifier.size(100.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Gestionar Enfermedades", color = colorResource(R.color.texto_principal), fontSize = 22.sp, style = MaterialTheme.typography.titleLarge)
            }
        },
        containerColor = colorResource(R.color.fondo_claro)
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding(), start = 24.dp, end = 24.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedCategoria) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = defaultEnfermedadTextFieldColors()
                )
                ExposedDropdownMenu(expandedCategoria, onDismissRequest = { expandedCategoria = false }, modifier = Modifier.background(Color.White)) {
                    categorias.forEach { categoria ->
                        DropdownMenuItem(
                            text = { Text(categoria) },
                            onClick = {
                                categoriaSeleccionada = categoria
                                enfermedadSeleccionada = null
                                expandedCategoria = false
                            },
                            modifier = Modifier.background(colorResource(R.color.white))
                        )
                    }
                }
            }

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
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedEnfermedad) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = defaultEnfermedadTextFieldColors()
                )
                ExposedDropdownMenu(expandedEnfermedad, onDismissRequest = { expandedEnfermedad = false }, modifier = Modifier.background(Color.White)) {
                    enfermedadesFiltradas.forEach { enf ->
                        DropdownMenuItem(
                            text = { Text(enf.nombre) },
                            onClick = {
                                enfermedadSeleccionada = enf
                                expandedEnfermedad = false
                            },
                            modifier = Modifier.background(colorResource(R.color.white))
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
                modifier = Modifier.fillMaxWidth(),
                colors = defaultEnfermedadTextFieldColors()
            )

            OutlinedTextField(
                value = observaciones,
                onValueChange = { observaciones = it },
                label = { Text("Observaciones") },
                modifier = Modifier.fillMaxWidth(),
                colors = defaultEnfermedadTextFieldColors()
            )

            Button(
                onClick = {
                    enfermedadSeleccionada?.let { enfermedad ->
                        val yaExiste = relaciones.any { it.enfermedadId == enfermedad.enfermedadId }
                        if (yaExiste) {
                            Toast.makeText(context, "La enfermedad ya está asignada", Toast.LENGTH_SHORT).show()
                        } else {
                            val nuevaRelacion = EnfermedadPaciente(
                                enfermedadPacienteId = UUID.randomUUID().toString(),
                                pacienteId = pacienteId,
                                enfermedadId = enfermedad.enfermedadId,
                                nombre = enfermedad.nombre,
                                categoria = enfermedad.categoria,
                                fechaDiagnostico = Date().toString(),
                                estado = estado,
                                observaciones = observaciones
                            )
                            viewModel.enfermedadPacienteVM.guardarRelacion(nuevaRelacion)
                            viewModel.pacienteVM.actualizarEnfermedadesDelPaciente(pacienteId)
                            Toast.makeText(context, "Enfermedad guardada", Toast.LENGTH_SHORT).show()
                            enfermedadSeleccionada = null
                            estado = "Activa"
                            observaciones = ""
                        }
                    }
                },
                enabled = enfermedadSeleccionada != null,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.primario))
            ) {
                Text("Guardar enfermedad", color = Color.White)
            }

            Divider(thickness = 1.dp, color = Color.LightGray)

            Text("Enfermedades asignadas", style = MaterialTheme.typography.titleMedium)

            relaciones.forEach { ep ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colorResource(R.color.color_tarjeta)),
                    elevation = CardDefaults.cardElevation(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Enfermedad: ${ep.nombre}")
                        Text("Categoría: ${ep.categoria}")
                        Text("Estado: ${ep.estado}")
                        Text("Observaciones: ${ep.observaciones}")
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
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

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun defaultEnfermedadTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = colorResource(R.color.primario),
    unfocusedBorderColor = colorResource(R.color.texto_principal),
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
