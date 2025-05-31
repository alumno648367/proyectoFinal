package net.azarquiel.cuidaplusjpc.view

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.navigation.compose.rememberNavController
import net.azarquiel.cuidaplusjpc.navigation.AppNavigation
import net.azarquiel.cuidaplusjpc.ui.theme.CuidaPlusJPCTheme
import net.azarquiel.cuidaplusjpc.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    // Requiere Android O o superior por el uso de funciones específicas (como fechas)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // ViewModel centralizado con acceso a todos los sub-viewmodels y repositorios
        val viewModel = MainViewModel(this)
        setContent {
            CuidaPlusJPCTheme {
                // Controlador de navegación para Compose
                val navController = rememberNavController()
                // Componente de navegación principal de la app
                AppNavigation(navController, viewModel)
            }
        }
    }
}
