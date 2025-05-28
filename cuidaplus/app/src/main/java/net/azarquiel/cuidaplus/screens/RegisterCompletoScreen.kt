package net.azarquiel.cuidaplus.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import net.azarquiel.cuidaplus.R
import net.azarquiel.cuidaplus.model.Usuario
import net.azarquiel.cuidaplus.navigation.AppScreens
import net.azarquiel.cuidaplus.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RegisterCompletoScreen(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    Scaffold(
        topBar = { RegisterCompletoTopBar() },
        containerColor = colorResource(R.color.fondo_claro),
        content = { padding ->
            RegisterCompletoContent(padding, viewModel, navController)
        }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterCompletoTopBar() {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.logosinfondo),
                    contentDescription = "Logo",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(end = 8.dp)
                )
                Text(
                    text = "Crear Usuario",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(R.color.primario)
        )
    )
}

@Composable
fun RegisterCompletoContent(
    padding: PaddingValues,
    viewModel: MainViewModel,
    navController: NavHostController
) {
    val usuarioVM = viewModel.usuarioVM
    val grupoVM = viewModel.grupoVM
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    val email = currentUser?.email ?: ""
    val uid = currentUser?.uid ?: return

    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
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

    var opcionGrupo by remember { mutableStateOf("crear") }
    var nombreGrupo by remember { mutableStateOf("") }
    var nombreGrupoExistente by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = {},
            label = { Text("Email") },
            enabled = false,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(R.color.primario),
                unfocusedBorderColor = colorResource(R.color.texto_principal),
                cursorColor = colorResource(R.color.primario),
                focusedLabelColor = colorResource(R.color.primario)
            )
        )
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(R.color.primario),
                unfocusedBorderColor = colorResource(R.color.texto_principal),
                cursorColor = colorResource(R.color.primario),
                focusedLabelColor = colorResource(R.color.primario)
            )
        )
        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(R.color.primario),
                unfocusedBorderColor = colorResource(R.color.texto_principal),
                cursorColor = colorResource(R.color.primario),
                focusedLabelColor = colorResource(R.color.primario)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

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

        Spacer(modifier = Modifier.height(8.dp))

        Text("Selecciona una opción:", fontSize = 14.sp)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = opcionGrupo == "crear",
                    onClick = { opcionGrupo = "crear" },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = colorResource(R.color.primario)
                    )
                )
                Text("Crear grupo")
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = opcionGrupo == "unirse",
                    onClick = { opcionGrupo = "unirse" },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = colorResource(R.color.primario)
                    )
                )
                Text("Unirse a grupo")
            }
        }


        if (opcionGrupo == "crear") {
            OutlinedTextField(
                value = nombreGrupo,
                onValueChange = { nombreGrupo = it },
                label = { Text("Nombre del grupo") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(R.color.primario),
                    unfocusedBorderColor = colorResource(R.color.texto_principal),
                    cursorColor = colorResource(R.color.primario),
                    focusedLabelColor = colorResource(R.color.primario)
                )
            )
        } else {
            OutlinedTextField(
                value = nombreGrupoExistente,
                onValueChange = { nombreGrupoExistente = it },
                label = { Text("Nombre del grupo") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(R.color.primario),
                    unfocusedBorderColor = colorResource(R.color.texto_principal),
                    cursorColor = colorResource(R.color.primario),
                    focusedLabelColor = colorResource(R.color.primario)
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        var isGuardando by remember { mutableStateOf(false) }
        Button(
            onClick = {
                if (isGuardando) return@Button
                val telefonoLong = telefono.toLongOrNull()
                if (nombre.isBlank() || telefonoLong == null || fechaNacimientoDate == null) {
                    Toast.makeText(
                        context,
                        "Rellena todos los campos correctamente",
                        Toast.LENGTH_LONG
                    ).show()
                    return@Button
                }

                isGuardando = true
                val usuario = Usuario(
                    usuarioId = uid,
                    nombre = nombre,
                    email = email,
                    numTelefono = telefonoLong,
                    fechaNacimiento = fechaNacimientoDate!!
                )

                if (opcionGrupo == "crear") {
                    viewModel.crearGrupoYUsuario(nombreGrupo, uid, usuario,
                        onSuccess = {
                            isGuardando = false
                            Toast.makeText(context, "Usuario y grupo creados", Toast.LENGTH_SHORT)
                                .show()
                            viewModel.usuarioVM.empezarEscucha(uid)
                             navController.navigate(AppScreens.MainScreen.route)
                        },
                        onFailure = {
                            isGuardando = false
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    viewModel.unirseAGrupoPorNombre(nombreGrupoExistente, uid, usuario,
                        onSuccess = {
                            isGuardando = false
                            Toast.makeText(context, "Usuario unido al grupo", Toast.LENGTH_SHORT).show()
                            viewModel.usuarioVM.empezarEscucha(uid)
                            navController.navigate(AppScreens.MainScreen.route)
                        },
                        onFailure = {
                            isGuardando = false
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    )
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