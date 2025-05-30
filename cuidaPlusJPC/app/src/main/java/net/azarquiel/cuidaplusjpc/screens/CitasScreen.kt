package net.azarquiel.cuidaplusjpc.screens

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
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
import net.azarquiel.cuidaplusjpc.model.CitaMedica
import net.azarquiel.cuidaplusjpc.viewmodel.CitaViewModel
import net.azarquiel.cuidaplusjpc.viewmodel.MainViewModel
import java.text.SimpleDateFormat

@Composable
fun CitasScreen(
    navController: NavHostController,
    viewModel: CitaViewModel,
    mainViewModel: MainViewModel,
    grupoId: String,
    padding: PaddingValues
) {
    val grupo = mainViewModel.grupoVM.grupo.observeAsState().value

    val citas: List<CitaMedica> = viewModel.citas

    var filtro by remember { mutableStateOf("") }
    val citasFiltradas = citas.filter {
        it.motivo.contains(filtro, ignoreCase = true)
                || it.medico.contains(filtro, ignoreCase = true)
    }

    LaunchedEffect(grupoId) {
        viewModel.cargarCitasPorGrupo(grupoId)
    }

    Scaffold(
        topBar = { CitasTopBar(grupo?.nombre ?: "") },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("addCita/$grupoId") },
                modifier = Modifier.padding(bottom = 56.dp),
                containerColor = colorResource(R.color.secundario),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir cita")
            }
        },
        containerColor = colorResource(R.color.fondo_claro)
    ) { innerPadding ->
        val combinedPadding = PaddingValues(
            top = padding.calculateTopPadding() + innerPadding.calculateTopPadding(),
            bottom = padding.calculateBottomPadding() + innerPadding.calculateBottomPadding(),
            start = 24.dp,
            end = 24.dp
        )
        CitasContent(citasFiltradas, filtro, onFiltroChange = { filtro = it }, padding = combinedPadding)
    }
}


@Composable
fun CitasTopBar(nombreGrupo: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 16.dp),
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
    padding: PaddingValues
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
            CitaCard(cita)
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun CitaCard(cita: CitaMedica) {
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm")
    val fechaTexto = formatter.format(cita.fechaHora)

    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 200), label = "scaleAnimCita"
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
                pressed = false
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.color_tarjeta))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("Fecha: $fechaTexto", fontWeight = FontWeight.Bold, color = colorResource(R.color.texto_principal))
            Text("Motivo: ${cita.motivo}", color = Color.DarkGray)
            Text("Médico: ${cita.medico}", color = Color.DarkGray)
            Text("Especialidad: ${cita.especialidad}", color = Color.DarkGray)
            Text("Ubicación: ${cita.ubicacion}", color = Color.DarkGray)
            Text("Realizada: ${if (cita.realizada) "Sí" else "No"}", color = Color.DarkGray)
        }
    }
}
