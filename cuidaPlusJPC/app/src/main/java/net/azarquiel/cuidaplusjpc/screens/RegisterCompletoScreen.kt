package net.azarquiel.cuidaplusjpc.screens

import Usuario
import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.azarquiel.cuidaplusjpc.R
import net.azarquiel.cuidaplusjpc.navigation.AppScreens
import net.azarquiel.cuidaplusjpc.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RegisterCompletoScreen(navController: NavHostController, viewModel: MainViewModel) {
    Scaffold(
        topBar = { RegisterCompletoTopBar() },
        containerColor = colorResource(R.color.fondo_claro)
    ) { padding ->
        RegisterCompletoContent(padding, viewModel, navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterCompletoTopBar() {
    TopAppBar(
        title = {
            // Columna para centrar logo y título uno debajo del otro
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Icon(
                    painter = painterResource(id = R.drawable.logosinfondo),
                    contentDescription = "Logo",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(top = 8.dp, bottom = 4.dp)
                )
                Text(
                    text = "Crear Usuario",
                    fontSize = 20.sp,
                    color = colorResource(R.color.texto_principal)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent // Fondo totalmente transparente
        )
    )
}

@Composable
fun RegisterCompletoContent(
    padding: PaddingValues,
    viewModel: MainViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val email = currentUser?.email ?: ""
    val uid = currentUser?.uid ?: return

    val calendar = remember { Calendar.getInstance() }
    val formato = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    // Estados para los campos del formulario
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var fechaNacimientoTexto by remember { mutableStateOf("") }
    var fechaNacimientoDate by remember { mutableStateOf<Date?>(null) }

    // DatePicker para seleccionar la fecha
    val datePicker = remember {
        DatePickerDialog(context, { _, year, month, day ->
            calendar.set(year, month, day, 0, 0, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            fechaNacimientoDate = calendar.time
            fechaNacimientoTexto = formato.format(calendar.time)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
    }

    // Opción de grupo: crear o unirse
    var opcionGrupo by remember { mutableStateOf("crear") }
    var nombreGrupo by remember { mutableStateOf("") }
    var nombreGrupoExistente by remember { mutableStateOf("") }
    var isGuardando by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.Top
    ) {
        // Email mostrado como campo solo lectura
        CampoSoloLectura(label = "Email", value = email)
        CampoTexto(label = "Nombre", value = nombre, onValueChange = { nombre = it })
        CampoTexto(
            label = "Teléfono",
            value = telefono,
            onValueChange = { telefono = it },
            keyboardType = KeyboardType.Number
        )

        // Fecha con campo editable + icono para abrir selector
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            CampoTexto(
                label = "Fecha de nacimiento",
                value = fechaNacimientoTexto,
                onValueChange = {
                    fechaNacimientoTexto = it
                    try {
                        fechaNacimientoDate = formato.parse(it)
                    } catch (_: Exception) {}
                },
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = { datePicker.show() }) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = colorResource(R.color.primario))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Selector de tipo de grupo
        Text("Selecciona una opción:", fontSize = 14.sp)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            RadioOpcion(text = "Crear grupo", seleccionado = opcionGrupo == "crear") { opcionGrupo = "crear" }
            RadioOpcion(text = "Unirse a grupo", seleccionado = opcionGrupo == "unirse") { opcionGrupo = "unirse" }
        }

        // Campo de nombre de grupo según opción
        if (opcionGrupo == "crear") {
            CampoTexto(label = "Nombre del grupo", value = nombreGrupo, onValueChange = { nombreGrupo = it })
        } else {
            CampoTexto(label = "Nombre del grupo", value = nombreGrupoExistente, onValueChange = { nombreGrupoExistente = it })
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de guardado principal
        Button(
            onClick = {
                if (isGuardando) return@Button

                val telefonoLong = telefono.toLongOrNull()
                if (nombre.isBlank() || telefonoLong == null || fechaNacimientoDate == null) {
                    Toast.makeText(context, "Rellena todos los campos correctamente", Toast.LENGTH_LONG).show()
                    return@Button
                }

                isGuardando = true

                // Creamos el objeto usuario para guardar en Firestore
                val usuario = Usuario(
                    usuarioId = uid,
                    nombre = nombre,
                    fechaNacimiento = fechaNacimientoDate!!,
                    email = email,
                    numTelefono = telefonoLong,
                    grupos = emptyList()
                )

                val onError: (String) -> Unit = {
                    isGuardando = false
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }

                val onSuccess: () -> Unit = {
                    coroutineScope.launch {
                        // Esperamos a que el usuario esté disponible en el ViewModel
                        viewModel.usuarioVM.empezarEscucha(uid)
                        while (viewModel.usuarioVM.usuario.value == null) {
                            delay(100)
                        }

                        // Si ya tiene grupo, cargamos todos los datos necesarios
                        val grupoId = viewModel.usuarioVM.usuario.value?.grupos?.firstOrNull()
                        if (!grupoId.isNullOrEmpty()) {
                            viewModel.grupoVM.cargarGrupo(grupoId)
                            while (viewModel.grupoVM.grupo.value == null) {
                                delay(100)
                            }
                            val grupo = viewModel.grupoVM.grupo.value!!
                            viewModel.usuarioVM.obtenerUsuariosPorIds(grupo.miembros)
                            viewModel.pacienteVM.cargarPacientesDelGrupo(grupo.grupoFamiliarId)
                        }

                        isGuardando = false

                        // Navegamos a la pantalla principal
                        navController.navigate("inicio") {
                            popUpTo(AppScreens.LoginUsuarioScreen.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }

                // Ejecutamos creación o unión al grupo según opción seleccionada
                if (opcionGrupo == "crear") {
                    viewModel.crearGrupoYUsuario(nombreGrupo, uid, usuario, onSuccess, onError)
                } else {
                    viewModel.unirseAGrupoPorNombre(nombreGrupoExistente, uid, usuario, onSuccess, onError)
                }
            },
            enabled = !isGuardando,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.primario),
                contentColor = colorResource(R.color.fondo_claro)
            )
        ) {
            Text("Guardar")
        }
    }
}

// Campo de texto normal reutilizable
@Composable
fun CampoTexto(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.padding(vertical = 4.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colorResource(R.color.primario),
            unfocusedBorderColor = colorResource(R.color.texto_principal),
            cursorColor = colorResource(R.color.primario),
            focusedLabelColor = colorResource(R.color.primario)
        )
    )
}


// Campo solo lectura (email)
@Composable
fun CampoSoloLectura(label: String, value: String) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        label = { Text(label) },
        enabled = false,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colorResource(R.color.primario),
            unfocusedBorderColor = colorResource(R.color.texto_principal),
            disabledLabelColor = colorResource(R.color.texto_principal),
            disabledBorderColor = colorResource(R.color.texto_principal),
            disabledTextColor = colorResource(R.color.texto_principal)
        )
    )
}

// Selector de opción tipo radio
@Composable
fun RadioOpcion(text: String, seleccionado: Boolean, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = seleccionado,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = colorResource(R.color.primario))
        )
        Text(text)
    }
}
