package com.example.hobbyyk_new.view.screen.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Visibility
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
import coil.request.ImageRequest
import com.example.hobbyyk_new.data.model.Activity
import com.example.hobbyyk_new.utils.Constants
import com.example.hobbyyk_new.viewmodel.ActivityViewModel
import org.json.JSONArray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityListScreen(navController: NavController, communityId: Int) {
    val viewModel: ActivityViewModel = viewModel()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var activityToDeleteId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(communityId) {
        viewModel.getActivities(communityId)
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Aktivitas?") },
            text = { Text("Apakah Anda yakin ingin menghapus aktivitas ini? Data tidak bisa dikembalikan.") },
            confirmButton = {
                Button(
                    onClick = {
                        activityToDeleteId?.let { id ->
                            viewModel.deleteActivity(id, communityId)
                        }
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Ya, Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Daftar Aktivitas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("create_activity/$communityId")
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (viewModel.activityList.isEmpty()) {
                Text("Belum ada aktivitas.", modifier = Modifier.align(Alignment.Center), color = Color.Gray)
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp)) {
                    items(viewModel.activityList) { activity ->
                        ActivityItemCard(
                            activity = activity,
                            onDelete = {
                                activityToDeleteId = activity.id
                                showDeleteDialog = true
                            },
                            onEdit = {
                                navController.navigate("create_activity/$communityId?activityId=${activity.id}")
                            },
                            onDetail = {
                                navController.navigate("detail_activity/${activity.id}")
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityItemCard(
    activity: Activity,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onDetail: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {

                // --- BAGIAN ATAS: GAMBAR (KIRI) & TEKS (KANAN) ---
                Row(verticalAlignment = Alignment.Top) {
                    // Ambil gambar pertama saja untuk thumbnail
                    val firstImage = remember(activity.foto_kegiatan) {
                        try {
                            val jsonArray = JSONArray(activity.foto_kegiatan)
                            if (jsonArray.length() > 0) jsonArray.getString(0) else null
                        } catch (e: Exception) { null }
                    }

                    // Tampilkan Gambar atau Placeholder
                    if (firstImage != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("${Constants.URL_GAMBAR_BASE}$firstImage")
                                .crossfade(true).build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.LightGray)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Event, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Kolom Teks
                    Column {
                        Text(
                            text = activity.judul_kegiatan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            maxLines = 2
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${activity.tanggal} • ${activity.waktu}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = activity.lokasi,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- BAGIAN BAWAH: TOMBOL AKSI (Edit & Detail) ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Tombol EDIT
                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Edit")
                    }

                    // Tombol DETAIL
                    Button(
                        onClick = onDetail,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Detail")
                    }
                }
            }

            // --- TOMBOL HAPUS (Pojok Kanan Atas) ---
            TextButton(
                onClick = onDelete,
                modifier = Modifier.align(Alignment.TopEnd),
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
            ) {
                Text("Hapus", fontSize = 12.sp)
            }
        }
    }
}