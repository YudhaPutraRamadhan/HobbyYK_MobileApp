package com.example.hobbyyk_new.view.screen.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hobbyyk_new.viewmodel.RequestAdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestAdminScreen(navController: NavController) {
    val viewModel: RequestAdminViewModel = viewModel()
    val context = LocalContext.current

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    LaunchedEffect(viewModel.successMessage, viewModel.errorMessage) {
        viewModel.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            navController.popBackStack()
            viewModel.resetState()
        }
        viewModel.errorMessage?.let {
            Toast.makeText(context, "Gagal: $it", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Daftar Komunitas",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFFFF6B35)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Bangun Komunitasmu",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFFF6B35)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = Color(0xFFFFF3E0),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Sistem akan membuatkan akun Admin otomatis dan mengirimkannya ke email Anda setelah permintaan disetujui.",
                    fontSize = 14.sp,
                    color = Color(0xFF616161),
                    modifier = Modifier.padding(20.dp),
                    lineHeight = 22.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            LabelText("Username Admin")
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                placeholder = { Text("Masukkan nama untuk identitas admin") },
                leadingIcon = { Icon(Icons.Default.Person, null, tint = Color(0xFFFF6B35)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                isError = viewModel.usernameError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF6B35),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedLabelColor = Color(0xFFFF6B35),
                    cursorColor = Color(0xFFFF6B35)
                )
            )

            viewModel.usernameError?.let {
                Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(start = 8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            LabelText("Email Aktif")
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("contoh@email.com") },
                leadingIcon = { Icon(Icons.Default.Email, null, tint = Color(0xFFFF6B35)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                isError = viewModel.emailError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF6B35),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedLabelColor = Color(0xFFFF6B35),
                    cursorColor = Color(0xFFFF6B35)
                )
            )

            viewModel.emailError?.let {
                Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(start = 8.dp))
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = { viewModel.submitRequest(username, email) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = !viewModel.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6B35),
                    disabledContainerColor = Color(0xFFFF6B35).copy(alpha = 0.6f)
                )
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Text(
                        "Kirim Permintaan",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}