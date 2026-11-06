/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptilerdemo.maptilermobiledemo

import com.maptilerdemo.maptilermobiledemo.packtools.PackToolsScreen
import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Suppress("FunctionName")
@Composable
fun MainScreen(context: Context) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Full screen showcase pages hide the tab bar.
    val showsBottomBar = backStackEntry?.destination?.route != "layers"

    Scaffold(
        bottomBar = { if (showsBottomBar) BottomNavigationBar(navController) },
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            Modifier.padding(paddingValues),
        ) {
            composable("home") { HomeScreen(navController, context) }
            composable("offline") { OfflineScreen(navController) }
            composable("benchmark") { BenchmarkScreen(context, navController) }
            composable("packtools") { PackToolsScreen(onClose = { navController.popBackStack() }) }
            composable("layers") { LayersShowcaseScreen(context, onClose = { navController.popBackStack() }) }
        }
    }
}
