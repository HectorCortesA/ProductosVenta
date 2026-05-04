package com.cortsor.productosventa.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cortsor.productosventa.ui.theme.screens.AddProductScreen
import com.cortsor.productosventa.ui.theme.screens.BuscarPuntoVentaScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "buscar_punto") {
        composable("buscar_punto") {
            BuscarPuntoVentaScreen(onNavigateToAddProduct = {
                navController.navigate("add_product")
            })
        }
        composable("add_product") {
            AddProductScreen()
        }
    }
}