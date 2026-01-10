package com.example.hobbyyk_new.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hobbyyk_new.data.api.RetrofitClient
import com.example.hobbyyk_new.data.datastore.UserStore
import com.example.hobbyyk_new.data.model.Community
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import org.json.JSONObject
import java.net.SocketTimeoutException

class AdminCommunityViewModel (private val userStore: UserStore) : ViewModel() {
    var myCommunity by mutableStateOf<Community?>(null)

    var otherCommunities by mutableStateOf<List<Community>>(emptyList())
    var searchQuery by mutableStateOf("")
    var selectedCategory by mutableStateOf("Semua")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    var isFirstTime by mutableStateOf(false)
        private set

    init {
        observeFirstTimeStatus()
    }
    private fun observeFirstTimeStatus() {
        viewModelScope.launch {
            userStore.userId.collect { id ->
                id?.let { currentId ->
                    userStore.getFirstTimeStatus(currentId).collect { status ->
                        isFirstTime = status
                    }
                }
            }
        }
    }

    fun completeTutorial() {
        viewModelScope.launch {
            val id = userStore.userId.first()
            id?.let {
                userStore.setFirstTimeFinished(it)
            }
        }
    }

    fun fetchMyCommunity() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.instance.getMyCommunity()
                if (response.isSuccessful) {
                    myCommunity = response.body()
                    fetchOtherCommunities()
                } else {
                    errorMessage = "Gagal memuat data"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchOtherCommunities() {
        viewModelScope.launch {
            try {
                val categoryParam = if (selectedCategory == "Semua") null else selectedCategory
                val queryParam = if (searchQuery.isBlank()) null else searchQuery

                val response = RetrofitClient.instance.getCommunities(queryParam, categoryParam)

                if (response.isSuccessful) {
                    val allCommunities = response.body() ?: emptyList()

                    otherCommunities = if (myCommunity != null) {
                        allCommunities.filter { it.id != myCommunity!!.id }
                    } else {
                        allCommunities
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchCommunityById(id: Int) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.instance.getCommunityById(id)
                if (response.isSuccessful) {
                    myCommunity = response.body()
                } else {
                    errorMessage = "Gagal memuat data detail: ${response.message()}"
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
        logoFile: File, bannerFile: File?
    ) {
        val alphaSpaceRegex = Regex("^[a-zA-Z\\s]+$")

        if (!nama.matches(alphaSpaceRegex)) {
            errorMessage = "Nama komunitas hanya boleh berisi huruf!"
            return
        }

        if (!lokasi.matches(alphaSpaceRegex)) {
            errorMessage = "Lokasi hanya boleh berisi nama daerah (huruf)!"
            return
        }

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

                var bannerMultipart: MultipartBody.Part? = null
                if (bannerFile != null) {
                    val requestBanner = bannerFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val bannerName = "banner_${System.currentTimeMillis()}.jpg"
                    bannerMultipart = MultipartBody.Part.createFormData("banner", bannerName, requestBanner)
                }

                val response = RetrofitClient.instance.createCommunity(
                    namaPart, lokasiPart, deskripsiPart,
                    kategoriPart, kontakPart, linkGrupPart,
                    logoMultipart, bannerMultipart
                )

                if (response.isSuccessful) {
                    errorMessage = "Komunitas Berhasil Dibuat"
                    fetchMyCommunity()
                } else {
                    val errorJson = response.errorBody()?.string()
                    errorMessage = try {
                        JSONObject(errorJson!!).optString("msg", "Gagal membuat komunitas")
                    } catch (e: Exception) {
                        "Gagal membuat: ${response.message()}"
                    }
                }
            } catch (e: SocketTimeoutException) {
                errorMessage = "Waktu habis. Server sedang sibuk, silakan coba lagi."
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteCommunity(id: Int) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.instance.deleteCommunity(id)
                if (response.isSuccessful) {
                    myCommunity = null
                    fetchOtherCommunities()
                } else {
                    errorMessage = "Gagal menghapus"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun updateCommunity(
        id: Int, nama: String, lokasi: String, deskripsi: String,
        kategori: String, kontak: String, linkGrup: String,
        newLogoFile: File?, newBannerFile: File?
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
                    val fileName = "logo_${System.currentTimeMillis()}.jpg"
                    logoMultipart = MultipartBody.Part.createFormData("file", fileName, requestLogo)
                }

                var bannerMultipart: MultipartBody.Part? = null
                if (newBannerFile != null) {
                    val requestBanner = newBannerFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val fileName = "banner_${System.currentTimeMillis()}.jpg"
                    bannerMultipart = MultipartBody.Part.createFormData("banner", fileName, requestBanner)
                }

                val response = RetrofitClient.instance.updateCommunity(
                    id, namaPart, lokasiPart, deskripsiPart, kategoriPart, kontakPart, linkGrupPart,
                    logoMultipart, bannerMultipart
                )

                if (response.isSuccessful) {
                    fetchCommunityById(id)
                    errorMessage = "Update Berhasil!"
                } else {
                    val errorJson = response.errorBody()?.string()
                    errorMessage = try {
                        JSONObject(errorJson!!).optString("msg", "Gagal update komunitas")
                    } catch (e: Exception) {
                        "Gagal update: ${response.message()}"
                    }
                }
            } catch (e: SocketTimeoutException) {
                errorMessage = "Waktu habis, server tidak merespon. Cek koneksi Anda."
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}