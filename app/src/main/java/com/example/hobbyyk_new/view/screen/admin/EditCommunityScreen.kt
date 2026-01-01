package com.example.hobbyyk_new.view.screen.admin

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.hobbyyk_new.utils.Constants
import com.example.hobbyyk_new.utils.uriToFile
import com.example.hobbyyk_new.viewmodel.AdminCommunityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCommunityScreen(navController: NavController, communityId: Int) {
    val viewModel: AdminCommunityViewModel = viewModel()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(communityId) {
        // [PERBAIKAN 2] Panggil fungsi fetch by ID, bukan fetchMyCommunity
        // Pastikan fungsi 'fetchCommunityById' atau sejenisnya ada di ViewModel kamu!
        // Jika belum ada, buat fungsi di AdminCommunityViewModel yang isinya:
        // getCommunityById(id) -> update state 'myCommunity'
        viewModel.fetchCommunityById(communityId)
    }

    var nama by remember { mutableStateOf("") }
    var kategori by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var lokasi by remember { mutableStateOf("") }
    var kontak by remember { mutableStateOf("") }
    var linkGrup by remember { mutableStateOf("") }

    var newLogoUri by remember { mutableStateOf<Uri?>(null) }
    var newBannerUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(viewModel.myCommunity) {
        viewModel.myCommunity?.let {
            nama = it.nama_komunitas
            kategori = it.kategori
            deskripsi = it.deskripsi ?: ""
            lokasi = it.lokasi ?: ""
            kontak = it.kontak ?: ""
            linkGrup = it.link_grup ?: ""
        }
    }

    val logoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { newLogoUri = it }
    val bannerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { newBannerUri = it }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit Profil Komunitas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        // Loading state visual
        if (viewModel.isLoading && nama.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text("Update Visual", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Klik gambar untuk mengubah", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // LOGO
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.LightGray.copy(alpha = 0.3f))
                                .clickable { logoLauncher.launch("image/*") }
                        ) {
                            if (newLogoUri != null) {
                                AsyncImage(model = newLogoUri, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                            } else {
                                val oldLogo = "${Constants.URL_GAMBAR_BASE}${viewModel.myCommunity?.foto_url}"
                                AsyncImage(model = oldLogo, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                            }
                        }
                        Text("Logo", fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                    }

                    // BANNER
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(2f)) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .height(100.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.LightGray.copy(alpha = 0.3f))
                                .clickable { bannerLauncher.launch("image/*") }
                        ) {
                            if (newBannerUri != null) {
                                AsyncImage(model = newBannerUri, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                            } else {
                                val oldBanner = "${Constants.URL_GAMBAR_BASE}${viewModel.myCommunity?.banner_url ?: viewModel.myCommunity?.foto_url}"
                                AsyncImage(model = oldBanner, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                            }
                        }
                        Text("Banner", fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(24.dp))

                // FORM INPUT
                OutlinedTextField(
                    value = nama, onValueChange = { nama = it },
                    label = { Text("Nama Komunitas") }, modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = kategori, onValueChange = { kategori = it },
                    label = { Text("Kategori") }, modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = lokasi, onValueChange = { lokasi = it },
                    label = { Text("Lokasi") }, modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = deskripsi, onValueChange = { deskripsi = it },
                    label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth(), minLines = 3
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = kontak, onValueChange = { kontak = it },
                    label = { Text("Kontak") }, modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = linkGrup, onValueChange = { linkGrup = it },
                    label = { Text("Link Grup") }, modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(40.dp))

                // TOMBOL SIMPAN
                Button(
                    onClick = {
                        val logoFile = if (newLogoUri != null) uriToFile(newLogoUri!!, context) else null
                        val bannerFile = if (newBannerUri != null) uriToFile(newBannerUri!!, context) else null

                        viewModel.updateCommunity(
                            id = communityId,
                            nama, lokasi, deskripsi, kategori, kontak, linkGrup,
                            logoFile, bannerFile
                        )

                        Toast.makeText(context, "Perubahan Disimpan!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = !viewModel.isLoading
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Simpan Perubahan")
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}