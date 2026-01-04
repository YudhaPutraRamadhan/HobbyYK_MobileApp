package com.example.hobbyyk_new.view.screen.admin

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

    // Logika sinkronisasi data (Tetap utuh)
    LaunchedEffect(activityId) { if (activityId != null && activityId != 0) viewModel.getActivityDetail(activityId) }
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

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(context, { _, y, m, d -> tanggal = String.format("%04d-%02d-%02d", y, m + 1, d) },
        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
    val timePickerDialog = TimePickerDialog(context, { _, h, min -> waktu = String.format("%02d:%02d:00", h, min) },
        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)

    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        selectedImages = uris.take(2)
        if (uris.size > 2) Toast.makeText(context, "Maksimal 2 foto terpilih!", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (activityId != 0) "Edit Kegiatan" else "Kegiatan Baru", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (viewModel.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            Column(modifier = Modifier.padding(paddingValues).fillMaxSize().padding(horizontal = 24.dp).verticalScroll(scrollState)) {
                Spacer(modifier = Modifier.height(24.dp))

                Text("Dokumentasi", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(
                        onClick = { imageLauncher.launch("image/*") },
                        modifier = Modifier.size(100.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Icon(Icons.Default.AddPhotoAlternate, null, tint = MaterialTheme.colorScheme.primary)
                            Text("${selectedImages.size}/2", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    selectedImages.forEach { uri ->
                        AsyncImage(model = uri, contentDescription = null, contentScale = ContentScale.Crop,
                            modifier = Modifier.size(100.dp).clip(RoundedCornerShape(16.dp)))
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                LabelText("Judul Kegiatan")
                OutlinedTextField(value = judul, onValueChange = { judul = it }, placeholder = { Text("Nama event...") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(Modifier.weight(1f)) {
                        LabelText("Tanggal")
                        OutlinedTextField(
                            value = tanggal, onValueChange = {}, readOnly = true,
                            trailingIcon = { IconButton(onClick = { datePickerDialog.show() }) { Icon(Icons.Default.CalendarToday, null, Modifier.size(18.dp)) } },
                            modifier = Modifier.fillMaxWidth().clickable { datePickerDialog.show() },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    Column(Modifier.weight(1f)) {
                        LabelText("Waktu")
                        OutlinedTextField(
                            value = waktu, onValueChange = {}, readOnly = true,
                            trailingIcon = { IconButton(onClick = { timePickerDialog.show() }) { Icon(Icons.Default.AccessTime, null, Modifier.size(18.dp)) } },
                            modifier = Modifier.fillMaxWidth().clickable { timePickerDialog.show() },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LabelText("Lokasi")
                OutlinedTextField(value = lokasi, onValueChange = { lokasi = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))

                Spacer(modifier = Modifier.height(16.dp))

                LabelText("Deskripsi")
                OutlinedTextField(value = deskripsi, onValueChange = { deskripsi = it }, modifier = Modifier.fillMaxWidth(), minLines = 4, shape = RoundedCornerShape(12.dp))

                Spacer(modifier = Modifier.height(48.dp))

                Button(
                    onClick = {
                        if (judul.isNotEmpty() && tanggal.isNotEmpty()) {
                            if (activityId == null || activityId == 0) {
                                viewModel.createActivity(communityId, judul, deskripsi, lokasi, tanggal, waktu, selectedImages, context)
                            } else {
                                viewModel.updateActivity(activityId, communityId, judul, deskripsi, lokasi, tanggal, waktu, selectedImages, context)
                            }
                        } else { Toast.makeText(context, "Judul dan Tanggal wajib diisi!", Toast.LENGTH_SHORT).show() }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !viewModel.isLoading
                ) {
                    Text(if (activityId != 0) "Update Kegiatan" else "Publikasikan", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}