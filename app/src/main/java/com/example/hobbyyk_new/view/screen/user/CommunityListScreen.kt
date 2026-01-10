package com.example.hobbyyk_new.view.screen.user

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.example.hobbyyk_new.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityListScreen(navController: NavController) {
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
    val categories = listOf("Semua") + Constants.COMMUNITY_CATEGORIES

    LaunchedEffect(viewModel.searchQuery, viewModel.selectedCategory) {
        viewModel.fetchCommunities(
            query = viewModel.searchQuery.ifEmpty { null },
            category = viewModel.selectedCategory
        )
    }

    Scaffold(
        containerColor = Color(0xFFFAFAFA),
        topBar = {
            TopAppBar(
                title = { Text("Eksplor Komunitas", fontWeight = FontWeight.Black, fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color(0xFF1A1A1A))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Search Bar Modernized
            Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                OutlinedTextField(
                    value = viewModel.searchQuery,
                    onValueChange = { viewModel.searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Cari hobi atau komunitas...", color = Color.Gray) },
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
                        focusedLabelColor = Color(0xFFFF6B35),
                        cursorColor = Color(0xFFFF6B35)
                    )
                )
            }

            // Categories Row
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(categories) { category ->
                    val isSelected = viewModel.selectedCategory == category

                    Surface(
                        onClick = { viewModel.selectedCategory = category },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) Color(0xFFFF6B35) else Color.White,
                        border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFEEEEEE)),
                        modifier = Modifier.shadow(if (isSelected) 8.dp else 0.dp, RoundedCornerShape(14.dp), spotColor = Color(0xFFFF6B35))
                    ) {
                        Text(
                            text = category,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else Color(0xFF757575),
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                        )
                    }
                }
            }

            // List Content
            Box(modifier = Modifier.fillMaxSize()) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFFFF6B35))
                } else if (viewModel.errorMessage != null) {
                    Text(viewModel.errorMessage!!, Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.error)
                } else if (viewModel.communities.isEmpty()) {
                    Text("Belum ada komunitas di kategori ini.", Modifier.align(Alignment.Center), color = Color.Gray)
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(24.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        items(viewModel.communities) { community ->
                            CommunityItem(
                                community = community,
                                onClick = { navController.navigate("community_detail/${community.id}") }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CommunityItem(community: Community, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFF0F0F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Box {
                val imageUrl = "${Constants.URL_GAMBAR_BASE}${community.banner_url ?: community.foto_url}"
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(imageUrl).crossfade(true).build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
                // Category Chip on top of image
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.padding(16.dp).align(Alignment.TopStart)
                ) {
                    Text(
                        text = community.kategori,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6B35),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = community.nama_komunitas,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1A1A1A)
                )

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 6.dp)) {
                    Icon(Icons.Default.LocationOn, null, tint = Color(0xFFFF6B35), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = community.lokasi, fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                }

                Text(
                    text = community.deskripsi,
                    fontSize = 14.sp,
                    color = Color(0xFF616161),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp
                )
            }
        }
    }
}