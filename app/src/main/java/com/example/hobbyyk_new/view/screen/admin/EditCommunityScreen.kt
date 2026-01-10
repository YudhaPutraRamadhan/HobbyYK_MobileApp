package com.example.hobbyyk_new.view.screen.admin

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.hobbyyk_new.data.datastore.UserStore
import com.example.hobbyyk_new.utils.Constants
import com.example.hobbyyk_new.utils.uriToFile
import com.example.hobbyyk_new.view.screen.auth.LabelText
import com.example.hobbyyk_new.viewmodel.AdminCommunityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCommunityScreen(navController: NavController, communityId: Int) {
    val context = LocalContext.current
    val userStore = remember { UserStore(context) }
    val alphaSpaceRegex = Regex("^[a-zA-Z\\s]*$")

    val factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AdminCommunityViewModel(userStore) as T
        }
    }
    val viewModel: AdminCommunityViewModel = viewModel(factory = factory)
    val scrollState = rememberScrollState()

    LaunchedEffect(communityId) { viewModel.fetchCommunityById(communityId) }

    var nama by remember { mutableStateOf("") }
    var kategori by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var lokasi by remember { mutableStateOf("") }
    var kontak by remember { mutableStateOf("") }
    var linkGrup by remember { mutableStateOf("") }

    var newLogoUri by remember { mutableStateOf<Uri?>(null) }
    var newBannerUri by remember { mutableStateOf<Uri?>(null) }
    var expanded by remember { mutableStateOf(false) }

    var isDataLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.errorMessage) {
        viewModel.errorMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()

            if (msg.contains("berhasil", ignoreCase = true) || msg.contains("sukses", ignoreCase = true)) {
                navController.popBackStack()
            }
            viewModel.errorMessage = null
        }
    }

    LaunchedEffect(viewModel.myCommunity) {
        if (viewModel.myCommunity != null && !isDataLoaded) {
            val it = viewModel.myCommunity!!
            nama = it.nama_komunitas
            kategori = it.kategori
            deskripsi = it.deskripsi ?: ""
            lokasi = it.lokasi ?: ""
            kontak = it.kontak ?: ""
            linkGrup = it.link_grup ?: ""

            isDataLoaded = true
        }
    }

    val logoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { newLogoUri = it }
    val bannerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { newBannerUri = it }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Edit Profil Komunitas", fontWeight = FontWeight.Black, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                }
            )
        }
    ) { paddingValues ->
        if (viewModel.isLoading && nama.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFFF6B35))
            }
        } else {
            Column(
                modifier = Modifier.padding(paddingValues).fillMaxSize().padding(horizontal = 24.dp).verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text("Visual Community", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color(0xFF1A1A1A))
                Text("Klik pada gambar untuk memperbarui visual.", fontSize = 13.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Box(
                            contentAlignment = Alignment.BottomEnd,
                            modifier = Modifier.size(110.dp).clickable { logoLauncher.launch("image/*") }
                        ) {
                            Box(modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color(0xFFF5F5F5))) {
                                val model = newLogoUri ?: "${Constants.URL_GAMBAR_BASE}${viewModel.myCommunity?.foto_url}"
                                AsyncImage(model = ImageRequest
                                    .Builder(context)
                                    .data(model)
                                    .crossfade(true)
                                    .build(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Surface(modifier = Modifier
                                .size(32.dp)
                                .offset(x = 4.dp, y = 4.dp),
                                shape = CircleShape,
                                color = Color(0xFFFF6B35),
                                shadowElevation = 4.dp) {
                                Icon(Icons.Default
                                    .CameraAlt,
                                    null,
                                    tint = Color.White,
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(2f)) {
                        Box(
                            contentAlignment = Alignment.BottomEnd,
                            modifier = Modifier.height(110.dp).fillMaxWidth().clickable { bannerLauncher.launch("image/*") }
                        ) {
                            Box(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(24.dp)).background(Color(0xFFF5F5F5))) {
                                val bannerModel = newBannerUri ?: "${Constants.URL_GAMBAR_BASE}${viewModel.myCommunity?.banner_url ?: viewModel.myCommunity?.foto_url}"
                                AsyncImage(model = ImageRequest
                                    .Builder(context)
                                    .data(bannerModel)
                                    .crossfade(true)
                                    .build(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Surface(modifier = Modifier
                                .size(32.dp)
                                .offset(x = 4.dp, y = 4.dp),
                                shape = CircleShape,
                                color = Color(0xFFFF6B35),
                                shadowElevation = 4.dp) {
                                Icon(Icons.Default.CameraAlt,
                                    null,
                                    tint = Color.White,
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                LabelText("Nama Komunitas")
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = nama,
                    onValueChange = { if (it.isEmpty() || it.matches(alphaSpaceRegex)) {
                        nama = it
                    } },
                    placeholder = { Text("Contoh: Komunitas Fotografi Jogja", color = Color.LightGray) },
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

                Spacer(modifier = Modifier.height(20.dp))

                LabelText("Kategori")
                Spacer(Modifier.height(8.dp))
                ExposedDropdownMenuBox(expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = kategori, onValueChange = {}, readOnly = true,
                        placeholder = { Text("Pilih kategori...", color = Color.LightGray) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF6B35),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedLabelColor = Color(0xFFFF6B35),
                            cursorColor = Color(0xFFFF6B35),
                            errorBorderColor = MaterialTheme.colorScheme.error
                        )
                    )
                    ExposedDropdownMenu(expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color.White)) {
                        Constants.COMMUNITY_CATEGORIES.forEach { option ->
                            DropdownMenuItem(text = { Text(option) },
                                onClick = { kategori = option; expanded = false }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                LabelText("Lokasi")
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = lokasi,
                    onValueChange = { if (it.isEmpty() || it.matches(alphaSpaceRegex)) {
                        lokasi = it
                    } },
                    placeholder = { Text("Daerah/Kecamatan di Yogyakarta", color = Color.LightGray) },
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

                Spacer(modifier = Modifier.height(20.dp))

                LabelText("Deskripsi")
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    placeholder = { Text("Jelaskan visi atau kegiatan komunitas Anda...", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF6B35),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedLabelColor = Color(0xFFFF6B35),
                        cursorColor = Color(0xFFFF6B35),
                        errorBorderColor = MaterialTheme.colorScheme.error
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                LabelText("Kontak Admin")
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = kontak,
                    onValueChange = { kontak = it },
                    placeholder = { Text("Nomor WhatsApp Aktif", color = Color.LightGray) },
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

                Spacer(modifier = Modifier.height(20.dp))

                LabelText("Link Grup WhatsApp")
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = linkGrup,
                    onValueChange = { linkGrup = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    placeholder = { Text("https://chat.whatsapp.com/...") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF6B35),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedLabelColor = Color(0xFFFF6B35),
                        cursorColor = Color(0xFFFF6B35),
                        errorBorderColor = MaterialTheme.colorScheme.error
                    )
                )

                Spacer(modifier = Modifier.height(56.dp))

                Button(
                    onClick = {
                        if (nama.isBlank() || lokasi.isBlank() || deskripsi.isBlank() || kategori.isBlank()) {
                            Toast.makeText(context, "Data wajib mohon dilengkapi!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val logoFile = if (newLogoUri != null) uriToFile(newLogoUri!!, context) else null
                        val bannerFile = if (newBannerUri != null) uriToFile(newBannerUri!!, context) else null

                        viewModel.updateCommunity(communityId, nama, lokasi, deskripsi,
                            kategori, kontak, linkGrup, logoFile, bannerFile)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(12.dp,
                            RoundedCornerShape(16.dp),
                            spotColor = Color(0xFFFF6B35)),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !viewModel.isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B35))
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp)
                    } else {
                        Text("Simpan Perubahan", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}