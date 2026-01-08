package com.example.hobbyyk_new.view.screen.user

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hobbyyk_new.data.datastore.UserStore
import com.example.hobbyyk_new.view.components.SpotlightOverlay
import com.example.hobbyyk_new.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel) {
    var step by remember { mutableStateOf(1) }
    var targetKomunitas by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var targetAktivitas by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var targetProfil by remember { mutableStateOf<LayoutCoordinates?>(null) }

    // Bungkus SEMUA dengan Box agar Overlay bisa menumpuk di atas Scaffold
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color(0xFFFAFAFA),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "HobbyYK",
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp,
                            color = Color(0xFFFF6B35),
                            fontSize = 24.sp
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = { navController.navigate("profile") },
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .onGloballyPositioned { targetProfil = it } // Menangkap Profil
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(Color(0xFFFF6B35), Color(0xFFFF9431))
                                    ),
                                    shape = CircleShape
                                )
                                .size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profil Saya",
                                modifier = Modifier.size(20.dp),
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text("Halo, Sobat Hobi!", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color(0xFF1A1A1A))
                Text("Eksplorasi keseruan komunitas di Yogyakarta hari ini.", fontSize = 15.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp, bottom = 32.dp))

                MenuCard(
                    modifier = Modifier.onGloballyPositioned { targetKomunitas = it }, // Kirim modifier
                    title = "Eksplor Komunitas",
                    subtitle = "Temukan teman sehobi di Jogja",
                    icon = Icons.Default.Groups,
                    color = Color(0xFFFF6B35),
                    onClick = { navController.navigate("community_list") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                MenuCard(
                    modifier = Modifier.onGloballyPositioned { targetAktivitas = it }, // Kirim modifier
                    title = "Aktivitas Terbaru",
                    subtitle = "Lihat Aktivitas seru Komunitas mu",
                    icon = Icons.Default.Event,
                    color = Color(0xFF4361EE),
                    onClick = { navController.navigate("activity_feed") }
                )
            }
        }

        // Overlay diletakkan di dalam Box, tapi di luar Scaffold agar menimpa semuanya
        if (viewModel.isFirstTime) {
            val (currentCoords, text, isLast) = when(step) {
                1 -> Triple(targetKomunitas, "Temukan teman sehobi di Jogja lewat sini!", false)
                2 -> Triple(targetAktivitas, "Pantau aktivitas seru komunitasmu di sini.", false)
                else -> Triple(targetProfil, "Atur profil dan ganti password di sini.", true)
            }

            SpotlightOverlay(
                targetCoordinates = currentCoords,
                text = text,
                onNext = { if(isLast) viewModel.completeTutorial() else step++ },
                onSkip = { viewModel.completeTutorial() },
                isLastStep = isLast
            )
        }
    }
}

@Composable
fun MenuCard(title: String, subtitle: String, icon: ImageVector, color: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(color, color.copy(alpha = 0.8f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = Color(0xFF757575),
                    lineHeight = 16.sp
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = null,
                tint = Color(0xFFBDBDBD),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}