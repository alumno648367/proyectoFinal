package net.azarquiel.cuidaplusjpc.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

// Define cada item del BottomNavigation con título, ruta y icono
sealed class BottomNavItem(val title: String, val route: String, val icon: ImageVector) {

    // Opción: pantalla principal (Mi Cuenta)
    object Inicio : BottomNavItem("Inicio", "inicio", Icons.Filled.Home)

    // Opción: perfil del usuario actual
    object Perfil : BottomNavItem("Mi perfil", "perfil", Icons.Filled.Person)

    // Opción: gestión del grupo familiar
    object Grupo : BottomNavItem("Grupo", "grupo", Icons.Filled.Group)

    // Opción: listado de pacientes del grupo
    object Pacientes : BottomNavItem("Pacientes", "pacientes", Icons.Filled.MedicalServices)

    // Opción: citas médicas (requiere ID del grupo en la ruta)
    object Citas : BottomNavItem("Citas", "citas/{grupoFamiliarId}", Icons.Filled.Event)
}
