package net.azarquiel.cuidaplusjpc.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import net.azarquiel.cuidaplusjpc.R
import net.azarquiel.cuidaplusjpc.model.Paciente
import net.azarquiel.cuidaplusjpc.viewmodel.MainViewModel
import java.util.*

@Composable
fun PacientesScreen(
    navController: NavHostController,
    viewModel: MainViewModel,
    padding: PaddingValues = PaddingValues()
) {
    val grupo = viewModel.grupoVM.grupo.observeAsState().value
    var filtro by remember { mutableStateOf("") }

    val paddingTop = padding.calculateTopPadding()

    // Scaffold principal con TopBar y fondo de pantalla
    Scaffold(
        containerColor = colorResource(R.color.fondo_claro),
        topBar = { PacientesTopBar(grupo?.nombre ?: "Grupo") }
    ) { innerPadding ->
        // Contenido principal con filtro, botón y lista
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingTop + innerPadding.calculateTopPadding(),
                    start = 24.dp,
                    end = 24.dp
                )
        ) {
            // Campo de búsqueda
            OutlinedTextField(
                value = filtro,
                onValueChange = { filtro = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                placeholder = { Text("Buscar paciente...") },
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

            // Botón para añadir nuevo paciente
            Button(
                onClick = { navController.navigate("addPaciente") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.secundario))
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir paciente", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Añadir paciente", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
            }

            // Lista de pacientes con scroll
            PacientesLista(viewModel, navController, filtro, bottomPadding = padding.calculateBottomPadding())
        }
    }
}

@Composable
fun PacientesTopBar(nombreGrupo: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.MedicalServices,
            contentDescription = null,
            tint = colorResource(R.color.primario),
            modifier = Modifier
                .size(100.dp)
                .padding(top = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Pacientes de $nombreGrupo",
            color = colorResource(R.color.texto_principal),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PacientesLista(
    viewModel: MainViewModel,
    navController: NavHostController,
    filtro: String,
    bottomPadding: Dp
) {
    val grupo = viewModel.grupoVM.grupo.observeAsState().value
    val pacientes by viewModel.pacienteVM.pacientesDelGrupo.observeAsState(emptyList())

    // Carga inicial de pacientes si hay grupo
    LaunchedEffect(grupo?.grupoFamiliarId) {
        grupo?.let {
            viewModel.pacienteVM.cargarPacientesDelGrupo(it.grupoFamiliarId)
        }
    }

    val pacientesFiltrados = pacientes.filter {
        it.nombreCompleto.contains(filtro, ignoreCase = true) ||
                it.direccion.contains(filtro, ignoreCase = true)
    }

    // Lista con padding inferior ajustado para evitar solapamiento
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = bottomPadding + 80.dp)
    ) {
        if (pacientes.isEmpty()) {
            item {
                Text(
                    text = "Aún no hay pacientes registrados.",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    fontWeight = FontWeight.Medium
                )
            }
        }
        items(pacientesFiltrados) { paciente ->
            PacienteCard(paciente, navController,viewModel)
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun PacienteCard(
    paciente: Paciente,
    navController: NavHostController,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    // Cálculo de edad basado en el año actual
    val edad = Calendar.getInstance().get(Calendar.YEAR) -
            Calendar.getInstance().apply { time = paciente.fechaNacimiento }.get(Calendar.YEAR)

    // Animación de escala al pulsar
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 200), label = "scaleAnim"
    )

    // Tarjeta individual del paciente
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                pressed = true
                navController.navigate("detailPaciente/${paciente.pacienteId}")
                pressed = false
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.color_tarjeta))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = colorResource(R.color.primario), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(paciente.nombreCompleto, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Cake, contentDescription = null, tint = colorResource(R.color.secundario), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("$edad años", fontSize = 16.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Place, contentDescription = null, tint = colorResource(R.color.terciario), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Dirección: ${paciente.direccion}", fontSize = 16.sp)
                }
            }

            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Eliminar paciente",
                tint = Color.Black,
                modifier = Modifier
                    .size(28.dp)
                    .clickable {
                        viewModel.pacienteVM.eliminarPaciente(
                            pacienteId = paciente.pacienteId,
                            onSuccess = {
                                Toast.makeText(context, "Paciente eliminado", Toast.LENGTH_SHORT).show()
                            },
                            onFailure = {
                                Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
            )
        }
    }
}