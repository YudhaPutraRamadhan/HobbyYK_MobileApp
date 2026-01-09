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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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

    LaunchedEffect(viewModel.message) {
        viewModel.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.message = null // Reset agar tidak muncul berulang
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Detail Komunitas", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            if (!isAdminPreview && viewModel.community != null && userRole == "user") {
                Surface(
                    color = Color.White,
                    modifier = Modifier.shadow(24.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp).fillMaxWidth().navigationBarsPadding(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            onClick = {
                                if (!viewModel.isLoading) viewModel.toggleLike(communityId)
                            },
                            shape = RoundedCornerShape(16.dp),
                            color = if (viewModel.isLiked) Color(0xFFFFEBEE) else Color(0xFFF5F5F5),
                            modifier = Modifier
                                .height(56.dp)
                                .wrapContentWidth()
                                .padding(end = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = if (viewModel.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = null,
                                    tint = if (viewModel.isLiked) Color(0xFFE53935) else Color.Gray,
                                    modifier = Modifier.size(22.dp)
                                )

                                if (viewModel.isLiked) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${viewModel.likeCount}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFFE53935)
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = { viewModel.toggleJoin(communityId) },
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            enabled = !viewModel.isLoading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (viewModel.isJoined) Color(0xFFE53935) else Color(0xFFFF6B35)
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Text(
                                if (viewModel.isJoined) "Keluar Komunitas" else "Gabung Sekarang",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (viewModel.isLoading && viewModel.community == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFFFF6B35))
            } else if (viewModel.community != null) {
                val data = viewModel.community!!
                Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

                    Box(modifier = Modifier.height(260.dp).fillMaxWidth()) {
                        AsyncImage(
                            model = "${Constants.URL_GAMBAR_BASE}${data.banner_url ?: data.foto_url}",
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.height(200.dp).fillMaxWidth()
                        )
                        Box(modifier = Modifier.height(200.dp).fillMaxWidth().background(
                            Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f)))
                        ))

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(start = 24.dp, bottom = 10.dp)
                                .size(100.dp)
                                .shadow(8.dp, CircleShape)
                                .clip(CircleShape)
                                .background(Color.White)
                                .padding(4.dp)
                                .clip(CircleShape)
                        ) {
                            AsyncImage(
                                model = "${Constants.URL_GAMBAR_BASE}${data.foto_url}",
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        Text(
                            text = data.nama_komunitas,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1A1A1A),
                            lineHeight = 34.sp
                        )

                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 12.dp)) {
                            BadgeItem(Icons.Default.LocationOn, data.lokasi, Color(0xFFFF6B35))
                            Spacer(modifier = Modifier.width(16.dp))
                            BadgeItem(Icons.Default.Group, "${viewModel.memberCount} Anggota", Color(0xFF4361EE))
                            Spacer(modifier = Modifier.width(16.dp))
                            BadgeItem(icon = Icons.Default.Favorite, label = "${viewModel.likeCount} Suka", color = Color(0xFFE53935)
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp), thickness = 1.dp, color = Color(0xFFF5F5F5))

                        Text("Tentang Komunitas", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color(0xFF1A1A1A))
                        Text(
                            text = data.deskripsi,
                            modifier = Modifier.padding(top = 8.dp),
                            color = Color(0xFF616161),
                            lineHeight = 24.sp,
                            fontSize = 15.sp
                        )

                        if (data.link_grup.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(32.dp))
                            Button(
                                onClick = {
                                    val url = data.link_grup
                                    if (url.startsWith("http")) {
                                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                                    } else {
                                        Toast.makeText(context, "Link tidak valid", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(60.dp),
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                            ) {
                                Icon(Icons.Default.Info, null, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Gabung Grup WhatsApp", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                        Text("Hubungi Admin", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color(0xFF1A1A1A))

                        Surface(
                            modifier = Modifier.padding(top = 12.dp, bottom = 40.dp).fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFFF8F9FA),
                            border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                        ) {
                            Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(44.dp).background(Color(0xFFFF6B35).copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Phone, null, tint = Color(0xFFFF6B35), modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text("WhatsApp / Telepon", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                                    Text(data.kontak, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A1A1A))
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
fun BadgeItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.background(color.copy(alpha = 0.1f), RoundedCornerShape(8.dp)).padding(horizontal = 10.dp, vertical = 6.dp)) {
        Icon(icon, null, tint = color, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, fontSize = 12.sp, color = color, fontWeight = FontWeight.Bold)
    }
}