package net.azarquiel.cuidaplusjpc.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PacientesScreen(
    navController: NavHostController,
    viewModel: MainViewModel,
    padding: PaddingValues
) {
    val grupo = viewModel.grupoVM.grupo.observeAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Pacientes de ${grupo?.nombre ?: "Grupo"}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.primario)
                )
            )
        },
        containerColor = colorResource(R.color.fondo_claro)
    ) { innerPadding ->
        val combinedPadding = PaddingValues(
            top = padding.calculateTopPadding() + innerPadding.calculateTopPadding(),
            bottom = padding.calculateBottomPadding() + innerPadding.calculateBottomPadding(),
            start = 24.dp,
            end = 24.dp
        )

        PacientesScreenContent(
            viewModel = viewModel,
            navController = navController,
            padding = combinedPadding
        )
    }
}

@Composable
fun PacientesScreenContent(
    viewModel: MainViewModel,
    navController: NavHostController,
    padding: PaddingValues
) {
    val pacientes = viewModel.pacienteVM.pacientes.observeAsState(emptyList())
    var filtro by remember { mutableStateOf("") }

    val pacientesFiltrados = pacientes.value.filter {
        it.nombreCompleto.contains(filtro, ignoreCase = true)
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
                placeholder = { Text("Buscar paciente...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") }
            )
        }

        item {
            Button(
                onClick = { navController.navigate("addPaciente") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.primario))
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir paciente", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Añadir paciente", color = Color.White)
            }
        }

        items(pacientesFiltrados) { paciente ->
            PacienteDetailCard(paciente)
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun PacienteDetailCard(paciente: Paciente) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val edad = Calendar.getInstance().get(Calendar.YEAR) -
                    Calendar.getInstance().apply { time = paciente.fechaNacimiento }.get(Calendar.YEAR)

            Text(paciente.nombreCompleto, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("$edad años")
            Text("Dirección: ${paciente.direccion}")

            Divider()

            Text("Medicación actual: (no implementado)")
            Text("Citas médicas: (no implementado)")
            Text("Tratamientos: (no implementado)")
            Text("Historial médico: (no implementado)")
        }
    }
}
