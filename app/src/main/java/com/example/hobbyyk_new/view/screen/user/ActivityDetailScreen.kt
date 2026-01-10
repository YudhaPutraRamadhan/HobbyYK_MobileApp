package com.example.hobbyyk_new.view.screen.admin.activity

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
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
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.95f)).clickable { selectedImageForFullscreen = null },
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
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Detail Aktivitas", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.Black)
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFFFF6B35))
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
                    if (images.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().height(240.dp).padding(24.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            images.take(3).forEach { imgName ->
                                AsyncImage(
                                    model = "${Constants.URL_GAMBAR_BASE}$imgName",
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(Color(0xFFF5F5F5))
                                        .shadow(4.dp)
                                        .clickable { selectedImageForFullscreen = imgName }
                                )
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        Text(
                            text = data.judul_kegiatan,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1A1A1A),
                            lineHeight = 34.sp
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        Surface(
                            color = Color(0xFFFAFAFA),
                            shape = RoundedCornerShape(24.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                DetailInfoRow(Icons.Default.CalendarToday, "Tanggal", data.tanggal, Color(0xFFFF6B35))
                                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 1.dp, color = Color(0xFFF0F0F0))
                                DetailInfoRow(Icons.Default.AccessTime, "Waktu", data.waktu, Color(0xFF4361EE))
                                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 1.dp, color = Color(0xFFF0F0F0))
                                DetailInfoRow(Icons.Default.LocationOn, "Lokasi", data.lokasi, Color(0xFF2E7D32))
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Text("Deskripsi Kegiatan", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color(0xFF1A1A1A))
                        Text(
                            text = data.deskripsi,
                            modifier = Modifier.padding(top = 12.dp, bottom = 40.dp),
                            lineHeight = 24.sp,
                            color = Color(0xFF616161),
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailInfoRow(icon: ImageVector, label: String, value: String, accentColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(40.dp).background(accentColor.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = accentColor, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
        }
    }
}