package net.azarquiel.cuidaplusjpc.view

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import net.azarquiel.cuidaplusjpc.navigation.AppNavigation
import net.azarquiel.cuidaplusjpc.ui.theme.CuidaPlusJPCTheme
import net.azarquiel.cuidaplusjpc.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel = MainViewModel(this)
        //viewModel.subirEnfermedadesAFirebase(this)
        setContent {
            CuidaPlusJPCTheme {
                AppNavigation(viewModel)
            }
        }
    }
}