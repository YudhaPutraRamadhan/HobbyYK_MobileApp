package com.example.hobbyyk_new.view.screen.user

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.hobbyyk_new.utils.Constants
import com.example.hobbyyk_new.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController) {
    val viewModel: ProfileViewModel = viewModel()
    val context = LocalContext.current

    var bio by remember { mutableStateOf("") }
    var noHp by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchProfile()
    }

    LaunchedEffect(viewModel.userProfile) {
        viewModel.userProfile?.let {
            if (bio.isEmpty()) bio = it.bio ?: ""
            if (noHp.isEmpty()) noHp = it.no_hp ?: ""
        }
    }

    LaunchedEffect(viewModel.message) {
        viewModel.message?.let {
            if (it.contains("berhasil", ignoreCase = true)) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.clearMessage()
                navController.popBackStack() // Kembali ke halaman Profil setelah simpan
            } else {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.clearMessage()
            }
        }
    }

    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImageUri = uri
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit Profil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (viewModel.isLoading && viewModel.userProfile == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    val model = if (selectedImageUri != null) selectedImageUri else
                        "${Constants.URL_GAMBAR_BASE}${viewModel.userProfile?.profile_pic}"

                    AsyncImage(
                        model = ImageRequest.Builder(context).data(model).crossfade(true).build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                            .clickable { imageLauncher.launch("image/*") }
                    )
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable { imageLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = bio, onValueChange = { bio = it },
                    label = { Text("Bio / Status") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = noHp, onValueChange = { noHp = it },
                    label = { Text("Nomor HP") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        viewModel.updateProfile(bio, noHp, selectedImageUri, context)
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Simpan Perubahan")
                    }
                }
            }
        }
    }
}