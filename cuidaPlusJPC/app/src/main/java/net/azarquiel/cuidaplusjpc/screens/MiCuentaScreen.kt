package net.azarquiel.cuidaplusjpc.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import net.azarquiel.cuidaplusjpc.R
import net.azarquiel.cuidaplusjpc.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MiCuentaScreen(navController: NavHostController, viewModel: MainViewModel) {
    val grupo by viewModel.grupoVM.grupo.observeAsState()

    Scaffold(
    ) { padding ->
        // Pasamos el topPadding para respetar el espacio del sistema
        MiCuentaContent(
            viewModel = viewModel,
            grupoNombre = grupo?.nombre,
            paddingTop = padding.calculateTopPadding()
        )
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun MiCuentaContent(viewModel: MainViewModel, grupoNombre: String?, paddingTop: Dp) {
    val pacientes by viewModel.pacienteVM.pacientesDelGrupo.observeAsState(emptyList())
    val enfermedadesPorPaciente by viewModel.enfermedadPacienteVM.enfermedadesPorPaciente.observeAsState(emptyMap())
    val citas = viewModel.citaVM.citas
    val tratamientosPorEnfermedad = viewModel.tratamientoVM.tratamientosPorEnfermedad
    val medicamentosPorEnfermedad = viewModel.medicamentoVM.medicamentosPorEnfermedad

    // Carga de datos al entrar en la pantalla
    LaunchedEffect(viewModel.grupoVM.grupo.value?.grupoFamiliarId) {
        viewModel.grupoVM.grupo.value?.let {
            viewModel.pacienteVM.escucharPacientesDelGrupo(it.grupoFamiliarId)
            viewModel.citaVM.cargarCitasPorGrupo(it.grupoFamiliarId)
        }
    }
    LaunchedEffect(pacientes) {
        if (pacientes.isNotEmpty())
            viewModel.enfermedadPacienteVM.cargarEnfermedadesParaPacientes(pacientes)
    }
    LaunchedEffect(enfermedadesPorPaciente) {
        enfermedadesPorPaciente.values.flatten().forEach {
            viewModel.tratamientoVM.cargarTratamientos(it.enfermedadPacienteId)
            viewModel.medicamentoVM.cargarMedicamentos(it.enfermedadPacienteId)
        }
    }

    // Scroll general con padding lateral y superior
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.fondo_claro))
            .padding(horizontal = 24.dp)
            .padding(top = paddingTop),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Top visual con el logo y nombre del grupo
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.logosinfondo),
                    contentDescription = "Logo Cuida+",
                    modifier = Modifier.size(120.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = grupoNombre.orEmpty(),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.texto_principal)
                )
            }
        }

        // Título sección
        item {
            Text(
                text = "Pacientes",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.texto_principal)
            )
        }

        // Tarjetas por cada paciente
        items(pacientes) { p ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.cardColors(containerColor = colorResource(R.color.color_tarjeta))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = p.nombreCompleto,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.primario),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Enfermedades
                    enfermedadesPorPaciente[p.pacienteId]?.let { enfList ->
                        if (enfList.isNotEmpty()) {
                            InfoRow(Icons.Default.LocalHospital, "Enfermedades", colorResource(R.color.primario))
                            enfList.forEach { e ->
                                Text(
                                    text = "• ${e.nombre} (${e.estado})",
                                    modifier = Modifier.padding(start = 32.dp, bottom = 4.dp)
                                )
                            }
                        }
                    }

                    // Tratamientos
                    enfermedadesPorPaciente[p.pacienteId]?.forEach { e ->
                        tratamientosPorEnfermedad[e.enfermedadPacienteId]?.let { trts ->
                            if (trts.isNotEmpty()) {
                                InfoRow(Icons.Default.MedicalServices, "Tratamientos", colorResource(R.color.secundario))
                                trts.forEach { t ->
                                    Text(
                                        text = "• ${t.nombre} (${t.tipo})",
                                        modifier = Modifier.padding(start = 32.dp, bottom = 4.dp)
                                    )
                                }
                            }
                        }

                        // Medicamentos
                        medicamentosPorEnfermedad[e.enfermedadPacienteId]?.let { meds ->
                            if (meds.isNotEmpty()) {
                                InfoRow(Icons.Default.Medication, "Medicaciones", colorResource(R.color.icono_secundario))
                                meds.forEach { m ->
                                    Text(
                                        text = "• ${m.nombre}, ${m.dosis}, cada ${m.frecuencia}",
                                        modifier = Modifier.padding(start = 32.dp, bottom = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Citas
                    val fmt = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    val citasPac = citas.filter { it.pacienteId == p.pacienteId }
                    if (citasPac.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        InfoRow(Icons.Default.Place, "Citas", colorResource(R.color.terciario))
                        citasPac.forEach {
                            Text(
                                text = "• ${fmt.format(it.fechaHora)}: ${it.especialidad} con ${it.medico}",
                                modifier = Modifier.padding(start = 32.dp, bottom = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Espacio final para no chocar con bottom nav
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun InfoRow(
    icon: ImageVector,
    label: String,
    iconTint: Color,
    labelColor: Color = iconTint,
    spacer: Dp = 8.dp
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(icon, contentDescription = null, tint = iconTint)
        Spacer(Modifier.width(spacer))
        Text(label, fontWeight = FontWeight.SemiBold, color = labelColor)
    }
}
