package net.azarquiel.cuidaplusjpc.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import net.azarquiel.cuidaplusjpc.R
import net.azarquiel.cuidaplusjpc.model.Paciente
import net.azarquiel.cuidaplusjpc.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PacientesScreen(
    navController: NavHostController,
    viewModel: MainViewModel,
    padding: PaddingValues
) {
    val pacientes = viewModel.pacienteVM.pacientes.observeAsState(emptyList())
    val grupo = viewModel.grupoVM.grupo.observeAsState().value

    var filtro by remember { mutableStateOf("") }

    val pacientesFiltrados = pacientes.value.filter {
        it.nombre.contains(filtro, ignoreCase = true)
    }

    Scaffold(
        containerColor = colorResource(R.color.fondo_claro)
    ) { innerPadding ->
        val combinedPadding = PaddingValues(
            top = padding.calculateTopPadding(),
            bottom = padding.calculateBottomPadding() + innerPadding.calculateBottomPadding(),
            start = 24.dp,
            end = 24.dp
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(combinedPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Cabecera
            Text(
                text = "Pacientes de ${grupo?.nombre ?: "Grupo"}",
                fontSize = 24.sp,
                color = colorResource(R.color.primario),
                fontWeight = FontWeight.Bold
            )

            // Buscador
            OutlinedTextField(
                value = filtro,
                onValueChange = { filtro = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar paciente...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") }
            )

            // Botón añadir
            Button(
                onClick = {
                    // navController.navigate("addPaciente")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.primario))
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir paciente", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Añadir paciente", color = Color.White)
            }

            // Lista de pacientes
            if (pacientesFiltrados.isEmpty()) {
                // Muestra card de ejemplo si no hay pacientes
                PacienteDetailCard(
                    Paciente(
                        nombre = "Ejemplo paciente",
                        fechaNacimiento = SimpleDateFormat("dd/MM/yyyy").parse("01/01/1960") ?: Date()
                    )
                )
            } else {
                pacientesFiltrados.forEach { paciente ->
                    PacienteDetailCard(paciente)
                }
            }

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

            Text(paciente.nombre, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("$edad años")

            Divider()

            Text("Medicación actual: (no implementado)")
            Text("Citas médicas: (no implementado)")
            Text("Tratamientos: (no implementado)")
            Text("Historial médico: (no implementado)")
        }
    }
}
