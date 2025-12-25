package com.example.hobbyyk_new.view.screen.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.hobbyyk_new.data.model.Community
import com.example.hobbyyk_new.utils.Constants.URL_GAMBAR_BASE
import com.example.hobbyyk_new.utils.uriToFile
import com.example.hobbyyk_new.viewmodel.AdminCommunityViewModel
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCommunityScreen(navController: NavController) {
    val viewModel: AdminCommunityViewModel = viewModel()
    val context = LocalContext.current

    var showCreateDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var communityToDelete by remember { mutableStateOf<Community?>(null) }
    var selectedCommunity by remember { mutableStateOf<Community?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchCommunities()
    }

    if (showEditDialog && selectedCommunity != null) {
        EditCommunityDialog(
            community = selectedCommunity!!,
            onDismiss = { showEditDialog = false },
            onSave = { id, nama, loc, desk, kat, kon, link, newLogoUri, newBannerUri ->

                val logoFile = if (newLogoUri != null) {
                    uriToFile(newLogoUri, context)
                } else null

                val bannerFile = if (newBannerUri != null) {
                    uriToFile(newBannerUri, context)
                } else null

                viewModel.updateCommunity(
                    id, nama, loc, desk, kat, kon, link,
                    logoFile, bannerFile
                )

                showEditDialog = false
            }
        )
    }

    if (showDeleteDialog && communityToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Komunitas?") },
            text = { Text("Yakin ingin menghapus komunitas '${communityToDelete!!.nama_komunitas}'? Semua data di dalamnya akan hilang permanen.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCommunity(communityToDelete!!.id) // Eksekusi Hapus
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Hapus Permanen")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            }
        )
    }

    if (showCreateDialog) {
        CreateCommunityDialog(
            onDismiss = { showCreateDialog = false },
            onSave = { nama, lokasi, deskripsi, kategori, kontak, linkGrup, logoUri, bannerUri ->
                if (logoUri != null && bannerUri != null) {
                    val logoFile = uriToFile(logoUri, context)
                    val bannerFile = uriToFile(bannerUri, context)

                    viewModel.createCommunity(
                        nama, lokasi, deskripsi,
                        kategori, kontak, linkGrup,
                        logoFile, bannerFile
                    )
                    showCreateDialog = false
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Kelola Komunitas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Komunitas", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(viewModel.communities) { community ->
                        AdminCommunityItem(
                            community = community,
                            onDelete = {
                                communityToDelete = community
                                showDeleteDialog = true
                            },
                            onEdit = {
                                selectedCommunity = community
                                showEditDialog = true
                            },
                            onClick = { navController.navigate("detail_community/${community.id}") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminCommunityItem(
    community: Community,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Column {
            AsyncImage(
                model = "http://10.0.2.2:5000/uploads/${community.banner_url}",
                contentDescription = "Banner Komunitas",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(160.dp)
            )

            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = community.nama_komunitas, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                        Text(text = community.lokasi, color = Color.Gray, fontSize = 14.sp)
                    }
                }

                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Blue)
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Hapus",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
fun EditCommunityDialog(
    community: Community,
    onDismiss: () -> Unit,
    onSave: (Int, String, String, String, String, String, String, Uri?, Uri?) -> Unit
) {
    var nama by remember { mutableStateOf(community.nama_komunitas) }
    var lokasi by remember { mutableStateOf(community.lokasi) }
    var deskripsi by remember { mutableStateOf(community.deskripsi) }
    var kategori by remember { mutableStateOf(community.kategori ?: "") }
    var kontak by remember { mutableStateOf(community.kontak ?: "") }
    var linkGrup by remember { mutableStateOf(community.link_grup ?: "") }
    var newLogoUri by remember { mutableStateOf<Uri?>(null) }
    var newBannerUri by remember { mutableStateOf<Uri?>(null) }

    val logoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { newLogoUri = it }
    val bannerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { newBannerUri = it }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Komunitas (Lengkap)") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Klik gambar untuk mengganti:", fontSize = 12.sp, color = Color.Gray)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.LightGray)
                            .clickable { logoLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (newLogoUri != null) {
                            AsyncImage(model = newLogoUri, contentDescription = "New Logo", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        } else {
                            AsyncImage(
                                model = "$URL_GAMBAR_BASE${community.foto_url}",
                                contentDescription = "Old Logo", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(80.dp)
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                            .background(Color.LightGray)
                            .clickable { bannerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (newBannerUri != null) {
                            AsyncImage(model = newBannerUri, contentDescription = "New Banner", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        } else {
                            AsyncImage(
                                model = "$URL_GAMBAR_BASE${community.banner_url ?: community.foto_url}", // Fallback ke foto_url kalau banner kosong
                                contentDescription = "Old Banner", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                OutlinedTextField(value = nama, onValueChange = { nama = it }, label = { Text("Nama Komunitas") })
                OutlinedTextField(value = lokasi, onValueChange = { lokasi = it }, label = { Text("Lokasi") })
                OutlinedTextField(value = deskripsi, onValueChange = { deskripsi = it }, label = { Text("Deskripsi") }, minLines = 3)
                OutlinedTextField(value = kategori, onValueChange = { kategori = it }, label = { Text("Kategori") })
                OutlinedTextField(value = kontak, onValueChange = { kontak = it }, label = { Text("Kontak") })
                OutlinedTextField(value = linkGrup, onValueChange = { linkGrup = it }, label = { Text("Link Grup") })
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(community.id, nama, lokasi, deskripsi, kategori, kontak, linkGrup, newLogoUri, newBannerUri)
            }) { Text("Simpan Perubahan") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}

@Composable
fun CreateCommunityDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String, Uri?, Uri?) -> Unit
) {
    var nama by remember { mutableStateOf("") }
    var lokasi by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }

    var kategori by remember { mutableStateOf("") }
    var kontak by remember { mutableStateOf("") }
    var linkGrup by remember { mutableStateOf("") }

    var logoUri by remember { mutableStateOf<Uri?>(null) }
    var bannerUri by remember { mutableStateOf<Uri?>(null) }

    val logoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { logoUri = it }
    val bannerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { bannerUri = it }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Buat Komunitas Lengkap") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("1. Logo & Banner:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                            .background(Color.LightGray)
                            .clickable { logoLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (logoUri != null) AsyncImage(model = logoUri, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        else Text("Logo", fontSize = 10.sp)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(80.dp)
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                            .background(Color.LightGray)
                            .clickable { bannerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (bannerUri != null) AsyncImage(model = bannerUri, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        else Text("Banner", fontSize = 10.sp)
                    }
                }

                OutlinedTextField(value = nama, onValueChange = { nama = it }, label = { Text("Nama Komunitas") })
                OutlinedTextField(value = lokasi, onValueChange = { lokasi = it }, label = { Text("Lokasi") })
                OutlinedTextField(value = deskripsi, onValueChange = { deskripsi = it }, label = { Text("Deskripsi") })
                OutlinedTextField(value = kategori, onValueChange = { kategori = it }, label = { Text("Kategori (mis: Olahraga)") })
                OutlinedTextField(value = kontak, onValueChange = { kontak = it }, label = { Text("Kontak (WA/Email)") })
                OutlinedTextField(value = linkGrup, onValueChange = { linkGrup = it }, label = { Text("Link Grup (Opsional)") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(nama, lokasi, deskripsi, kategori, kontak, linkGrup, logoUri, bannerUri)
                },
                enabled = logoUri != null && bannerUri != null && nama.isNotEmpty()
            ) { Text("Simpan") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}