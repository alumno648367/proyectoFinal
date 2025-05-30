package net.azarquiel.cuidaplusjpc.screens

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
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
    padding: PaddingValues
) {
    val grupo = viewModel.grupoVM.grupo.observeAsState().value

    Scaffold(
        topBar = { PacientesTopBar(grupo?.nombre ?: "Grupo") },
        containerColor = colorResource(R.color.fondo_claro)
    ) { innerPadding ->
        val combinedPadding = PaddingValues(
            top = padding.calculateTopPadding() + innerPadding.calculateTopPadding(),
            bottom = padding.calculateBottomPadding() + innerPadding.calculateBottomPadding(),
            start = 24.dp,
            end = 24.dp
        )
        PacientesContent(viewModel, navController, combinedPadding)
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
fun PacientesContent(
    viewModel: MainViewModel,
    navController: NavHostController,
    padding: PaddingValues
) {
    val grupo = viewModel.grupoVM.grupo.observeAsState().value
    val pacientes by viewModel.pacienteVM.pacientesDelGrupo.observeAsState(emptyList())
    var filtro by remember { mutableStateOf("") }

    val pacientesFiltrados = pacientes.filter {
        it.nombreCompleto.contains(filtro, ignoreCase = true)
    }

    LaunchedEffect(grupo?.grupoFamiliarId) {
        grupo?.let {
            viewModel.pacienteVM.cargarPacientesDelGrupo(it.grupoFamiliarId)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            OutlinedTextField(
                value = filtro,
                onValueChange = { filtro = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder ={ Text("Buscar paciente...") },
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

        item {
            Button(
                onClick = { navController.navigate("addPaciente") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.secundario))
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir paciente", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Añadir paciente",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }

        items(pacientesFiltrados) { paciente ->
            PacienteCard(paciente, navController)
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun PacienteCard(
    paciente: Paciente,
    navController: NavHostController
) {
    val edad = Calendar.getInstance().get(Calendar.YEAR) -
            Calendar.getInstance().apply { time = paciente.fechaNacimiento }.get(Calendar.YEAR)

    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 200), label = "scaleAnim"
    )

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
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(paciente.nombreCompleto, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = colorResource(R.color.texto_principal))
            Text("$edad años", color = Color.DarkGray)
            Text("Dirección: ${paciente.direccion}", color = Color.DarkGray)
        }
    }
}
