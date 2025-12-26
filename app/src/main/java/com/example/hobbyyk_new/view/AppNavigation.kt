package com.example.hobbyyk_new.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hobbyyk_new.view.screen.admin.ActivityFormScreen
import com.example.hobbyyk_new.view.screen.user.ActivityListScreen
import com.example.hobbyyk_new.view.screen.admin.AdminDashboard
import com.example.hobbyyk_new.view.screen.admin.CreateCommunityScreen
import com.example.hobbyyk_new.view.screen.admin.EditCommunityScreen
import com.example.hobbyyk_new.view.screen.admin.activity.ActivityDetailScreen
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
                    when (role) {
                        "super_admin" -> {
                            navController.navigate("super_admin_dashboard") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                        "admin_komunitas" -> {
                            navController.navigate("admin_dashboard") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                        else -> {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
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
            route = "detail_community/{communityId}?isAdmin={isAdmin}",
            arguments = listOf(
                navArgument("communityId") { type = NavType.IntType },
                navArgument("isAdmin") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("communityId") ?: 0
            val isAdmin = backStackEntry.arguments?.getBoolean("isAdmin") ?: false

            CommunityDetailScreen(navController, id, isAdminPreview = isAdmin)
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
        composable("admin_dashboard") {
            AdminDashboard(navController)
        }
        composable("super_admin_communities") {
            SuperAdminCommunityList(navController = navController)
        }
        composable("register") {
            RegisterScreen(navController = navController)
        }

        composable("create_community") {
            CreateCommunityScreen(navController = navController)
        }

        composable(
            route = "edit_community/{communityId}",
            arguments = listOf(navArgument("communityId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("communityId") ?: 0
            EditCommunityScreen(navController = navController, communityId = id)
        }

        composable(
            route = "create_activity/{communityId}?activityId={activityId}",
            arguments = listOf(
                navArgument("communityId") { type = NavType.IntType },
                navArgument("activityId") {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val commId = backStackEntry.arguments?.getInt("communityId") ?: 0
            val actId = backStackEntry.arguments?.getInt("activityId") ?: 0

            ActivityFormScreen(navController, commId, actId)
        }

        composable(
            route = "activity_list/{communityId}",
            arguments = listOf(
                navArgument("communityId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("communityId") ?: 0

            ActivityListScreen(navController, id)
        }

        composable(
            route = "detail_activity/{activityId}",
            arguments = listOf(navArgument("activityId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("activityId") ?: 0
            ActivityDetailScreen(navController, id)
        }
    }
}