package com.example.hobbyyk_new.view.screen.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.hobbyyk_new.data.model.Activity
import com.example.hobbyyk_new.utils.Constants
import com.example.hobbyyk_new.viewmodel.ActivityViewModel
import org.json.JSONArray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityFeedScreen(navController: NavController) {
    val viewModel: ActivityViewModel = viewModel()

    LaunchedEffect(Unit) {
        viewModel.fetchActivityFeed()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Aktivitas Terbaru", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (viewModel.feedList.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Belum ada aktivitas apapun.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(viewModel.feedList) { activity ->
                        FeedItemCard(
                            activity = activity,
                            onClick = { navController.navigate("detail_activity/${activity.id}")
                            },
                            onCommunityClick = { communityId ->
                                navController.navigate("community_detail/$communityId")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FeedItemCard(
    activity: Activity,
    onClick: () -> Unit,
    onCommunityClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            activity.community?.id?.let { id -> onCommunityClick(id) }
                        }
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Logo
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("${Constants.URL_GAMBAR_BASE}${activity.community?.foto_url}")
                            .crossfade(true).build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // Teks Nama & Tanggal
                    Column {
                        Text(
                            text = activity.community?.nama_komunitas ?: "Komunitas HobbyYk",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Diposting pada ${activity.tanggal}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // --- BODY: GAMBAR & KONTEN ---
            // Area ini otomatis ikut klik Card utama (onClick)
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {

                val firstImage = remember(activity.foto_kegiatan) {
                    try {
                        val jsonArray = JSONArray(activity.foto_kegiatan)
                        if (jsonArray.length() > 0) jsonArray.getString(0) else null
                    } catch (e: Exception) { null }
                }

                if (firstImage != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("${Constants.URL_GAMBAR_BASE}$firstImage")
                            .crossfade(true).build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.LightGray)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Text(
                    text = activity.judul_kegiatan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(activity.lokasi, fontSize = 12.sp, color = Color.Gray, maxLines = 1)

                    Spacer(modifier = Modifier.width(16.dp))

                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(activity.tanggal, fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}