package net.azarquiel.cuidaplusjpc.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import net.azarquiel.cuidaplusjpc.R
import net.azarquiel.cuidaplusjpc.model.CitaMedica
import net.azarquiel.cuidaplusjpc.viewmodel.CitaViewModel
import net.azarquiel.cuidaplusjpc.viewmodel.MainViewModel
import java.text.SimpleDateFormat

@Composable
fun CitasScreen(
    navController: NavHostController,
    citaViewModel: CitaViewModel,
    viewModel: MainViewModel,
    grupoId: String,
    padding: PaddingValues = PaddingValues()
) {
    val grupo = viewModel.grupoVM.grupo.observeAsState().value

    val citas: List<CitaMedica> = citaViewModel.citas

    var filtro by remember { mutableStateOf("") }
    val citasFiltradas = citas.filter {
        val texto = filtro.trim().lowercase()
            it.motivo.lowercase().contains(texto) ||
            it.medico.lowercase().contains(texto) ||
            it.ubicacion.lowercase().contains(texto) ||
            it.especialidad.lowercase().contains(texto) ||
            it.usuarioAcompananteNombre.lowercase().contains(texto)
    }


    LaunchedEffect(grupoId) {
        citaViewModel.cargarCitasPorGrupo(grupoId)
    }

    Scaffold(
        topBar = { CitasTopBar(grupo?.nombre ?: "") },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("addCita/$grupoId") },
                containerColor = colorResource(R.color.secundario),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir cita")
            }
        },
        containerColor = colorResource(R.color.fondo_claro)
    ) { innerPadding ->
        CitasContent(
            citas = citasFiltradas,
            filtro = filtro,
            onFiltroChange = { filtro = it },
            padding = PaddingValues(
                top = innerPadding.calculateTopPadding(),
                start = 24.dp,
                end = 24.dp
            ),
            viewModel = viewModel
        )
    }

}


@Composable
fun CitasTopBar(nombreGrupo: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = null,
            tint = colorResource(R.color.primario),
            modifier = Modifier
                .size(100.dp)
                .padding(top = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Citas de $nombreGrupo",
            color = colorResource(R.color.texto_principal),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CitasContent(
    citas: List<CitaMedica>,
    filtro: String,
    onFiltroChange: (String) -> Unit,
    padding: PaddingValues,
    viewModel: MainViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            OutlinedTextField(
                value = filtro,
                onValueChange = onFiltroChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar cita...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(R.color.primario),
                    unfocusedBorderColor = colorResource(R.color.secundario),
                    cursorColor = colorResource(R.color.primario),
                    focusedLeadingIconColor = colorResource(R.color.primario),
                    unfocusedLeadingIconColor = colorResource(R.color.secundario),
                    focusedPlaceholderColor = Color.Gray,
                    unfocusedPlaceholderColor = Color.Gray,
                    focusedTextColor = colorResource(R.color.texto_principal),
                    unfocusedTextColor = colorResource(R.color.texto_principal)
                )
            )
        }

        items(citas) { cita ->
            CitaCard(cita,viewModel)
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun CitaCard(cita: CitaMedica, viewModel: MainViewModel) {
    val paciente = viewModel.pacienteVM.pacientesDelGrupo.value?.find { it.pacienteId == cita.pacienteId }
    val context = LocalContext.current
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm")
    val fechaTexto = formatter.format(cita.fechaHora)

    val alpha = if (cita.realizada) 0.4f else 1f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.color_tarjeta))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = paciente?.nombreCompleto?.uppercase() ?: "PACIENTE DESCONOCIDO",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.texto_principal),
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .padding(bottom = 8.dp),
                letterSpacing = 1.5.sp,
            )

            Text(
                text = fechaTexto,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.primario)
            )
            Spacer(modifier = Modifier.height(8.dp))

            InfoRow(Icons.Default.Person, "Acompañante", cita.usuarioAcompananteNombre)
            InfoRow(Icons.Default.Description, "Motivo", cita.motivo)
            InfoRow(Icons.Default.MedicalServices, "Médico", cita.medico)
            InfoRow(Icons.Default.LocalHospital, "Especialidad", cita.especialidad)
            InfoRow(Icons.Default.Place, "Ubicación", cita.ubicacion)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Realizada",
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.texto_principal)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = cita.realizada,
                        onCheckedChange = { nuevoEstado ->
                            viewModel.citaVM.actualizarEstadoCita(
                                citaId = cita.citaMedicaId,
                                realizada = nuevoEstado,
                                onSuccess = {
                                    Toast.makeText(context, "Estado actualizado", Toast.LENGTH_SHORT).show()
                                },
                                onFailure = {
                                    Toast.makeText(context, "Error al actualizar", Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = colorResource(R.color.primario),
                            checkedTrackColor = colorResource(R.color.primario).copy(alpha = 0.5f),
                            uncheckedThumbColor = colorResource(R.color.fondo_claro),
                            uncheckedTrackColor = colorResource(R.color.secundario).copy(alpha = 0.5f)
                        )
                    )
                }

                IconButton(onClick = {
                    viewModel.citaVM.eliminarCita(
                        cita.citaMedicaId,
                        onSuccess = {
                            Toast.makeText(context, "Cita eliminada", Toast.LENGTH_SHORT).show()
                        },
                        onFailure = {
                            Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show()
                        }
                    )
                }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar cita",
                        tint = colorResource(R.color.primario)
                    )
                }
            }

        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, valor: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Icon(icon, contentDescription = null, tint = colorResource(R.color.primario))
        Text("$label:", fontWeight = FontWeight.Bold)
        Text(valor, color = Color.DarkGray)
    }
}

