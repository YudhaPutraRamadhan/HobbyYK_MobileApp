package com.example.hobbyyk_new.view.screen.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
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
import coil.request.ImageRequest
import com.example.hobbyyk_new.utils.Constants
import com.example.hobbyyk_new.viewmodel.AdminCommunityViewModel
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.hobbyyk_new.data.datastore.UserStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(navController: NavController) {
    val viewModel: AdminCommunityViewModel = viewModel()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userStore = UserStore(context)

    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchMyCommunity()
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Komunitas?") },
            text = { Text("Apakah Anda yakin ingin menghapus komunitas Anda secara permanen? Semua data anggota dan aktivitas akan hilang.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.myCommunity?.let { viewModel.deleteCommunity(it.id) }
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
            TopAppBar(
                title = { Text("Dashboard Admin") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues).fillMaxSize().padding(16.dp)
        ) {
            item {
                Text("Area Manajer", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
            }

            item {
                MyCommunityCard(
                    community = viewModel.myCommunity,
                    isLoading = viewModel.isLoading,
                    onCreateClick = { navController.navigate("create_community") },
                    onEditClick = { id -> navController.navigate("edit_community/$id") },
                    onDetailClick = { id ->
                        navController.navigate("detail_community/$id?isAdmin=true")
                    },
                    onDeleteClick = { showDeleteDialog = true }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            if (viewModel.myCommunity != null && !viewModel.isLoading) {
                item {
                    ActivityManagerCard(
                        onClick = {
                            navController.navigate("activity_list/${viewModel.myCommunity!!.id}")
                        }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            item {
                Text("Jelajahi Komunitas Lain", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Text("Fitur jelajah akan tampil di sini...", color = Color.Gray, fontSize = 14.sp)

                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            userStore.clearSession()
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                ) {
                    Text("Keluar (Logout)")
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun ActivityManagerCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Aktivitas Komunitas",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Kelola jadwal mabar, gathering, dll.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Icon(Icons.Default.Settings, contentDescription = null, tint = Color.Gray)
        }
    }
}

@Composable
fun MyCommunityCard(
    community: com.example.hobbyyk_new.data.model.Community?,
    isLoading: Boolean,
    onCreateClick: () -> Unit,
    onEditClick: (Int) -> Unit,
    onDetailClick: (Int) -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {

            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Komunitas Saya", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                else if (community == null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("Anda belum mengelola komunitas apapun.", color = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onCreateClick) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Text(" Buat Komunitas Baru")
                        }
                    }
                }
                else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val imageUrl = "${Constants.URL_GAMBAR_BASE}${community.foto_url}"
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current).data(imageUrl).build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)).background(Color.LightGray)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(community.nama_komunitas, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(community.kategori, fontSize = 14.sp, color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { onEditClick(community.id) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Kelola", fontSize = 12.sp)
                        }

                        Button(
                            onClick = { onDetailClick(community.id) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Detail", fontSize = 12.sp)
                        }
                    }
                }
            }

            if (community != null && !isLoading) {
                TextButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.align(Alignment.TopEnd).padding(end = 4.dp, top = 4.dp),
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Hapus Komunitas", fontSize = 12.sp)
                }
            }
        }
    }
}