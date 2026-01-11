package com.example.scentra.uicontroller

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.scentra.uicontroller.route.DestinasiDashboard
import com.example.scentra.uicontroller.route.DestinasiDetail
import com.example.scentra.uicontroller.route.DestinasiEntryProduct
import com.example.scentra.uicontroller.route.DestinasiHistory
import com.example.scentra.uicontroller.route.DestinasiLanding
import com.example.scentra.uicontroller.route.DestinasiLogin
import com.example.scentra.uicontroller.route.DestinasiProfile
import com.example.scentra.uicontroller.route.DestinasiRegister
import com.example.scentra.uicontroller.route.DestinasiUpdate
import com.example.scentra.uicontroller.view.HalamanDashboard
import com.example.scentra.uicontroller.view.HalamanDetailProduct
import com.example.scentra.uicontroller.view.HalamanEditProduct
import com.example.scentra.uicontroller.view.HalamanEntryProduct
import com.example.scentra.uicontroller.view.HalamanHistory
import com.example.scentra.uicontroller.view.HalamanLanding
import com.example.scentra.uicontroller.view.HalamanLogin
import com.example.scentra.uicontroller.view.HalamanProfile
import com.example.scentra.uicontroller.view.HalamanRegister

@Composable
fun ScentraNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = DestinasiLanding.route,
        modifier = modifier
    ) {

        composable(DestinasiLanding.route) {
            HalamanLanding(
                onStartClicked = {
                    navController.navigate(DestinasiLogin.route) {
                        popUpTo(DestinasiLanding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(DestinasiLogin.route) {
            HalamanLogin(
                onLoginSuccess = { role ->
                    navController.navigate("${DestinasiDashboard.route}/$role") {
                        popUpTo(DestinasiLogin.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = DestinasiDashboard.routeWithArgs,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "Staff"

            HalamanDashboard(
                role = role,
                onNavigateToProfile = {
                    navController.navigate("${DestinasiProfile.route}/$role") { launchSingleTop = true }
                },
                onNavigateToHistory = {
                    navController.navigate(DestinasiHistory.route) { launchSingleTop = true }
                },
                // FAB (Tambah Produk Baru) -> Entry
                onAddProductClick = {
                    navController.navigate(DestinasiEntryProduct.route)
                },
                // Card (Lihat Detail) -> Detail
                onProductClick = { idProduk ->
                    navController.navigate("${DestinasiDetail.route}/$idProduk")
                }
            )
        }

        composable(DestinasiRegister.route) {
            HalamanRegister(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = DestinasiProfile.routeWithArgs,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "Staff"

            HalamanProfile(
                role = role,
                onLogoutClick = {
                    navController.navigate(DestinasiLogin.route) { popUpTo(0) }
                },
                onAddStaffClick = {
                    navController.navigate(DestinasiRegister.route)
                },
                onNavigate = { route ->
                    when (route) {
                        "dashboard" -> navController.navigate("${DestinasiDashboard.route}/$role") { launchSingleTop = true }
                        "history" -> navController.navigate(DestinasiHistory.route) { launchSingleTop = true }
                        "profile" -> { }
                    }
                }
            )
        }

        composable(DestinasiHistory.route) {
            HalamanHistory(
                onNavigate = { route ->
                    when (route) {
                        "dashboard" -> navController.navigate("${DestinasiDashboard.route}/Admin") { launchSingleTop = true }
                        "profile" -> navController.navigate("${DestinasiProfile.route}/Admin") { launchSingleTop = true }
                        "history" -> { }
                    }
                }
            )
        }

        composable(DestinasiEntryProduct.route) {
            HalamanEntryProduct(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = DestinasiDetail.routeWithArgs,
            arguments = listOf(navArgument(DestinasiDetail.idProduk) {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val idProduk = backStackEntry.arguments?.getInt(DestinasiDetail.idProduk)
            if (idProduk != null) {
                HalamanDetailProduct(
                    idProduk = idProduk,
                    navigateBack = { navController.popBackStack() },
                    // 👇 Callback Edit sementara
                    onEditClick = { id ->
                        navController.navigate("${DestinasiUpdate.route}/$id")
                    }
                )
            }
        }

        composable(
            route = DestinasiUpdate.routeWithArgs,
            arguments = listOf(navArgument(DestinasiUpdate.idProduk) {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val idProduk = backStackEntry.arguments?.getInt(DestinasiUpdate.idProduk)
            if (idProduk != null) {
                HalamanEditProduct(
                    idProduk = idProduk,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}