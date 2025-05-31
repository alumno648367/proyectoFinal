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
    val items = listOf(
        BottomNavItem.Perfil,
        BottomNavItem.Grupo,
        BottomNavItem.Inicio,
        BottomNavItem.Pacientes,
        BottomNavItem.Citas
    )

    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(containerColor = colorResource(R.color.primario)) {
        items.forEach { item ->
            val isCitas = item == BottomNavItem.Citas
            val actualRoute = if (isCitas && grupoId != null) "citas/$grupoId" else item.route

            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                selected = navBackStackEntry?.destination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    navController.navigate(actualRoute) {
                        popUpTo(item.route) { inclusive = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    indicatorColor = colorResource(R.color.secundario)
                )
            )
        }
    }
}
