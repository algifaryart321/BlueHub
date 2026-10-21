package com.bluehub.app.presentation.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bluehub.app.presentation.theme.DarkSurface
import com.bluehub.app.presentation.theme.DarkSurfaceVariant
import com.bluehub.app.presentation.theme.TechCyan
import com.bluehub.app.presentation.theme.TextMuted
import com.bluehub.app.presentation.theme.TextPrimary

@Composable
fun BottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = DarkSurface,
        contentColor = TextPrimary
    ) {
        Screen.items.forEach { screen ->
            val isSelected = currentRoute == screen.route
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(Screen.Devices.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.title
                    )
                },
                label = {
                    Text(
                        text = screen.title,
                        fontSize = 11.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = DarkSurface,
                    selectedTextColor = TechCyan,
                    indicatorColor = TechCyan,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted
                )
            )
        }
    }
}
