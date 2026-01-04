package com.example.hobbyyk_new.view.screen.user

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.hobbyyk_new.data.datastore.UserStore
import com.example.hobbyyk_new.utils.Constants
import com.example.hobbyyk_new.viewmodel.CommunityDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityDetailScreen(
    navController: NavController,
    communityId: Int,
    isAdminPreview: Boolean = false
) {
    val viewModel: CommunityDetailViewModel = viewModel()
    val context = LocalContext.current
    val userStore = remember { UserStore(context) }
    val userRole by userStore.userRole.collectAsState(initial = null)

    LaunchedEffect(communityId) { viewModel.getDetail(communityId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        bottomBar = {
            if (!isAdminPreview && viewModel.community != null && userRole == "user") {
                Surface(
                    tonalElevation = 12.dp,
                    shadowElevation = 12.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.toggleLike(communityId) },
                            modifier = Modifier.weight(0.4f).height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, if (viewModel.isLiked) Color.Red else Color.LightGray)
                        ) {
                            Icon(
                                imageVector = if (viewModel.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = if (viewModel.isLiked) Color.Red else Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Button(
                            onClick = { viewModel.toggleJoin(communityId) },
                            modifier = Modifier.weight(0.6f).height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (viewModel.isJoined) Color.Red else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(if (viewModel.isJoined) "Keluar Komunitas" else "Gabung Sekarang", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (viewModel.community != null) {
                val data = viewModel.community!!
                Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                    Box(modifier = Modifier.height(240.dp).fillMaxWidth()) {
                        AsyncImage(
                            model = "${Constants.URL_GAMBAR_BASE}${data.banner_url ?: data.foto_url}",
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.height(180.dp).fillMaxWidth()
                        )
                        AsyncImage(
                            model = "${Constants.URL_GAMBAR_BASE}${data.foto_url}",
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(start = 24.dp)
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .padding(4.dp)
                                .clip(CircleShape)
                        )
                    }

                    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                        Text(text = data.nama_komunitas, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)

                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                            BadgeItem(Icons.Default.LocationOn, data.lokasi)
                            Spacer(modifier = Modifier.width(12.dp))
                            BadgeItem(Icons.Default.Group, "${viewModel.memberCount} Anggota")
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text("Tentang Komunitas", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(
                            text = data.deskripsi,
                            modifier = Modifier.padding(top = 8.dp),
                            color = Color.DarkGray,
                            lineHeight = 24.sp
                        )

                        if (data.link_grup.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = {
                                    val url = data.link_grup
                                    if (url.startsWith("http")) {
                                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                                    } else {
                                        Toast.makeText(context, "Link tidak valid", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                            ) {
                                Text("Grup WhatsApp Komunitas", fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                        Text("Hubungi Admin", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Card(
                            modifier = Modifier.padding(top = 12.dp, bottom = 40.dp).fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Phone, null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text("WhatsApp / Telepon", fontSize = 12.sp, color = Color.Gray)
                                    Text(data.kontak, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BadgeItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, fontSize = 12.sp, color = Color.Gray)
    }
}