package com.example.hobbyyk_new.view.screen.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.hobbyyk_new.data.datastore.UserStore
import com.example.hobbyyk_new.utils.Constants
import com.example.hobbyyk_new.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val viewModel: ProfileViewModel = viewModel()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userStore = UserStore(context)
    val lifecycleOwner = LocalLifecycleOwner.current

    var showEmailDialog by remember { mutableStateOf(false) }
    var tempNewEmail by remember { mutableStateOf("") }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.fetchProfile()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(viewModel.message) {
        viewModel.message?.let {
            if (!it.contains("berhasil", ignoreCase = true)) {
                // Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
            viewModel.clearMessage()
        }
    }

    LaunchedEffect(viewModel.navigateToVerifyPass) {
        if (viewModel.navigateToVerifyPass) {
            navController.navigate("verify_change_pass")
            viewModel.navigateToVerifyPass = false
        }
    }

    LaunchedEffect(viewModel.navigateToVerifyEmail) {
        viewModel.navigateToVerifyEmail?.let { email ->
            navController.navigate("verify_change_email/$email")
            viewModel.navigateToVerifyEmail = null
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profil Saya") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (viewModel.isLoading && viewModel.userProfile == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data("${Constants.URL_GAMBAR_BASE}${viewModel.userProfile?.profile_pic}")
                        .crossfade(true).build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(viewModel.userProfile?.username ?: "User", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(viewModel.userProfile?.email ?: "-", color = Color.Gray)

                Spacer(modifier = Modifier.height(24.dp))

                if (viewModel.managedCommunity != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Admin Komunitas", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                Text(viewModel.managedCommunity?.nama_komunitas ?: "", color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Edit Informasi", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    IconButton(onClick = { navController.navigate("edit_profile") }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Bio", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Gray)
                        Text(
                            text = if (viewModel.userProfile?.bio.isNullOrEmpty()) "-" else viewModel.userProfile!!.bio!!,
                            fontSize = 16.sp
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Nomor HP", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                                Text(
                                    text = if (viewModel.userProfile?.no_hp.isNullOrEmpty()) "-" else viewModel.userProfile!!.no_hp!!,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text("Keamanan Akun", modifier = Modifier.align(Alignment.Start), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { viewModel.requestOtpPassword() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ganti Password")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { showEmailDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Email, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ganti Email")
                }

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = {
                        scope.launch {
                            userStore.clearSession()
                            navController.navigate("login") { popUpTo(0) { inclusive = true } }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Keluar (Logout)")
                }
            }
        }

        if (showEmailDialog) {
            AlertDialog(
                onDismissRequest = { showEmailDialog = false },
                title = { Text("Ganti Email") },
                text = {
                    OutlinedTextField(
                        value = tempNewEmail,
                        onValueChange = { tempNewEmail = it },
                        label = { Text("Masukkan Email Baru") }
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        if (tempNewEmail.isNotEmpty()) {
                            viewModel.requestOtpEmail(tempNewEmail)
                            showEmailDialog = false
                        }
                    }) { Text("Kirim OTP") }
                }
            )
        }
    }
}