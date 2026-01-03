package com.example.hobbyyk_new.view.screen.user

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    val viewModel: HomeViewModel = viewModel()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Daftar Komunitas", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {

            if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage!!,
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(viewModel.communities) { community ->
                    CommunityItem(
                        community = community,
                        onClick = {
                            navController.navigate("community_detail/${community.id}")
                        }
                    )
                }
            }
        }
    }
}
@Composable
fun CommunityItem(
    community: Community,
    onClick: () -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier
            .fillMaxWidth()
            .clickable{ onClick() }
    ) {
        Column {
            val imageFilename = community.banner_url ?: community.foto_url

            val imageUrl = "${Constants.BASE_URL}uploads/$imageFilename"

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Banner Komunitas",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(180.dp)
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = community.nama_komunitas, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(text = "📍 ${community.lokasi}", style = MaterialTheme.typography.bodyMedium)
                Text(text = community.deskripsi, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
            }
        }
    }
}