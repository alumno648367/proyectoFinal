package net.azarquiel.cuidaplusjpc.screens

import Usuario
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
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
fun GrupoScreen(navController: NavHostController, viewModel: MainViewModel) {
    val grupo = viewModel.grupoVM.grupo.observeAsState().value

    LaunchedEffect(grupo?.grupoFamiliarId) {
        grupo?.let {
            viewModel.usuarioVM.obtenerUsuariosPorIds(it.miembros)
            viewModel.pacienteVM.cargarPacientesDelGrupo(it.grupoFamiliarId)
        }
    }

    Scaffold(
        topBar = { GrupoTopBar(grupo?.nombre ?: "GRUPO") },
        containerColor = colorResource(R.color.fondo_claro)
    ) { padding ->
        GrupoContent(padding, viewModel, navController)
    }
}

@Composable
fun GrupoTopBar(nombreGrupo: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(R.color.fondo_claro))
            .padding(top = 24.dp, bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Group,
            contentDescription = null,
            tint = colorResource(R.color.primario),
            modifier = Modifier
                .size(100.dp)
                .padding(top = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Grupo: ${nombreGrupo}",
            color = colorResource(R.color.texto_principal),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
fun GrupoContent(
    padding: PaddingValues,
    viewModel: MainViewModel,
    navController: NavHostController
) {
    val usuarioActual = viewModel.usuarioVM.usuario.observeAsState().value
    val usuarios by viewModel.usuarioVM.usuariosGrupo.observeAsState(emptyList())
    val pacientes by viewModel.pacienteVM.pacientesDelGrupo.observeAsState(emptyList())
    val contexto = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .padding(padding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        item {
            SeccionTitulo("MIEMBROS")
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                usuarios.forEach {
                    CardUsuario(it, usuarioActual, navController, contexto)
                }
                if (usuarios.isEmpty()) Text("No hay miembros.", color = Color.Gray)
            }
        }

        item {
            SeccionTitulo("PACIENTES")
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                pacientes.forEach {
                    CardPaciente(it, navController)
                }
                if (pacientes.isEmpty()) Text("No hay pacientes.", color = Color.Gray)
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun SeccionTitulo(texto: String) {
    Text(
        text = texto,
        fontSize = 20.sp,
        fontWeight = FontWeight.ExtraBold,
        color = colorResource(R.color.texto_principal),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .shadow(2.dp),
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )
}

@Composable
fun CardUsuario(
    usuario: Usuario,
    usuarioActual: Usuario?,
    navController: NavHostController,
    contexto: Context
) {
    val esActual = usuario.usuarioId == usuarioActual?.usuarioId
    val bgColor by animateColorAsState(
        targetValue = if (esActual) colorResource(R.color.usuario_actual_tarjeta) else colorResource(R.color.color_tarjeta),
        label = "CardUserColor"
    )
    val textColor = if (esActual) Color.White else colorResource(R.color.texto_principal)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                if (esActual) {
                    navController.navigate("perfil")
                } else {
                    Toast.makeText(contexto, "Solo puedes ver tu propio perfil", Toast.LENGTH_SHORT).show()
                }
            },
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (esActual) Icons.Default.Star else Icons.Default.Person,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(usuario.nombre, fontWeight = FontWeight.Bold, color = textColor)
                Text(usuario.email, color = textColor.copy(alpha = 0.8f))
            }
        }
    }
}

@Composable
fun CardPaciente(
    paciente: Paciente,
    navController: NavHostController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("detailPaciente/${paciente.pacienteId}") },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(paciente.nombreCompleto, fontWeight = FontWeight.Bold)
            Text(paciente.direccion, fontSize = 14.sp)
            Text(
                "Nacimiento: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(paciente.fechaNacimiento)}",
                fontSize = 13.sp
            )
        }
    }
}
