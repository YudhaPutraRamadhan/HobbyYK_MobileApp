package com.example.hobbyyk_new.view


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.datastore.preferences.core.edit
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hobbyyk_new.view.screen.admin.ActivityFormScreen
import com.example.hobbyyk_new.view.screen.admin.ActivityListScreen
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
import com.example.hobbyyk_new.view.screen.user.ActivityFeedScreen
import com.example.hobbyyk_new.view.screen.user.EditProfileScreen
import com.example.hobbyyk_new.view.screen.user.ProfileScreen
import com.example.hobbyyk_new.view.screen.user.VerifyChangeEmailScreen
import com.example.hobbyyk_new.view.screen.user.VerifyChangePassScreen
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.navigation
import com.example.hobbyyk_new.data.datastore.dataStore
import com.example.hobbyyk_new.utils.SessionManager
import com.example.hobbyyk_new.view.screen.auth.RequestAdminScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val isSessionExpired by SessionManager.isSessionExpired.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(isSessionExpired) {
        if (isSessionExpired) {
            showDialog = true
        }
    }

    val performLogout = {
        scope.launch {
            showDialog = false
            SessionManager.reset()
            context.dataStore.edit { it.clear() }

            navController.navigate("auth_graph") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    if (showDialog) {
        SessionExpiredDialog(onConfirm = { performLogout() })
    }

    NavHost(
        navController = navController,
        startDestination = "auth_graph"
    ) {
        authGraph(navController)

        userGraph(navController)

        adminGraph(navController)

        superAdminGraph(navController)
    }
}

fun NavGraphBuilder.authGraph(navController: NavController) {
    navigation(startDestination = "landing", route = "auth_graph") {
        composable("landing") { LandingApp(navController) }
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable(
            route = "verify_otp/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            VerifyOtpScreen(navController, email)
        }
    }
}

fun NavGraphBuilder.userGraph(navController: NavController) {
    navigation(startDestination = "home", route = "user_graph") {
        composable("home") { HomeScreen(navController) }
        composable("community_list") { CommunityListScreen(navController) }
        composable("activity_feed") { ActivityFeedScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("edit_profile") { EditProfileScreen(navController) }
        composable("request_admin") { RequestAdminScreen(navController) }

        composable(
            route = "community_detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            CommunityDetailScreen(navController, id)
        }

        composable(
            route = "detail_activity/{activityId}",
            arguments = listOf(navArgument("activityId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("activityId") ?: 0
            ActivityDetailScreen(navController, id)
        }

        composable("verify_change_pass") { VerifyChangePassScreen(navController) }
        composable(
            route = "verify_change_email/{newEmail}",
            arguments = listOf(navArgument("newEmail") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("newEmail") ?: ""
            VerifyChangeEmailScreen(navController, email)
        }
    }
}

fun NavGraphBuilder.adminGraph(navController: NavController) {
    navigation(startDestination = "admin_dashboard", route = "admin_graph") {
        composable("admin_dashboard") { AdminDashboard(navController) }
        composable("create_community") { CreateCommunityScreen(navController) }

        composable(
            route = "admin_community_detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            CommunityDetailScreen(navController, id, isAdminPreview = true)
        }

        composable(
            route = "edit_community/{communityId}",
            arguments = listOf(navArgument("communityId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("communityId") ?: 0
            EditCommunityScreen(navController, id)
        }

        composable(
            route = "activity_list/{communityId}",
            arguments = listOf(navArgument("communityId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("communityId") ?: 0
            ActivityListScreen(navController, id)
        }

        composable(
            route = "activity_form/{communityId}/{activityId}",
            arguments = listOf(
                navArgument("communityId") { type = NavType.IntType },
                navArgument("activityId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val commId = backStackEntry.arguments?.getInt("communityId") ?: 0
            val actId = backStackEntry.arguments?.getInt("activityId") ?: 0
            ActivityFormScreen(navController, commId, actId)
        }
    }
}

fun NavGraphBuilder.superAdminGraph(navController: NavController) {
    navigation(startDestination = "super_admin_dashboard", route = "super_admin_graph") {
        composable("super_admin_dashboard") { SuperAdminDashboard(navController) }
        composable("user_list") { UserListScreen(navController) }
        composable("super_admin_community_list") { SuperAdminCommunityList(navController) }
    }
}

@Composable
fun SessionExpiredDialog(onConfirm: () -> Unit) {
    Dialog(onDismissRequest = { onConfirm() }) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Warning, null, tint = Color.Red, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Sesi Berakhir", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Masa berlaku sesi Anda telah habis demi keamanan. Silakan login kembali.",
                    textAlign = TextAlign.Center, color = Color.Gray
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onConfirm, shape = RoundedCornerShape(8.dp)) {
                    Text("Login Ulang")
                }
            }
        }
    }
}