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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.hobbyyk_new.view.screen.auth.LabelText
import com.example.hobbyyk_new.viewmodel.ActivityViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityFormScreen(navController: NavController, communityId: Int, activityId: Int? = null) {
    val viewModel: ActivityViewModel = viewModel()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var judul by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var lokasi by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") }
    var waktu by remember { mutableStateOf("") }
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val alphaSpaceRegex = Regex("^[a-zA-Z\\s]*$")

    LaunchedEffect(activityId) {
        if (activityId != null && activityId != 0) viewModel.getActivityDetail(activityId)
    }

    LaunchedEffect(viewModel.selectedActivity) {
        if (activityId != null && activityId != 0) {
            viewModel.selectedActivity?.let {
                judul = it.judul_kegiatan; deskripsi = it.deskripsi
                lokasi = it.lokasi; tanggal = it.tanggal; waktu = it.waktu
            }
        }
    }

    LaunchedEffect(viewModel.successMessage) {
        viewModel.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages(); navController.popBackStack()
        }
    }

    LaunchedEffect(viewModel.errorMessage) {
        viewModel.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessages()
        }
    }

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(context, { _, y, m, d ->
        tanggal = String.format("%04d-%02d-%02d", y, m + 1, d)
    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

    val timePickerDialog = TimePickerDialog(context, { _, h, min ->
        waktu = String.format("%02d:%02d:00", h, min)
    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)

    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        selectedImages = uris.take(2)
        if (uris.size > 2) Toast.makeText(context, "Maksimal 2 foto terpilih!", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text(if (activityId != 0) "Edit Kegiatan" else "Kegiatan Baru", fontWeight = FontWeight.Black, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.Black)
                    }
                }
            )
        }
    ) { paddingValues ->
        if (viewModel.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFFF6B35))
            }
        } else {
            Column(modifier = Modifier.padding(paddingValues).fillMaxSize().padding(horizontal = 24.dp).verticalScroll(scrollState)) {
                Spacer(modifier = Modifier.height(24.dp))

                Text("Dokumentasi Kegiatan", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color(0xFF1A1A1A))
                Text("Unggah foto keseruan aktivitas Anda (Maks. 2 foto)", fontSize = 13.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(20.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Surface(
                        onClick = { imageLauncher.launch("image/*") },
                        modifier = Modifier.size(110.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = Color(0xFFF8F9FA),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Icon(Icons.Default.AddPhotoAlternate, null, tint = Color(0xFFFF6B35), modifier = Modifier.size(32.dp))
                            Spacer(Modifier.height(4.dp))
                            Text("${selectedImages.size}/2 Foto", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.Gray)
                        }
                    }

                    selectedImages.forEach { uri ->
                        AsyncImage(
                            model = uri, contentDescription = null, contentScale = ContentScale.Crop,
                            modifier = Modifier.size(110.dp).clip(RoundedCornerShape(24.dp)).shadow(2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                LabelText("Judul Kegiatan")
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = judul,
                    onValueChange = { if (it.isEmpty() || it.matches(alphaSpaceRegex)) judul = it }, // Filter Huruf
                    placeholder = { Text("Contoh: Kopdar Rutin Fotografi", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFFF6B35), unfocusedBorderColor = Color(0xFFEEEEEE))
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(Modifier.weight(1f)) {
                        LabelText("Tanggal")
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = tanggal, onValueChange = {}, readOnly = true,
                            trailingIcon = { IconButton(onClick = { datePickerDialog.show() }) { Icon(Icons.Default.CalendarToday, null, Modifier.size(18.dp), tint = Color(0xFFFF6B35)) } },
                            modifier = Modifier.fillMaxWidth().clickable { datePickerDialog.show() },
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFFF6B35), unfocusedBorderColor = Color(0xFFEEEEEE))
                        )
                    }
                    Column(Modifier.weight(1f)) {
                        LabelText("Waktu")
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = waktu, onValueChange = {}, readOnly = true,
                            trailingIcon = { IconButton(onClick = { timePickerDialog.show() }) { Icon(Icons.Default.AccessTime, null, Modifier.size(18.dp), tint = Color(0xFFFF6B35)) } },
                            modifier = Modifier.fillMaxWidth().clickable { timePickerDialog.show() },
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFFF6B35), unfocusedBorderColor = Color(0xFFEEEEEE))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                LabelText("Lokasi")
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = lokasi,
                    onValueChange = { if (it.isEmpty() || it.matches(alphaSpaceRegex)) lokasi = it }, // Filter Huruf
                    placeholder = { Text("Lokasi spesifik di Jogja", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFFF6B35), unfocusedBorderColor = Color(0xFFEEEEEE))
                )

                Spacer(modifier = Modifier.height(20.dp))

                LabelText("Deskripsi Kegiatan")
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = deskripsi, onValueChange = { deskripsi = it },
                    modifier = Modifier.fillMaxWidth(), minLines = 5, shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFFF6B35), unfocusedBorderColor = Color(0xFFEEEEEE))
                )

                Spacer(modifier = Modifier.height(48.dp))

                Button(
                    onClick = {
                        if (judul.isEmpty() || tanggal.isEmpty() || lokasi.isEmpty() || deskripsi.isEmpty()) {
                            Toast.makeText(context, "Mohon lengkapi semua data!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if ((activityId == null || activityId == 0) && selectedImages.isEmpty()) {
                            Toast.makeText(context, "Minimal unggah 1 foto kegiatan!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (activityId == null || activityId == 0) {
                            viewModel.createActivity(communityId, judul, deskripsi, lokasi, tanggal, waktu, selectedImages, context)
                        } else {
                            viewModel.updateActivity(activityId, communityId, judul, deskripsi, lokasi, tanggal, waktu, selectedImages, context)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp).shadow(12.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFFFF6B35)),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !viewModel.isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B35))
                ) {
                    Text(if (activityId != 0 && activityId != null) "Update Kegiatan" else "Publikasikan Sekarang", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}