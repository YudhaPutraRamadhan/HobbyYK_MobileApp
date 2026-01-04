package com.example.hobbyyk_new.view.screen.admin.activity

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.hobbyyk_new.utils.Constants
import com.example.hobbyyk_new.viewmodel.ActivityViewModel
import org.json.JSONArray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDetailScreen(
    navController: NavController,
    activityId: Int
) {
    val viewModel: ActivityViewModel = viewModel()
    var selectedImageForFullscreen by remember { mutableStateOf<String?>(null) }
    val blurRadius by animateDpAsState(
        targetValue = if (selectedImageForFullscreen != null) 15.dp else 0.dp,
        label = "blur"
    )

    LaunchedEffect(activityId) {
        viewModel.getActivityDetail(activityId)
    }

    // Fullscreen Image View (Tetap mempertahankan logika aslimu)
    if (selectedImageForFullscreen != null) {
        Dialog(
            onDismissRequest = { selectedImageForFullscreen = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.9f)).clickable { selectedImageForFullscreen = null },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = "${Constants.URL_GAMBAR_BASE}$selectedImageForFullscreen",
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    Scaffold(
        modifier = Modifier.blur(blurRadius),
        topBar = {
            TopAppBar(
                title = { Text("Detail Aktivitas", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (viewModel.selectedActivity != null) {
                val data = viewModel.selectedActivity!!
                val images = remember(data.foto_kegiatan) {
                    try {
                        val jsonArray = JSONArray(data.foto_kegiatan)
                        List(jsonArray.length()) { jsonArray.getString(it) }
                    } catch (e: Exception) { emptyList() }
                }

                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                ) {
                    // Modern Gallery Grid
                    if (images.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().height(220.dp).padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            images.forEach { imgName ->
                                AsyncImage(
                                    model = "${Constants.URL_GAMBAR_BASE}$imgName",
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { selectedImageForFullscreen = imgName }
                                )
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                        Text(
                            text = data.judul_kegiatan,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 32.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Info Cards Section
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                DetailInfoRow(Icons.Default.CalendarToday, "Tanggal", data.tanggal)
                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)
                                DetailInfoRow(Icons.Default.AccessTime, "Waktu", data.waktu)
                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)
                                DetailInfoRow(Icons.Default.LocationOn, "Lokasi", data.lokasi)
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Text("Deskripsi Kegiatan", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(
                            text = data.deskripsi,
                            modifier = Modifier.padding(top = 12.dp, bottom = 40.dp),
                            lineHeight = 24.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 11.sp, color = Color.Gray)
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }
    }
}