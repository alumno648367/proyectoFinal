package net.azarquiel.cuidaplusjpc.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import net.azarquiel.cuidaplusjpc.R
import net.azarquiel.cuidaplusjpc.navigation.AppScreens
import net.azarquiel.cuidaplusjpc.viewmodel.MainViewModel

@Composable
fun RegisterUsuarioScreen(navController: NavHostController, viewModel: MainViewModel) {
    RegisterUsuarioContent(navController, viewModel)
}

@Composable
fun RegisterUsuarioContent(navController: NavHostController, viewModel: MainViewModel) {
    val context = LocalContext.current
    val clientId = stringResource(id = R.string.default_web_client_id)

    // Configuración para iniciar sesión con Google
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    // Resultado del intent de Google Sign-In
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            // Si todo va bien, usamos el token para registrarnos en Firebase
            viewModel.registroConGoogle(
                account.idToken ?: "",
                onSuccess = {
                    Toast.makeText(context, "Sesión iniciada con Google", Toast.LENGTH_SHORT).show()
                    navController.navigate(AppScreens.RegisterCompletoScreen.route)
                },
                onFailure = {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            )
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Estados del formulario
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showEmailError by remember { mutableStateOf(false) }
    var showPasswordError by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    // Diseño principal centrado
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Cabecera
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Usuario",
            tint = colorResource(R.color.primario),
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Crear una cuenta",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.texto_principal)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Campo de email
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                showEmailError = false
            },
            label = { Text("Email") },
            isError = showEmailError,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(R.color.primario),
                unfocusedBorderColor = colorResource(R.color.texto_principal),
                cursorColor = colorResource(R.color.primario),
                focusedLabelColor = colorResource(R.color.primario)
            )
        )

        // Campo de contraseña con icono para mostrar/ocultar
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                showPasswordError = false
            },
            label = { Text("Contraseña") },
            isError = showPasswordError,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = icon, contentDescription = "Mostrar/Ocultar")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(R.color.primario),
                unfocusedBorderColor = colorResource(R.color.texto_principal),
                cursorColor = colorResource(R.color.primario),
                focusedLabelColor = colorResource(R.color.primario)
            )
        )

        // Aviso de términos legales
        Text(
            text = "Al registrarte, aceptas la Política de privacidad y los Términos de servicio",
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Botón de registro con email y contraseña
        Button(
            onClick = {
                val emailOk = email.isNotBlank()
                val passwordOk = password.isNotBlank()

                showEmailError = !emailOk
                showPasswordError = !passwordOk

                if (emailOk && passwordOk) {
                    viewModel.registroConEmail(
                        email, password,
                        onSuccess = {
                            Toast.makeText(context, "Usuario creado", Toast.LENGTH_SHORT).show()
                            navController.navigate(AppScreens.RegisterCompletoScreen.route)
                        },
                        onFailure = {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    Toast.makeText(context, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.primario),
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Crear cuenta", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Botón de Google Sign-In
        Button(
            onClick = {
                val signInIntent = googleSignInClient.signInIntent
                launcher.launch(signInIntent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = colorResource(R.color.texto_principal)
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.google),
                contentDescription = "Google",
                tint = Color.Unspecified,
                modifier = Modifier.size(35.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Continuar con Google", fontSize = 16.sp)
        }
    }
}
