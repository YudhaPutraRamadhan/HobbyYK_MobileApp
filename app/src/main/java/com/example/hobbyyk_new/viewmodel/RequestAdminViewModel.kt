package com.example.hobbyyk_new.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hobbyyk_new.data.api.ApiService
import com.example.hobbyyk_new.data.api.RetrofitClient
import com.example.hobbyyk_new.data.model.RequestAdminPayload
import kotlinx.coroutines.launch

class RequestAdminViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
    var successMessage by mutableStateOf<String?>(null)
    var errorMessage by mutableStateOf<String?>(null)

    fun submitRequest(username: String, email: String) {
        if (username.isEmpty() || email.isEmpty()) {
            errorMessage = "Username dan Email wajib diisi!"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            successMessage = null
            try {
                val payload = RequestAdminPayload(username, email)
                val response = RetrofitClient.instance.requestAdminAccount(payload)

                if (response.isSuccessful) {
                    successMessage = "Berhasil! Cek email Anda untuk detail login."
                } else {
                    // Coba ambil pesan error dari backend (misal: Email sudah ada)
                    val errorBody = response.errorBody()?.string()
                    errorMessage = errorBody ?: "Gagal mengirim permintaan."
                }
            } catch (e: Exception) {
                errorMessage = "Error jaringan: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun resetState() {
        successMessage = null
        errorMessage = null
    }
}