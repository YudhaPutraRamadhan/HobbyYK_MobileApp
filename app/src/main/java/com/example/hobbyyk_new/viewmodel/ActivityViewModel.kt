package com.example.hobbyyk_new.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hobbyyk_new.data.api.RetrofitClient
import com.example.hobbyyk_new.data.model.Activity
import com.example.hobbyyk_new.utils.uriToFile
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class ActivityViewModel : ViewModel() {
    var activityList by mutableStateOf<List<Activity>>(emptyList())

    var selectedActivity by mutableStateOf<Activity?>(null)

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var successMessage by mutableStateOf<String?>(null)

    var feedList = mutableStateListOf<Activity>()

    fun getActivities(communityId: Int) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.instance.getActivitiesByCommunity(communityId)
                if (response.isSuccessful) {
                    activityList = response.body() ?: emptyList()
                } else {
                    errorMessage = "Gagal memuat aktivitas"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun getActivityDetail(id: Int) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.instance.getActivityById(id)
                if (response.isSuccessful) {
                    selectedActivity = response.body()
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun createActivity(
        communityId: Int,
        judul: String, deskripsi: String, lokasi: String,
        tanggal: String, waktu: String,
        imageUris: List<Uri>,
        context: Context
    ) {
        val alphaSpaceRegex = Regex("^[a-zA-Z\\s]*$")
        if (!judul.matches(alphaSpaceRegex) || !lokasi.matches(alphaSpaceRegex)) {
            errorMessage = "Judul dan Lokasi hanya boleh berisi huruf!"
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                val judulPart = judul.toRequestBody("text/plain".toMediaTypeOrNull())
                val deskPart = deskripsi.toRequestBody("text/plain".toMediaTypeOrNull())
                val lokasiPart = lokasi.toRequestBody("text/plain".toMediaTypeOrNull())
                val tanggalPart = tanggal.toRequestBody("text/plain".toMediaTypeOrNull())
                val waktuPart = waktu.toRequestBody("text/plain".toMediaTypeOrNull())
                val commIdPart = communityId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                val imageParts = mutableListOf<MultipartBody.Part>()
                imageUris.forEachIndexed { index, uri ->
                    val file = uriToFile(uri, context)
                    val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val fileName = "act_${System.currentTimeMillis()}_$index.jpg"
                    val part = MultipartBody.Part.createFormData("foto_kegiatan", fileName, requestFile)
                    imageParts.add(part)
                }

                val response = RetrofitClient.instance.createActivity(
                    judulPart, deskPart, lokasiPart, tanggalPart, waktuPart, commIdPart, imageParts
                )

                if (response.isSuccessful) {
                    successMessage = "Aktivitas berhasil dibuat!"
                    getActivities(communityId)
                } else {
                    val errorJson = response.errorBody()?.string()
                    errorMessage = try {
                        JSONObject(errorJson!!).optString("msg", "Gagal membuat aktivitas")
                    } catch (e: Exception) {
                        "Gagal: ${response.message()}"
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteActivity(id: Int, communityId: Int) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.instance.deleteActivity(id)
                if (response.isSuccessful) {
                    successMessage = "Aktivitas dihapus"
                    getActivities(communityId)
                }
            } catch (e: Exception) {
                errorMessage = "Gagal hapus: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun updateActivity(
        id: Int,
        communityId: Int,
        judul: String, deskripsi: String, lokasi: String,
        tanggal: String, waktu: String,
        imageUris: List<Uri>,
        context: Context
    ) {
        val alphaSpaceRegex = Regex("^[a-zA-Z\\s]*$")
        if (!judul.matches(alphaSpaceRegex) || !lokasi.matches(alphaSpaceRegex)) {
            errorMessage = "Judul dan Lokasi hanya boleh berisi huruf!"
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                val judulPart = judul.toRequestBody("text/plain".toMediaTypeOrNull())
                val deskPart = deskripsi.toRequestBody("text/plain".toMediaTypeOrNull())
                val lokasiPart = lokasi.toRequestBody("text/plain".toMediaTypeOrNull())
                val tanggalPart = tanggal.toRequestBody("text/plain".toMediaTypeOrNull())
                val waktuPart = waktu.toRequestBody("text/plain".toMediaTypeOrNull())

                val imageParts = if (imageUris.isNotEmpty()) {
                    val parts = mutableListOf<MultipartBody.Part>()
                    imageUris.forEachIndexed { index, uri ->
                        val file = uriToFile(uri, context)
                        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        val fileName = "update_${System.currentTimeMillis()}_$index.jpg"
                        parts.add(MultipartBody.Part.createFormData("foto_kegiatan", fileName, requestFile))
                    }
                    parts
                } else {
                    null
                }

                val response = RetrofitClient.instance.updateActivity(
                    id, judulPart, deskPart, lokasiPart, tanggalPart, waktuPart, imageParts
                )

                if (response.isSuccessful) {
                    successMessage = "Aktivitas berhasil diperbarui!"
                    getActivities(communityId)
                } else {
                    val errorJson = response.errorBody()?.string()
                    errorMessage = try {
                        JSONObject(errorJson!!).optString("msg", "Gagal memperbarui aktivitas")
                    } catch (e: Exception) {
                        "Gagal update: ${response.message()}"
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchActivityFeed() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.instance.getAllActivities()
                if (response.isSuccessful) {
                    response.body()?.let {
                        feedList.clear()
                        feedList.addAll(it)
                    }
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun clearMessages() {
        errorMessage = null
        successMessage = null
    }
}