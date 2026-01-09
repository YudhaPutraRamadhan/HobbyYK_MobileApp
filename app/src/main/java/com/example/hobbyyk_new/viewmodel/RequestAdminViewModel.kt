package com.example.hobbyyk_new.viewmodel

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hobbyyk_new.data.api.ApiService
import com.example.hobbyyk_new.data.api.RetrofitClient
import com.example.hobbyyk_new.data.model.RequestAdminPayload
import kotlinx.coroutines.launch
import org.json.JSONObject

class RequestAdminViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
    var successMessage by mutableStateOf<String?>(null)
    var errorMessage by mutableStateOf<String?>(null)

    var usernameError by mutableStateOf<String?>(null)
    var emailError by mutableStateOf<String?>(null)

    fun submitRequest(username: String, email: String) {
        var hasError = false

        if (username.isEmpty()) {
            usernameError = "Username wajib diisi"
            hasError = true
        } else {
            usernameError = null
        }

        if (email.isEmpty()) {
            emailError = "Email wajib diisi"
            hasError = true
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Format email tidak valid"
            hasError = true
        } else {
            emailError = null
        }

        if (hasError) return

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            successMessage = null

            try {
                val payload = RequestAdminPayload(username, email)
                val response = RetrofitClient.instance.requestAdminAccount(payload)

                if (response.isSuccessful) {
                    successMessage = response.body()?.msg ?: "Sukses! Cek email Anda untuk mendapatkan password."
                } else {
                    val errorBody = response.errorBody()?.string()
                    val msgFromBackend = try {
                        JSONObject(errorBody ?: "{}").getString("msg")
                    } catch (e: Exception) {
                        "Gagal mengirim permintaan."
                    }

                    if (msgFromBackend.contains("Email", ignoreCase = true)) {
                        emailError = msgFromBackend
                    } else if (msgFromBackend.contains("Username", ignoreCase = true)) {
                        usernameError = msgFromBackend
                    } else {
                        errorMessage = msgFromBackend
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Gagal terhubung ke server: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun resetState() {
        successMessage = null
        errorMessage = null
        usernameError = null
        emailError = null
    }
}