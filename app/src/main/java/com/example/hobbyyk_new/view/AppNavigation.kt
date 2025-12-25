package com.example.hobbyyk_new.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hobbyyk_new.view.screen.admin.AdminCommunityScreen
import com.example.hobbyyk_new.view.screen.user.CommunityDetailScreen
import com.example.hobbyyk_new.view.screen.user.CommunityListScreen
import com.example.hobbyyk_new.view.screen.user.HomeScreen
import com.example.hobbyyk_new.view.screen.auth.LandingApp
import com.example.hobbyyk_new.view.screen.auth.LoginScreen
import com.example.hobbyyk_new.view.screen.auth.RegisterScreen
import com.example.hobbyyk_new.view.screen.auth.VerifyOtpScreen
import com.example.hobbyyk_new.view.screen.superadmin.SuperAdminCommunityList
import com.example.hobbyyk_new.view.screen.superadmin.SuperAdminDashboard
import com.example.hobbyyk_new.view.screen.superadmin.UserListScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "LandingApp") {

        composable("LandingApp") {
            LandingApp(
                onNavigate = { destination ->
                    navController.navigate(destination) {
                        popUpTo("LandingApp") { inclusive = true }
                    }
                }
            )
        }

        composable("login") {
            LoginScreen(
                navController = navController,
                onLoginSuccess = { role ->
                    if (role == "super_admin" || role == "admin") {

                        navController.navigate("super_admin_dashboard") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(
            route = "verify_otp/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            VerifyOtpScreen(navController = navController, email = email)
        }

        composable(
            route = "detail_community/{communityId}",
            arguments = listOf(
                androidx.navigation.navArgument("communityId") {
                    type = androidx.navigation.NavType.IntType // Tipe datanya Angka (Int)
                }
            )
        ) { backStackEntry ->
            // Ambil ID dari paket yang dikirim
            val id = backStackEntry.arguments?.getInt("communityId") ?: 0

            // Panggil Layarnya
            CommunityDetailScreen(navController, id)
        }

        composable("home") {
            HomeScreen(navController)
        }

        composable("super_admin_dashboard") {
            SuperAdminDashboard(navController)
        }

        composable("community_list") {
            CommunityListScreen(navController)
        }
        composable("user_list") {
            UserListScreen(navController)
        }
        composable("admin_community_list") {
            AdminCommunityScreen(navController)
        }
        composable("super_admin_communities") {
            SuperAdminCommunityList(navController = navController)
        }
        composable("register") {
            RegisterScreen(navController = navController)
        }

    }
}