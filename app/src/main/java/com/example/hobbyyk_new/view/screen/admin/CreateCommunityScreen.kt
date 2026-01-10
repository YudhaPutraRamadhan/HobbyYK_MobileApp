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
import androidx.compose.material.icons.filled.Image
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
import com.example.hobbyyk_new.data.datastore.UserStore
import com.example.hobbyyk_new.utils.Constants
import com.example.hobbyyk_new.utils.uriToFile
import com.example.hobbyyk_new.view.screen.auth.LabelText
import com.example.hobbyyk_new.viewmodel.AdminCommunityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCommunityScreen(navController: NavController) {
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

    var nama by remember { mutableStateOf("") }
    var kategori by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var lokasi by remember { mutableStateOf("") }
    var kontak by remember { mutableStateOf("") }
    var linkGrup by remember { mutableStateOf("") }
    var logoUri by remember { mutableStateOf<Uri?>(null) }
    var bannerUri by remember { mutableStateOf<Uri?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val logoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { logoUri = it }
    val bannerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { bannerUri = it }

    LaunchedEffect(viewModel.errorMessage) {
        viewModel.errorMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            if (msg.contains("berhasil", ignoreCase = true)) {
                navController.popBackStack()
            }
            viewModel.errorMessage = null
        }
    }

    Scaffold(
        containerColor = Color(0xFFFAFAFA),
        topBar = {
            TopAppBar(
                title = { Text("Registrasi Komunitas", fontWeight = FontWeight.Black, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text("Aset Visual", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color(0xFF1A1A1A))
            Text("Logo dan Banner adalah wajah komunitas Anda.", fontSize = 13.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(110.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.White)
                            .shadow(2.dp, RoundedCornerShape(24.dp))
                            .clickable { logoLauncher.launch("image/*") }
                    ) {
                        if (logoUri != null) {
                            AsyncImage(model = logoUri, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Color(0xFFFF6B35), modifier = Modifier.size(32.dp))
                                Text("Logo", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            }
                        }
                    }
                }

                // Banner Picker
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(2f)) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .height(110.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.White)
                            .shadow(2.dp, RoundedCornerShape(24.dp))
                            .clickable { bannerLauncher.launch("image/*") }
                    ) {
                        if (bannerUri != null) {
                            AsyncImage(model = bannerUri, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Upload, contentDescription = null, tint = Color(0xFFFF6B35), modifier = Modifier.size(32.dp))
                                Text("Pilih Banner Utama", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider(thickness = 1.dp, color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(32.dp))

            Text("Detail Informasi", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color(0xFF1A1A1A))
            Spacer(modifier = Modifier.height(20.dp))

            LabelText("Nama Komunitas")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = nama, onValueChange = { if (it.isEmpty() || it.matches(alphaSpaceRegex)) {
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
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = kategori,
                    onValueChange = {},
                    readOnly = true,
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

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    Constants.COMMUNITY_CATEGORIES.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption, fontWeight = FontWeight.Medium) },
                            onClick = { kategori = selectionOption; expanded = false },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            LabelText("Lokasi")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = lokasi, onValueChange = { if (it.isEmpty() || it.matches(alphaSpaceRegex)) {
                    lokasi = it
                } },
                placeholder = { Text("Daerah/Kecamatan di Yogyakarta", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
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
                value = deskripsi, onValueChange = { deskripsi = it },
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

            Spacer(modifier = Modifier.height(32.dp))
            Text("Hubungan Masyarakat", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color(0xFF1A1A1A))
            Spacer(modifier = Modifier.height(20.dp))

            LabelText("Kontak Admin")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = kontak, onValueChange = { kontak = it },
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

            LabelText("Link Grup WhatsApp (Opsional)")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = linkGrup, onValueChange = { linkGrup = it },
                placeholder = { Text("https://chat.whatsapp.com/...", color = Color.LightGray) },
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

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = {
                    if (logoUri != null && nama.isNotEmpty() && lokasi.isNotEmpty() && deskripsi.isNotEmpty() && kategori.isNotEmpty()) {
                        val logoFile = uriToFile(logoUri!!, context)
                        val bannerFile = if (bannerUri != null) uriToFile(bannerUri!!, context) else null

                        viewModel.createCommunity(
                            nama, lokasi, deskripsi, kategori, kontak, linkGrup,
                            logoFile, bannerFile
                        )
                    } else {
                        Toast.makeText(context, "Mohon lengkapi Nama, Kategori, Lokasi, Deskripsi dan Logo!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp).shadow(8.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFFFF6B35)),
                shape = RoundedCornerShape(16.dp),
                enabled = !viewModel.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B35))
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text("Daftarkan Komunitas", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}