package net.azarquiel.cuidaplusjpc.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import net.azarquiel.cuidaplusjpc.R
import net.azarquiel.cuidaplusjpc.navigation.AppScreens
import net.azarquiel.cuidaplusjpc.viewmodel.MainViewModel

@Composable
fun HomeScreen(navController: NavHostController, viewModel: MainViewModel) {
    Scaffold(
        containerColor = colorResource(R.color.fondo_claro),
    ) { padding ->
        HomeScreenContent(padding, navController)
    }
}

@Composable
fun HomeScreenContent(padding: PaddingValues, navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 32.dp, vertical = 48.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Logo con animación de entrada
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + scaleIn()
        ) {
            Image(
                painter = painterResource(id = R.drawable.logosinletras),
                contentDescription = "Logo Cuida+",
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
            )
        }

        // Nombre de la app + lema
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Cuida+",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.texto_principal)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Cuidamos a los que más importan",
                fontSize = 18.sp,
                color = colorResource(R.color.texto_principal),
                textAlign = TextAlign.Center
            )
        }

        // Botón para empezar registro
        Button(
            onClick = { navController.navigate(AppScreens.RegisterUsuarioScreen.route) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.primario),
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = MaterialTheme.shapes.large
        ) {
            Text(
                text = "Empezar",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Enlace inferior para usuarios ya registrados
        Text(
            text = "¿Ya tienes una cuenta? Inicia sesión",
            modifier = Modifier
                .padding(top = 16.dp)
                .clickable { navController.navigate(AppScreens.LoginUsuarioScreen.route) },
            color = colorResource(R.color.texto_principal),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}
