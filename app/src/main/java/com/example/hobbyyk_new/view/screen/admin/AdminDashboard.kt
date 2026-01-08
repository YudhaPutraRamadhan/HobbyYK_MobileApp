package com.example.hobbyyk_new.view.screen.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.hobbyyk_new.data.model.Community
import com.example.hobbyyk_new.utils.Constants
import com.example.hobbyyk_new.view.components.SpotlightOverlay
import com.example.hobbyyk_new.viewmodel.AdminCommunityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(navController: NavController, viewModel: AdminCommunityViewModel) {
    var step by remember { mutableStateOf(1) }
    var targetWorkstation by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var targetInspirasi by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var targetProfil by remember { mutableStateOf<LayoutCoordinates?>(null) }

    val categories = listOf("Semua") + Constants.COMMUNITY_CATEGORIES
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchMyCommunity()
    }

    LaunchedEffect(viewModel.searchQuery, viewModel.selectedCategory, viewModel.myCommunity) {
        viewModel.fetchOtherCommunities()
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            shape = RoundedCornerShape(28.dp),
            title = { Text("Hapus Komunitas?", fontWeight = FontWeight.Black) },
            text = { Text("Tindakan ini permanen. Semua data anggota dan aktivitas akan dihapus dari sistem.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.myCommunity?.let { viewModel.deleteCommunity(it.id) }
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Ya, Hapus", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal", color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color(0xFFFAFAFA),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Admin Dashboard",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            letterSpacing = (-0.5).sp
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = { navController.navigate("profile") },
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .onGloballyPositioned { targetProfil = it }
                                .background(Color(0xFFFF6B35).copy(alpha = 0.1f), CircleShape)
                                .size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profil",
                                tint = Color(0xFFFF6B35),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
                    )
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Text(
                        text = "Kelola Komunitas",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1A1A1A)
                    )
                }

                item {
                    Box(modifier = Modifier.onGloballyPositioned { targetWorkstation = it }) {
                        MyCommunityCard(
                            community = viewModel.myCommunity,
                            isLoading = viewModel.isLoading,
                            onCreateClick = { navController.navigate("create_community") },
                            onEditClick = { id -> navController.navigate("edit_community/$id") },
                            onDetailClick = { id -> navController.navigate("admin_community_detail/$id") },
                            onDeleteClick = { showDeleteDialog = true }
                        )
                    }
                }

                if (viewModel.myCommunity != null && !viewModel.isLoading) {
                    item {
                        ActivityManagerCard(
                            onClick = {
                                navController.navigate("activity_list/${viewModel.myCommunity!!.id}")
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item {
                    Column(modifier = Modifier.onGloballyPositioned { targetInspirasi = it }) {
                        Text(
                            "Inspirasi Komunitas",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1A1A1A)
                        )

                        OutlinedTextField(
                            value = viewModel.searchQuery,
                            onValueChange = { viewModel.searchQuery = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            placeholder = { Text("Cari referensi komunitas...", color = Color.Gray) },
                            leadingIcon = { Icon(Icons.Default.Search, null, tint = Color(0xFFFF6B35)) },
                            trailingIcon = {
                                if (viewModel.searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.searchQuery = "" }) {
                                        Icon(Icons.Default.Close, null, tint = Color.Gray)
                                    }
                                }
                            },
                            shape = RoundedCornerShape(20.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFFF6B35),
                                unfocusedBorderColor = Color(0xFFEEEEEE),
                                cursorColor = Color(0xFFFF6B35)
                            )
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(categories) { category ->
                                val isSelected = viewModel.selectedCategory == category
                                Surface(
                                    onClick = { viewModel.selectedCategory = category },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) Color(0xFFFF6B35) else Color.White,
                                    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
                                ) {
                                    Text(
                                        category,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else Color.Gray,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                if (viewModel.otherCommunities.isEmpty() && !viewModel.isLoading) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("Belum ada komunitas lain.", color = Color.LightGray, fontWeight = FontWeight.Medium)
                        }
                    }
                } else {
                    items(viewModel.otherCommunities) { community ->
                        AdminExploreCommunityItem(
                            community = community,
                            onClick = { navController.navigate("community_detail/${community.id}") }
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }

        if (viewModel.isFirstTime) {
            val (currentCoords, text, isLast) = when (step) {
                1 -> Triple(targetWorkstation, "Ini adalah ruang kerjamu. Kelola komunitas dan buat aktivitas baru untuk anggota di sini.", false)
                2 -> Triple(targetInspirasi, "Kamu juga tetap bisa melihat komunitas lain untuk referensi atau mencari komunitas teman.", false)
                else -> Triple(targetProfil, "Pengaturan akun admin kamu bisa diakses di sini.", true)
            }

            SpotlightOverlay(
                targetCoordinates = currentCoords,
                text = text,
                onNext = {
                    if (isLast) viewModel.completeTutorial() else step++
                },
                onSkip = { viewModel.completeTutorial() },
                isLastStep = isLast
            )
        }
    }
}

@Composable
fun MyCommunityCard(
    community: Community?,
    isLoading: Boolean,
    onCreateClick: () -> Unit,
    onEditClick: (Int) -> Unit,
    onDetailClick: (Int) -> Unit,
    onDeleteClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE)),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            if (isLoading) {
                Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFFF6B35), strokeWidth = 3.dp)
                }
            } else if (community == null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
                ) {
                    Box(
                        modifier = Modifier.size(60.dp).background(Color(0xFFF5F5F5), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Groups, null, tint = Color.LightGray, modifier = Modifier.size(30.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Belum memiliki komunitas", color = Color.Gray, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onCreateClick,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B35)),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Buat Komunitas Sekarang", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFF5F5F5))
                    ) {
                        AsyncImage(
                            model = "${Constants.URL_GAMBAR_BASE}${community.foto_url}",
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text(community.nama_komunitas, fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color(0xFF1A1A1A))
                        Spacer(Modifier.height(4.dp))
                        Surface(
                            color = Color(0xFFFF6B35).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                community.kategori,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF6B35),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.background(Color(0xFFFFEBEE), CircleShape).size(36.dp)
                    ) {
                        Icon(Icons.Default.Delete, null, tint = Color(0xFFE53935), modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = { onEditClick(community.id) },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFEEEEEE)),

                    ) {
                        Icon(Icons.Default.Settings, null, Modifier.size(18.dp), tint = Color(0xFF424242))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Kelola", fontWeight = FontWeight.Bold, color = Color(0xFF424242))
                    }

                    Button(
                        onClick = { onDetailClick(community.id) },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4361EE))
                    ) {
                        Icon(Icons.Default.Visibility, null, Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Preview", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityManagerCard(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE)),
        shadowElevation = 2.dp

    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFF4361EE), Color(0xFF4895EF)))),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Event, null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text("Manajemen Aktivitas", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A1A1A))
                Text("Buat jadwal & post kegiatan seru", fontSize = 12.sp, color = Color.Gray)
            }
            Icon(Icons.Default.ArrowForwardIos, null, modifier = Modifier.size(14.dp), tint = Color(0xFFBDBDBD))
        }
    }
}

@Composable
fun AdminExploreCommunityItem(community: Community, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F0F0)),
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 2.dp
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(70.dp).clip(RoundedCornerShape(14.dp))) {
                AsyncImage(
                    model = "${Constants.URL_GAMBAR_BASE}${community.banner_url ?: community.foto_url}",
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(community.nama_komunitas, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A1A1A))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = Color(0xFFFF6B35), modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(community.lokasi, fontSize = 12.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        community.kategori,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}