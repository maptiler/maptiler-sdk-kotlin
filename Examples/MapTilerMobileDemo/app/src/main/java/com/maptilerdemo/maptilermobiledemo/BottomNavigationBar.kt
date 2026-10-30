/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptilerdemo.maptilermobiledemo

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: ImageVector,
)

@Suppress("FunctionName")
@Composable
fun BottomNavigationBar(navController: NavController) {
    val navItems =
        listOf(
            BottomNavItem("Demo", "home", Icons.Default.Home),
            BottomNavItem("Offline", "offline", Icons.Default.Place),
        )

    NavigationBar(
        containerColor = Color(0xFF00A1C2),
        contentColor = Color.White,
    ) {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route

        navItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { navController.navigate(item.route) },
                icon = {
                    // Long-pressing the Offline tab opens the hidden pack estimator tools.
                    val gestures =
                        if (item.route == "offline") {
                            Modifier.pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = { navController.navigate(item.route) },
                                    onLongPress = { navController.navigate("packtools") },
                                )
                            }
                        } else {
                            Modifier
                        }
                    Icon(item.icon, contentDescription = item.label, modifier = gestures)
                },
                label = { Text(item.label) },
                colors =
                    NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedIconColor = Color.White,
                        unselectedTextColor = Color.White,
                        indicatorColor = Color(0xFF00A1C2),
                    ),
            )
        }
    }
}
