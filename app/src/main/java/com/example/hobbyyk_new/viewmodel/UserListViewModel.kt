package com.example.hobbyyk_new.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hobbyyk_new.data.api.RetrofitClient
import com.example.hobbyyk_new.data.model.CreateUserRequest
import com.example.hobbyyk_new.data.model.UpdateUserRequest
import com.example.hobbyyk_new.data.model.User
import kotlinx.coroutines.launch

class UserListViewModel : ViewModel() {
    var users by mutableStateOf<List<User>>(emptyList())
    var isLoading by mutableStateOf(true)
    var errorMessage by mutableStateOf<String?>(null)

    fun fetchUsers() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.instance.getUsers()
                if (response.isSuccessful) {
                    users = response.body() ?: emptyList()
                } else {
                    errorMessage = "Gagal: ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteUser(id: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.deleteUser(id)
                if (response.isSuccessful) {
                    users = users.filter { it.id != id }
                } else {
                    errorMessage = "Gagal menghapus: ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            }
        }
    }

    fun updateUser(id: Int, username: String, email: String, role: String, isVerified: Boolean) {
        viewModelScope.launch {
            try {
                val request = UpdateUserRequest(username, email, role, isVerified)
                val response = RetrofitClient.instance.updateUser(id, request)

                if (response.isSuccessful) {
                    // Update list di UI secara manual biar gak usah loading ulang
                    users = users.map {
                        if (it.id == id) it.copy(username = username, email = email, role = role, isVerified = isVerified)
                        else it
                    }
                } else {
                    errorMessage = "Gagal update: ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            }
        }
    }

    fun createUser(username: String, email: String, role: String) {
        viewModelScope.launch {
            try {
                val request = CreateUserRequest(username, email, role)
                val response = RetrofitClient.instance.createUser(request)

                if (response.isSuccessful) {
                    // Refresh data biar user baru muncul
                    fetchUsers()
                } else {
                    errorMessage = "Gagal membuat user: ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            }
        }
    }
}