package net.azarquiel.cuidaplus.view

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import net.azarquiel.cuidaplus.navigation.AppNavigation
import net.azarquiel.cuidaplus.ui.theme.CuidaPlusTheme
import net.azarquiel.cuidaplus.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel = MainViewModel(this)
        viewModel.subirEnfermedadesAFirebase(this)
        viewModel.subirMedicamentosMaestro(this)
        viewModel.subirTratamientosMaestro(this)

        setContent {
            CuidaPlusTheme {
                AppNavigation(viewModel)
            }
        }
    }
}