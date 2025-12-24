package com.example.hobbyyk_new.view.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hobbyyk_new.data.datastore.UserStore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperAdminDashboard(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userStore = UserStore(context)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "PANEL SUPER ADMIN",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFB71C1C)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Selamat Datang, Penguasa Sistem!", fontSize = 18.sp)

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { /* Nanti */ },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
            ) {
                Text("👥  Kelola Pengguna")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* Nanti */ },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
            ) {
                Text("🏢  Kelola Semua Komunitas")
            }

            Spacer(modifier = Modifier.height(48.dp))

            OutlinedButton(
                onClick = {
                    scope.launch {
                        userStore.clearSession()

                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Keluar (Logout)", color = Color.Red)
            }
        }
    }
}