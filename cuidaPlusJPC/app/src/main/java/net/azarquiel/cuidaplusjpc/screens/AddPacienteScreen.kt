package net.azarquiel.cuidaplusjpc.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
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
import net.azarquiel.cuidaplusjpc.model.Paciente
import net.azarquiel.cuidaplusjpc.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPacienteScreen(
    navController: NavHostController,
    viewModel: MainViewModel,
    padding: PaddingValues
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Añadir paciente", color = Color.White) },
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
        AddPacienteScreenContent(viewModel, navController, combinedPadding)
    }
}

@Composable
fun AddPacienteScreenContent(
    viewModel: MainViewModel,
    navController: NavHostController,
    padding: PaddingValues
) {
    val grupo = viewModel.grupoVM.grupo.observeAsState().value
    val context = LocalContext.current

    var nombreCompleto by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }

    var fechaNacimientoTexto by remember { mutableStateOf("") }
    var fechaNacimientoDate by remember { mutableStateOf<Date?>(null) }

    val calendar = Calendar.getInstance()
    val datePicker = DatePickerDialog(
        context,
        { _, year, month, day ->
            calendar.set(year, month, day, 0, 0, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            fechaNacimientoDate = calendar.time
            val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            fechaNacimientoTexto = formato.format(fechaNacimientoDate!!)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        OutlinedTextField(
            value = nombreCompleto,
            onValueChange = { nombreCompleto = it },
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = direccion,
            onValueChange = { direccion = it },
            label = { Text("Dirección") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            OutlinedTextField(
                value = fechaNacimientoTexto,
                onValueChange = {
                    fechaNacimientoTexto = it
                    val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    try {
                        fechaNacimientoDate = formato.parse(it)
                    } catch (e: Exception) {
                        fechaNacimientoDate = null
                    }
                }
                ,
                label = { Text("Fecha de nacimiento") },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(R.color.primario),
                    unfocusedBorderColor = colorResource(R.color.texto_principal),
                    cursorColor = colorResource(R.color.primario),
                    focusedLabelColor = colorResource(R.color.primario)
                )
            )
            IconButton(onClick = { datePicker.show() }) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Seleccionar fecha",
                    tint = colorResource(R.color.primario)
                )
            }
        }

        // Botón guardar
        Button(
            onClick = {
                if (nombreCompleto.isBlank() || direccion.isBlank() || fechaNacimientoDate == null || grupo == null) {
                    Toast.makeText(context, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val paciente = Paciente(
                    pacienteId = UUID.randomUUID().toString(),
                    grupoFamiliarId = grupo.grupoFamiliarId,
                    nombreGrupo = grupo.nombre,
                    nombreCompleto = nombreCompleto,
                    direccion = direccion,
                    fechaNacimiento = fechaNacimientoDate!!
                )

                viewModel.pacienteVM.guardarPaciente(
                    paciente,
                    onSuccess = {
                        Toast.makeText(context, "Paciente guardado", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    },
                    onFailure = {
                        Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                    }
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.primario))
        ) {
            Icon(Icons.Default.Add, contentDescription = "Guardar", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Guardar", color = Color.White)
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
