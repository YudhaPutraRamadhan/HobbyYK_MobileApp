package com.example.hobbyyk_new.view.screen.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hobbyyk_new.viewmodel.LoginViewModel
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign

@Composable
fun LoginScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel: LoginViewModel = viewModel()
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.isLoginSuccess) {
        if (viewModel.isLoginSuccess) {
            val route = when (viewModel.userRole) {
                "super_admin" -> "super_admin_graph"
                "admin_komunitas" -> "admin_graph"
                else -> "user_graph"
            }
            navController.navigate(route) {
                popUpTo("auth_graph") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Selamat Datang!",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFFF6B35),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Silakan masuk untuk melanjutkan petualangan hobimu",
                fontSize = 15.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                value = viewModel.email,
                onValueChange = {
                    viewModel.email = it
                    if (viewModel.emailError != null) viewModel.emailError = null
                },
                label = { Text("Email") },
                isError = viewModel.emailError != null,
                supportingText = {
                    if (viewModel.emailError != null) {
                        Text(text = viewModel.emailError!!, color = MaterialTheme.colorScheme.error)
                    }
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = "Email Icon",
                        tint = Color(0xFFFF6B35)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF6B35),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedLabelColor = Color(0xFFFF6B35),
                    cursorColor = Color(0xFFFF6B35),
                    errorBorderColor = MaterialTheme.colorScheme.error
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = viewModel.password,
                onValueChange = {
                    viewModel.password = it
                    if (viewModel.passwordError != null) viewModel.passwordError = null
                },
                label = { Text("Password") },
                isError = viewModel.passwordError != null,
                supportingText = {
                    if (viewModel.passwordError != null) {
                        Text(text = viewModel.passwordError!!, color = MaterialTheme.colorScheme.error)
                    }
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Password Icon",
                        tint = Color(0xFFFF6B35)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = "Toggle Password", tint = Color.Gray)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF6B35),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedLabelColor = Color(0xFFFF6B35),
                    cursorColor = Color(0xFFFF6B35),
                    errorBorderColor = MaterialTheme.colorScheme.error
                )
            )

            if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.Start)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Login Button - Colorful & Bold
            Button(
                onClick = { viewModel.login() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp)),
                enabled = !viewModel.isLoading,
                shape = RoundedCornerShape(16.dp),
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
                        "Masuk",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Register Link
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Belum punya akun? ", color = Color.Gray, fontSize = 17.sp)
                Text(
                    text = "Daftar",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF6B35),
                    fontSize = 17.sp,
                    modifier = Modifier.clickable { navController.navigate("register") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Divider
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 40.dp),
                thickness = 1.dp,
                color = Color.LightGray.copy(alpha = 0.4f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Request Admin Section - Colorful card
            Surface(
                color = Color(0xFFFFF3E0),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Ingin mendaftarkan komunitas?",
                        fontSize = 16.sp,
                        color = Color(0xFF616161),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Ajukan Sebagai Admin",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6B35),
                        modifier = Modifier.clickable { navController.navigate("request_admin") }
                    )
                }
            }
        }
    }
}