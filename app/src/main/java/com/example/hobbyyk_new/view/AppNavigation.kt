package com.example.hobbyyk_new.view

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hobbyyk_new.view.screen.CommunityListScreen
import com.example.hobbyyk_new.view.screen.HomeScreen
import com.example.hobbyyk_new.view.screen.LandingApp
import com.example.hobbyyk_new.view.screen.LoginScreen
import com.example.hobbyyk_new.view.screen.SuperAdminDashboard

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

        composable("home") {
            HomeScreen(navController)
        }

        composable("super_admin_dashboard") {
            SuperAdminDashboard(navController)
        }

        composable("community_list") {
            CommunityListScreen(navController)
        }
    }
}