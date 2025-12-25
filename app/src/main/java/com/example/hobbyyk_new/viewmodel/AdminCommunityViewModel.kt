package com.example.hobbyyk_new.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hobbyyk_new.data.api.RetrofitClient
import com.example.hobbyyk_new.data.model.Community
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AdminCommunityViewModel : ViewModel() {
    var communities by mutableStateOf<List<Community>>(emptyList())
    var isLoading by mutableStateOf(true)
    var errorMessage by mutableStateOf<String?>(null)

    fun fetchCommunities() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.instance.getCommunities()
                if (response.isSuccessful) {
                    communities = response.body() ?: emptyList()
                } else {
                    errorMessage = "Gagal memuat: ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteCommunity(id: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.deleteCommunity(id)
                if (response.isSuccessful) {
                    communities = communities.filter { it.id != id }
                } else {
                    errorMessage = "Gagal menghapus: ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            }
        }
    }

    fun updateCommunity(
        id: Int, nama: String, lokasi: String, deskripsi: String,
        kategori: String, kontak: String, linkGrup: String,
        newLogoFile: File?,
        newBannerFile: File?
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val namaPart = nama.toRequestBody("text/plain".toMediaTypeOrNull())
                val lokasiPart = lokasi.toRequestBody("text/plain".toMediaTypeOrNull())
                val deskripsiPart = deskripsi.toRequestBody("text/plain".toMediaTypeOrNull())
                val kategoriPart = kategori.toRequestBody("text/plain".toMediaTypeOrNull())
                val kontakPart = kontak.toRequestBody("text/plain".toMediaTypeOrNull())
                val linkGrupPart = linkGrup.toRequestBody("text/plain".toMediaTypeOrNull())

                var logoMultipart: MultipartBody.Part? = null
                if (newLogoFile != null) {
                    val requestLogo = newLogoFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    logoMultipart = MultipartBody.Part.createFormData("file", newLogoFile.name, requestLogo)
                }

                var bannerMultipart: MultipartBody.Part? = null
                if (newBannerFile != null) {
                    val requestBanner = newBannerFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    bannerMultipart = MultipartBody.Part.createFormData("banner", newBannerFile.name, requestBanner)
                }

                val response = RetrofitClient.instance.updateCommunity(
                    id, namaPart, lokasiPart, deskripsiPart, kategoriPart, kontakPart, linkGrupPart,
                    logoMultipart, bannerMultipart
                )

                if (response.isSuccessful) {
                    fetchCommunities()
                } else {
                    errorMessage = "Gagal update: ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun createCommunity(
        nama: String, lokasi: String, deskripsi: String,
        kategori: String, kontak: String, linkGrup: String,
        logoFile: File, bannerFile: File
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val namaPart = nama.toRequestBody("text/plain".toMediaTypeOrNull())
                val lokasiPart = lokasi.toRequestBody("text/plain".toMediaTypeOrNull())
                val deskripsiPart = deskripsi.toRequestBody("text/plain".toMediaTypeOrNull())
                val kategoriPart = kategori.toRequestBody("text/plain".toMediaTypeOrNull())
                val kontakPart = kontak.toRequestBody("text/plain".toMediaTypeOrNull())
                val linkGrupPart = linkGrup.toRequestBody("text/plain".toMediaTypeOrNull())

                val requestLogo = logoFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val logoMultipart = MultipartBody.Part.createFormData("file", logoFile.name, requestLogo)

                val requestBanner = bannerFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val bannerMultipart = MultipartBody.Part.createFormData("banner", bannerFile.name, requestBanner)

                val response = RetrofitClient.instance.createCommunity(
                    namaPart, lokasiPart, deskripsiPart,
                    kategoriPart, kontakPart, linkGrupPart,
                    logoMultipart, bannerMultipart
                )

                if (response.isSuccessful) fetchCommunities()
                else errorMessage = "Gagal: ${response.message()}"

            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}