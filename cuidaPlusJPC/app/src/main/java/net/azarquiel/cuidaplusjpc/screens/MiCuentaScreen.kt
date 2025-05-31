package net.azarquiel.cuidaplusjpc.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Place
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import net.azarquiel.cuidaplusjpc.R
import net.azarquiel.cuidaplusjpc.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MiCuentaScreen(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    val grupo by viewModel.grupoVM.grupo.observeAsState()

    Scaffold(
        topBar = {
            Box {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                }
                MiCuentaTopBar(grupoNombre = grupo?.nombre)
            }
        },
        containerColor = colorResource(R.color.fondo_claro)
    ) { innerPadding ->
        // Aquí aplicamos el innerPadding de la Scaffold
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            MiCuentaContent(viewModel)
        }
    }
}

@Composable
fun MiCuentaTopBar(grupoNombre: String?, logoRes: Int = R.drawable.logosinfondo) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(R.color.fondo_claro))
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(logoRes),
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

@SuppressLint("SimpleDateFormat")
@Composable
private fun MiCuentaContent(viewModel: MainViewModel) {
    val pacientes by viewModel.pacienteVM.pacientesDelGrupo.observeAsState(emptyList())
    val enfermedadesPorPaciente by viewModel.enfermedadPacienteVM.enfermedadesPorPaciente.observeAsState(emptyMap())
    val citas = viewModel.citaVM.citas
    val tratamientosPorEnfermedad = viewModel.tratamientoVM.tratamientosPorEnfermedad
    val medicamentosPorEnfermedad = viewModel.medicamentoVM.medicamentosPorEnfermedad

    // Cargas
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            // margen lateral y vertical sencillo
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text(
                text = "Pacientes",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.texto_principal)
            )
        }
        items(pacientes) { p ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.cardColors(containerColor = colorResource(R.color.color_tarjeta))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Nombre del paciente
                    Text(
                        text = p.nombreCompleto,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.primario),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // —— Enfermedades ——
                    enfermedadesPorPaciente[p.pacienteId]?.let { enfList ->
                        if (enfList.isNotEmpty()) {
                            InfoRow(
                                icon = Icons.Default.LocalHospital,
                                label = "Enfermedades",
                                iconTint = colorResource(R.color.primario)
                            )
                            enfList.forEach { e ->
                                Text(
                                    text = "• ${e.nombre} (${e.estado})",
                                    modifier = Modifier.padding(start = 32.dp, bottom = 4.dp)
                                )
                            }
                        }
                    }

                    // —— Tratamientos ——
                    enfermedadesPorPaciente[p.pacienteId]?.forEach { e ->
                        tratamientosPorEnfermedad[e.enfermedadPacienteId]?.let { trts ->
                            if (trts.isNotEmpty()) {
                                InfoRow(
                                    icon = Icons.Default.MedicalServices,
                                    label = "Tratamientos",
                                    iconTint = colorResource(R.color.secundario)
                                )
                                trts.forEach { t ->
                                    Text(
                                        text = "• ${t.nombre} (${t.tipo})",
                                        modifier = Modifier.padding(start = 32.dp, bottom = 4.dp)
                                    )
                                }
                            }
                        }

                        // —— Medicación ——
                        medicamentosPorEnfermedad[e.enfermedadPacienteId]?.let { meds ->
                            if (meds.isNotEmpty()) {
                                InfoRow(
                                    icon = Icons.Default.Medication,
                                    label = "Medicaciones",
                                    iconTint = colorResource(R.color.icono_secundario)
                                )
                                meds.forEach { m ->
                                    Text(
                                        text = "• ${m.nombre}, ${m.dosis}, cada ${m.frecuencia}",
                                        modifier = Modifier.padding(start = 32.dp, bottom = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    // —— Citas ——
                    val fmt = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    val citasPac = citas.filter { it.pacienteId == p.pacienteId }
                    if (citasPac.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        InfoRow(
                            icon = Icons.Default.Place,
                            label = "Citas",
                            iconTint = colorResource(R.color.terciario)
                        )
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
        item { Spacer(Modifier.height(80.dp)) } // espacio final sobre nav
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
