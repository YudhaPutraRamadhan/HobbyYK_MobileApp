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

    if (selectedImageForFullscreen != null) {
        Dialog(
            onDismissRequest = { selectedImageForFullscreen = null },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        selectedImageForFullscreen = null
                    },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("${Constants.URL_GAMBAR_BASE}$selectedImageForFullscreen")
                        .crossfade(true).build(),
                    contentDescription = "Full Screen Image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    Scaffold(
        modifier = Modifier.blur(blurRadius),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detail Aktivitas") },
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
            } else if (viewModel.selectedActivity != null) {
                val data = viewModel.selectedActivity!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    val images = remember(data.foto_kegiatan) {
                        try {
                            val jsonArray = JSONArray(data.foto_kegiatan)
                            val list = mutableListOf<String>()
                            for (i in 0 until jsonArray.length()) {
                                list.add(jsonArray.getString(i))
                            }
                            list
                        } catch (e: Exception) { emptyList<String>() }
                    }

                    if (images.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            images.forEach { imgName ->
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data("${Constants.URL_GAMBAR_BASE}$imgName")
                                        .crossfade(true).build(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.LightGray)
                                        .clickable {
                                            selectedImageForFullscreen = imgName
                                        }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    Text(data.judul_kegiatan, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    InfoRow(Icons.Default.CalendarToday, data.tanggal)
                    InfoRow(Icons.Default.AccessTime, data.waktu)
                    InfoRow(Icons.Default.LocationOn, data.lokasi)

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                    Text("Deskripsi", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(data.deskripsi, lineHeight = 24.sp, color = Color.DarkGray)
                }
            } else {
                Text("Gagal memuat data", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, fontSize = 16.sp)
    }
}