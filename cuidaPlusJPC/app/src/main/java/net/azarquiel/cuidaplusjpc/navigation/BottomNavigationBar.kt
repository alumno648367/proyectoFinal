package net.azarquiel.cuidaplusjpc.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.navigation.NavController
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

    NavigationBar(containerColor = colorResource(R.color.primario)) {
        items.forEach { item ->
            // 3. Si es el item "Citas" y tenemos grupoId, navegamos a citas/<grupoId>
            val targetRoute = if (item == BottomNavItem.Citas && grupoId != null) {
                "citas/$grupoId"
            } else {
                item.route
            }

            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                selected = navController.currentDestination?.route == targetRoute,
                onClick = {
                    navController.navigate(targetRoute) {
                        popUpTo(targetRoute) { inclusive = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    indicatorColor   = colorResource(R.color.secundario)
                )
            )
        }
    }
}
