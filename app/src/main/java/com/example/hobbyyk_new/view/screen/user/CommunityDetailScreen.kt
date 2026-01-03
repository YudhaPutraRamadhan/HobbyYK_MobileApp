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

    LaunchedEffect(communityId) {
        viewModel.getDetail(communityId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detail Komunitas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            if (!isAdminPreview && viewModel.community != null) {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.toggleLike(communityId) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(
                                1.dp,
                                if (viewModel.isLiked) Color.Red else MaterialTheme.colorScheme.outline
                            )
                        ) {
                            Icon(
                                imageVector = if (viewModel.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Like",
                                tint = if (viewModel.isLiked) Color.Red else Color.Gray
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${viewModel.likeCount} ${if (viewModel.isLiked) "Disukai" else "Suka"}",
                                color = if (viewModel.isLiked) Color.Red else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Button(
                            onClick = { viewModel.toggleJoin(communityId) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (viewModel.isJoined) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(text = if (viewModel.isJoined) "Keluar" else "Gabung")
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

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(modifier = Modifier.height(200.dp).fillMaxWidth()) {
                        AsyncImage(
                            model = "${Constants.URL_GAMBAR_BASE}${data.banner_url ?: data.foto_url}",
                            contentDescription = "Banner",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = "${Constants.URL_GAMBAR_BASE}${data.foto_url}",
                                contentDescription = "Logo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray)
                            )
                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = data.nama_komunitas,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    Text(text = "${data.lokasi} • ", color = Color.Gray, fontSize = 12.sp)

                                    Icon(Icons.Default.Group, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    Text(text = " ${viewModel.memberCount} Anggota", color = Color.Gray, fontSize = 12.sp)

                                    Text(text = " • ", color = Color.Gray, fontSize = 12.sp)

                                    Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                                    Text(text = " ${viewModel.likeCount} Suka", color = Color.Gray, fontSize = 12.sp)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = data.kategori, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(text = "Tentang Komunitas", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = data.deskripsi, lineHeight = 24.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Spacer(modifier = Modifier.height(24.dp))

                        if (data.link_grup.isNotEmpty()) {
                            OutlinedButton(
                                onClick = {
                                    try {
                                        val url = data.link_grup
                                        if (url.startsWith("http://") || url.startsWith("https://")) {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                            context.startActivity(intent)
                                        } else {
                                            Toast.makeText(context, "Link tidak valid! Harus diawali http:// atau https://", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Gagal membuka link: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF25D366))
                            ) {
                                Text("Gabung Grup WhatsApp", fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        Text(text = "Kontak Person", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 80.dp) // Padding bawah biar gak ketutup bottom bar
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = "Telepon", tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(text = "Nomor Admin", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = data.kontak, fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }
            } else {
                Text("Gagal memuat data", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}