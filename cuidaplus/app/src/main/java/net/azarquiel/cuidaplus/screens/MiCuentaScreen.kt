package net.azarquiel.cuidaplus.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import net.azarquiel.cuidaplus.R
import net.azarquiel.cuidaplus.navigation.AppScreens
import net.azarquiel.cuidaplus.viewmodel.MainViewModel


@Composable
fun MiCuentaScreen(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    Scaffold(
        topBar = { MiCuentaTopBar(viewModel) },
        containerColor = colorResource(R.color.fondo_claro),
        content = { padding ->
            MiCuentaContent(padding, navController, viewModel)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiCuentaTopBar(viewModel: MainViewModel) {
    val usuario = viewModel.usuarioVM.usuario.observeAsState()
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mi cuenta",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = usuario.value?.nombre ?: "",
                    fontSize = 16.sp,
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
fun MiCuentaContent(
    padding: PaddingValues,
    navController: NavHostController,
    viewModel: MainViewModel
) {
    val usuario = viewModel.usuarioVM.usuario.observeAsState()

    usuario.value?.let { user ->
        println("Usuario cargado: ${user.nombre}")
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Datos personales",
                fontSize = 18.sp,
                color = colorResource(R.color.texto_principal),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Email: ${user.email}", fontSize = 16.sp, color = colorResource(R.color.texto_principal))
            Text("Teléfono: ${user.numTelefono}", fontSize = 16.sp, color = colorResource(R.color.texto_principal))

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Grupos familiares",
                fontSize = 18.sp,
                color = colorResource(R.color.texto_principal),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(user.grupos.size) { i ->
                    val grupoId = user.grupos[i]
                    GrupoCard(grupoId, navController)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { /* TODO: lógica para unirse a grupo */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.secundario),
                    contentColor = colorResource(R.color.texto_principal)
                )
            ) {
                Text("Unirse a nuevo grupo", fontSize = 16.sp)
            }
            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(AppScreens.HomeScreen.route) {
                        popUpTo(0) // limpia todo el backstack
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.primario),
                    contentColor = colorResource(R.color.texto_principal)
                )
            ) {
                Text("Cerrar sesión", fontSize = 16.sp)
            }

        }
    } ?: run {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = colorResource(R.color.primario))
        }
    }
}

@Composable
fun GrupoCard(grupoId: String, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable {
                navController.navigate("GrupoFamiliarScreen/$grupoId")
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.color_tarjeta),
            contentColor = colorResource(R.color.texto_principal)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Text(
            text = "Grupo ID: $grupoId", // más adelante se carga el nombre real
            modifier = Modifier.padding(16.dp),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}
