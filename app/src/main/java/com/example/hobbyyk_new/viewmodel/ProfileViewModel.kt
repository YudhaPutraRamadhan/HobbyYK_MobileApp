package com.example.hobbyyk_new.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hobbyyk_new.data.api.RetrofitClient
import com.example.hobbyyk_new.data.model.CommunitySimple
import com.example.hobbyyk_new.data.model.User
import com.example.hobbyyk_new.utils.uriToFile
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class ProfileViewModel : ViewModel() {
    var userProfile by mutableStateOf<User?>(null)
    var managedCommunity by mutableStateOf<CommunitySimple?>(null)

    var isLoading by mutableStateOf(false)
    var message by mutableStateOf<String?>(null)

    var navigateToVerifyPass by mutableStateOf(false)
    var navigateToVerifyEmail by mutableStateOf<String?>(null)

    fun fetchProfile() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.instance.getMyProfile()
                if (response.isSuccessful) {
                    val data = response.body()
                    userProfile = data?.user
                    managedCommunity = data?.managed_community
                }
            } catch (e: Exception) {
            } finally {
                isLoading = false
            }
        }
    }

    fun updateProfile(username: String, bio: String, noHp: String, imageUri: Uri?, context: Context) {
        viewModelScope.launch {
            isLoading = true
            try {
                val usernamePart = username.toRequestBody("text/plain".toMediaTypeOrNull())
                val bioPart = bio.toRequestBody("text/plain".toMediaTypeOrNull())
                val hpPart = noHp.toRequestBody("text/plain".toMediaTypeOrNull())

                val imagePart = if (imageUri != null) {
                    val file = uriToFile(imageUri, context)
                    val reqFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("profile_pic", file.name, reqFile)
                } else null

                val response = RetrofitClient.instance.updateProfile(usernamePart, bioPart, hpPart, imagePart)
                if (response.isSuccessful) {
                    message = "Profil berhasil diperbarui!"
                    fetchProfile()
                } else {
                    message = "Gagal: ${response.message()}"
                }
            } catch (e: Exception) {
                message = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun requestOtpPassword() {
        viewModelScope.launch {
            isLoading = true
            try {
                val res = RetrofitClient.instance.reqChangePassOtp()
                if (res.isSuccessful) {
                    message = "OTP dikirim ke email Anda"
                    navigateToVerifyPass = true
                } else {
                    message = "Gagal kirim OTP"
                }
            } catch (e: Exception) { message = "Error: ${e.message}" }
            finally { isLoading = false }
        }
    }

    fun verifyChangePass(otp: String, newPass: String, confPass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                val request = com.example.hobbyyk_new.data.model.ChangePasswordRequest(otp, newPass, confPass)
                val res = RetrofitClient.instance.verifyChangePass(request)

                if (res.isSuccessful) {
                    message = "Password Sukses Diubah!"
                    onSuccess()
                } else {
                    val errorMsg = res.errorBody()?.string()
                    message = "Gagal: OTP salah atau syarat password tidak terpenuhi"
                }
            } catch (e: Exception) {
                message = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun requestOtpEmail(newEmail: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val request = com.example.hobbyyk_new.data.model.EmailRequest(newEmail)
                val res = RetrofitClient.instance.reqChangeEmailOtp(request)

                if (res.isSuccessful) {
                    message = "OTP dikirim ke $newEmail"
                    navigateToVerifyEmail = newEmail
                } else {
                    message = "Gagal: Email sudah terdaftar atau format salah"
                }
            } catch (e: Exception) {
                message = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun verifyChangeEmail(otp: String, newEmail: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                val request = com.example.hobbyyk_new.data.model.VerifyEmailRequest(otp, newEmail)
                val res = RetrofitClient.instance.verifyChangeEmail(request)

                if (res.isSuccessful) {
                    message = "Email Sukses Diubah!"
                    onSuccess()
                    fetchProfile()
                } else {
                    message = "Gagal: OTP salah atau email sudah digunakan"
                }
            } catch (e: Exception) {
                message = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun clearMessage() { message = null }
}