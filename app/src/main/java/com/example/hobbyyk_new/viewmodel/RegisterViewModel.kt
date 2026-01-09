package com.example.hobbyyk_new.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hobbyyk_new.data.api.RetrofitClient
import com.example.hobbyyk_new.data.model.RegisterRequest
import com.example.hobbyyk_new.data.model.VerifyOtpRequest
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confPassword by mutableStateOf("")

    var navigateToOtp by mutableStateOf<String?>(null)
    var otpCode by mutableStateOf("")
    var showOtpDialog by mutableStateOf(false)

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var isSuccess by mutableStateOf(false)

    var emailError by mutableStateOf<String?>(null)
    var passwordError by mutableStateOf<String?>(null)
    var nameError by mutableStateOf<String?>(null)
    var confPasswordError by mutableStateOf<String?>(null)



    fun register() {
        emailError = null
        passwordError = null
        confPasswordError = null
        nameError = null
        errorMessage = null

        var isValid = true

        if (name.isBlank()) {
            nameError = "Nama tidak boleh kosong"
            isValid = false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Format email salah"
            isValid = false
        }

        if (password.length < 8) {
            passwordError = "Password minimal 8 karakter"
            isValid = false
        }

        if (confPassword != password) {
            confPasswordError = "Konfirmasi password tidak cocok"
            isValid = false
        }

        if (!isValid) return

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val request = RegisterRequest(
                    username = name,
                    email = email,
                    password = password,
                    confPassword = confPassword,
                    role = "user"
                )
                val response = RetrofitClient.instance.register(request)

                if (response.isSuccessful) {
                    navigateToOtp = email
                } else {
                    val errorMsg = response.errorBody()?.string() ?: ""

                    when {
                        errorMsg.contains("Email", ignoreCase = true) -> {
                            emailError = "Email ini sudah terdaftar, gunakan email lain."
                        }
                        errorMsg.contains("Username", ignoreCase = true) -> {
                            nameError = "Username sudah dipakai, cari nama yang lebih unik ya!"
                        }
                        else -> {
                            errorMessage = "Gagal: $errorMsg"
                        }
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Koneksi bermasalah: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}