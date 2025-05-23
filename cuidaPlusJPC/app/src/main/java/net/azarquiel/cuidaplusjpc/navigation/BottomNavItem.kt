package net.azarquiel.cuidaplusjpc.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val title: String, val route: String, val icon: ImageVector) {
    object Inicio : BottomNavItem("Inicio", "inicio", Icons.Filled.Home)

    object Perfil : BottomNavItem("Mi perfil", "perfil", Icons.Filled.Person)

    object Grupo : BottomNavItem("Grupo", "grupo", Icons.Filled.Group)

    object Pacientes : BottomNavItem("Pacientes", "pacientes", Icons.Filled.MedicalServices)

    object Citas : BottomNavItem("Citas", "citas", Icons.Filled.Event)

    object Notificaciones : BottomNavItem("Notificaciones", "Notificaciones", Icons.Filled.Notifications)
}
