package com.example.hobbyyk_new.view.screen.admin

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.hobbyyk_new.viewmodel.ActivityViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityFormScreen(
    navController: NavController,
    communityId: Int,
    activityId: Int? = null
) {
    val viewModel: ActivityViewModel = viewModel()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var judul by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var lokasi by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") }
    var waktu by remember { mutableStateOf("") }
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }

    LaunchedEffect(activityId) {
        if (activityId != null && activityId != 0) {
            viewModel.getActivityDetail(activityId)
        }
    }

    LaunchedEffect(viewModel.selectedActivity) {
        if (activityId != null && activityId != 0) {
            viewModel.selectedActivity?.let {
                judul = it.judul_kegiatan
                deskripsi = it.deskripsi
                lokasi = it.lokasi
                tanggal = it.tanggal
                waktu = it.waktu
            }
        }
    }

    LaunchedEffect(viewModel.successMessage) {
        viewModel.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
            navController.popBackStack()
        }
    }

    LaunchedEffect(viewModel.errorMessage) {
        viewModel.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessages()
        }
    }

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            val formattedDate = String.format("%04d-%02d-%02d", year, month + 1, day)
            tanggal = formattedDate
        },
        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour, minute ->
            val formattedTime = String.format("%02d:%02d:00", hour, minute)
            waktu = formattedTime
        },
        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true
    )

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.size > 2) {
            Toast.makeText(context, "Maksimal 2 foto!", Toast.LENGTH_SHORT).show()
            selectedImages = uris.take(2)
        } else {
            selectedImages = uris
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (activityId != null && activityId != 0) "Edit Aktivitas" else "Buat Aktivitas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (viewModel.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                Text("Foto Kegiatan (Max 2)", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.LightGray.copy(alpha = 0.3f))
                            .clickable { imageLauncher.launch("image/*") }
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Color.Gray)
                            Text("${selectedImages.size}/2", fontSize = 12.sp, color = Color.Gray)
                        }
                    }

                    selectedImages.forEach { uri ->
                        Box(modifier = Modifier.size(100.dp)) {
                            AsyncImage(
                                model = uri,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        }
                    }
                }
                if (activityId != null && activityId != 0 && selectedImages.isEmpty()) {
                    Text("Biarkan kosong jika tidak ingin mengubah foto.", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top=4.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = judul, onValueChange = { judul = it },
                    label = { Text("Judul Kegiatan") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = lokasi, onValueChange = { lokasi = it },
                    label = { Text("Lokasi") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = tanggal, onValueChange = {},
                    label = { Text("Tanggal (YYYY-MM-DD)") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().clickable { datePickerDialog.show() }
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = waktu, onValueChange = {},
                    label = { Text("Waktu (HH:MM)") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { timePickerDialog.show() }) {
                            Icon(Icons.Default.AccessTime, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().clickable { timePickerDialog.show() }
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = deskripsi, onValueChange = { deskripsi = it },
                    label = { Text("Deskripsi Lengkap") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (judul.isNotEmpty() && tanggal.isNotEmpty()) {

                            if (activityId == null || activityId == 0) {
                                viewModel.createActivity(
                                    communityId = communityId,
                                    judul = judul,
                                    deskripsi = deskripsi,
                                    lokasi = lokasi,
                                    tanggal = tanggal,
                                    waktu = waktu,
                                    imageUris = selectedImages,
                                    context = context
                                )
                            } else {
                                viewModel.updateActivity(
                                    id = activityId,
                                    communityId = communityId,
                                    judul = judul,
                                    deskripsi = deskripsi,
                                    lokasi = lokasi,
                                    tanggal = tanggal,
                                    waktu = waktu,
                                    imageUris = selectedImages,
                                    context = context
                                )
                            }
                        } else {
                            Toast.makeText(context, "Lengkapi Judul dan Tanggal!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text(text = if (activityId != null && activityId != 0) "Simpan Perubahan" else "Buat Kegiatan")
                }
            }
        }
    }
}