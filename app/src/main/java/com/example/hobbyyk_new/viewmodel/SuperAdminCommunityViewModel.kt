package com.example.hobbyyk_new.view.screen.superadmin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hobbyyk_new.data.api.RetrofitClient
import com.example.hobbyyk_new.data.model.Community
import kotlinx.coroutines.launch

class SuperAdminCommunityViewModel : ViewModel() {
    var communities by mutableStateOf<List<Community>>(emptyList())
    var isLoading by mutableStateOf(true)
    var errorMessage by mutableStateOf<String?>(null)

    fun fetchAllCommunities() {
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

    fun deleteCommunityAny(id: Int) {
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
}