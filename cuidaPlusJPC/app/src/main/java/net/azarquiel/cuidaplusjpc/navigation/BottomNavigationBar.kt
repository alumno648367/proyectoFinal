package net.azarquiel.cuidaplusjpc.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import net.azarquiel.cuidaplusjpc.R

@Composable
fun BottomNavigationBar(
    navController: NavController,
    grupoId: String?
) {
    // Define las opciones que se muestran en la barra inferior
    val items = listOf(
        BottomNavItem.Perfil,
        BottomNavItem.Grupo,
        BottomNavItem.Inicio,
        BottomNavItem.Pacientes,
        BottomNavItem.Citas
    )

    // Obtiene la pantalla actual del stack de navegación
    val navBackStackEntry = navController.currentBackStackEntryAsState().value

    // Contenedor de la barra de navegación inferior
    NavigationBar(containerColor = colorResource(R.color.primario)) {
        items.forEach { item ->

            // Si el botón es el de citas y hay grupo, construye la ruta con ID
            val isCitas = item == BottomNavItem.Citas
            val actualRoute = if (isCitas && grupoId != null) "citas/$grupoId" else item.route

            // Define cada ítem de la barra
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },

                // Marca como seleccionado si la ruta actual coincide
                selected = navBackStackEntry?.destination?.hierarchy?.any { it.route == item.route } == true,

                // Acción al pulsar el ítem: navega a su ruta
                onClick = {
                    navController.navigate(actualRoute) {
                        // Evita apilar pantallas duplicadas
                        popUpTo(item.route) { inclusive = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },

                // Colores personalizados del ítem
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = colorResource(R.color.secundario),
                    indicatorColor = colorResource(R.color.secundario)
                )
            )
        }
    }
}
