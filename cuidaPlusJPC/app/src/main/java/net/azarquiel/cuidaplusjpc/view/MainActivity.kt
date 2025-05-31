package net.azarquiel.cuidaplusjpc.view

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.google.firebase.auth.FirebaseAuth
import net.azarquiel.cuidaplusjpc.navigation.*
import net.azarquiel.cuidaplusjpc.screens.*
import net.azarquiel.cuidaplusjpc.ui.theme.CuidaPlusJPCTheme
import net.azarquiel.cuidaplusjpc.viewmodel.MainViewModel
import androidx.compose.material3.*

class MainActivity : ComponentActivity() {
    private val auth = FirebaseAuth.getInstance()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = MainViewModel(this)
        enableEdgeToEdge()

        setContent {
            CuidaPlusJPCTheme {
                val navController = rememberNavController()
                val isUserLoggedIn = remember { mutableStateOf(auth.currentUser != null) }

                LaunchedEffect(Unit) {
                    auth.addAuthStateListener {
                        isUserLoggedIn.value = it.currentUser != null
                    }
                }

                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route
                val grupoFamiliarId: String? = backStackEntry?.arguments?.getString("grupoFamiliarId")

                // Ocultar menú solo en estas rutas
                val noBottomBarRoutes = listOf(
                    AppScreens.SplashScreen.route,
                    AppScreens.HomeScreen.route,
                    AppScreens.RegisterUsuarioScreen.route,
                    AppScreens.RegisterCompletoScreen.route,
                    AppScreens.LoginUsuarioScreen.route
                )

                val showBottomBar = currentRoute != null && currentRoute !in noBottomBarRoutes

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavigationBar(navController, grupoFamiliarId)
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = AppScreens.SplashScreen.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // Login y registro (sin menú inferior)
                        composable(AppScreens.SplashScreen.route) {
                            SplashScreen(navController, viewModel)
                        }
                        composable(AppScreens.HomeScreen.route) {
                            HomeScreen(navController, viewModel)
                        }
                        composable(AppScreens.RegisterUsuarioScreen.route) {
                            RegisterUsuarioScreen(navController, viewModel)
                        }
                        composable(AppScreens.RegisterCompletoScreen.route) {
                            RegisterCompletoScreen(navController, viewModel)
                        }
                        composable(AppScreens.LoginUsuarioScreen.route) {
                            LoginUsuarioScreen(navController, viewModel)
                        }

                        // Pantallas principales (con menú)
                        composable("inicio") {
                            MiCuentaScreen(navController, viewModel)
                        }
                        composable("perfil") {
                            PerfilScreen(navController, viewModel)
                        }
                        composable("grupo") {
                            GrupoScreen(navController, viewModel)
                        }
                        composable("pacientes") {
                            PacientesScreen(navController, viewModel)
                        }
                        composable("citas/{grupoFamiliarId}") { backStackEntry ->
                            val grupoId = backStackEntry.arguments?.getString("grupoFamiliarId") ?: return@composable
                            CitasScreen(navController, viewModel.citaVM, viewModel, grupoId)
                        }

                        // Pantallas secundarias (también con menú si lo deseas)
                        composable("addCita/{grupoFamiliarId}") { backStackEntry ->
                            val grupoId = backStackEntry.arguments?.getString("grupoFamiliarId") ?: return@composable
                            AddCitaScreen(navController, viewModel, grupoId)
                        }
                        composable("addPaciente") {
                            AddPacienteScreen(navController, viewModel)
                        }
                        composable("detailPaciente/{pacienteId}") { backStackEntry ->
                            val pacienteId = backStackEntry.arguments?.getString("pacienteId") ?: return@composable
                            PacienteDetailScreen(pacienteId,viewModel, navController)
                        }
                        composable("gestionarEnfermedades/{pacienteId}") { backStackEntry ->
                            val pacienteId = backStackEntry.arguments?.getString("pacienteId") ?: return@composable
                            GestionarEnfermedadesScreen(pacienteId, navController, viewModel)
                        }
                        composable("gestionarTratamientos/{enfermedadPacienteId}") { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("enfermedadPacienteId") ?: return@composable
                            GestionarTratamientosScreen(id,viewModel, navController)
                        }
                        composable("gestionarMedicacion/{enfermedadPacienteId}") { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("enfermedadPacienteId") ?: return@composable
                            GestionarMedicacionScreen(id, navController, viewModel)
                        }
                    }
                }
            }
        }
    }
}
