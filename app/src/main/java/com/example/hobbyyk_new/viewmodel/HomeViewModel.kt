package com.example.hobbyyk_new.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.hobbyyk_new.data.api.RetrofitClient
import com.example.hobbyyk_new.data.datastore.UserStore
import com.example.hobbyyk_new.data.model.Community
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel (private val userStore: UserStore) : ViewModel() {

    var communities by mutableStateOf<List<Community>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    var searchQuery by mutableStateOf("")
    var selectedCategory by mutableStateOf("Semua")

    var isFirstTime by mutableStateOf(false)
        private set

    init {
        observeFirstTimeStatus()
        fetchCommunities()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val context = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as android.app.Application)
                val userStore = UserStore(context)
                HomeViewModel(userStore = userStore)
            }
        }
    }

    private fun observeFirstTimeStatus() {
        viewModelScope.launch {
            // Gunakan collectLatest agar jika ID berubah, dia akan mengulang observasi
            userStore.userId.collectLatest { id ->
                if (id != null) {
                    // Begitu ID dapat, baru kita ambil status tutorialnya
                    userStore.getFirstTimeStatus(id).collect { status ->
                        isFirstTime = status
                        Log.d("HomeViewModel", "Status tutorial untuk User $id: $status")
                    }
                } else {
                    Log.d("HomeViewModel", "UserId masih null, menunggu...")
                }
            }
        }
    }

    fun completeTutorial() {
        viewModelScope.launch {
            // Pastikan kita ambil ID terbaru sebelum menyimpan
            val id = userStore.userId.first()
            id?.let {
                userStore.setFirstTimeFinished(it)
                isFirstTime = false // Langsung matikan overlay secara manual agar responsif
            }
        }
    }

    fun fetchCommunities(
        query: String? = null,
        category: String? = null
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.instance.getCommunities(
                    search = query,
                    category = if (category == "Semua") null else category
                )

                if (response.isSuccessful) {
                    communities = response.body() ?: emptyList()
                    Log.d("HomeViewModel", "Dapat ${communities.size} komunitas")
                } else {
                    errorMessage = "Gagal mengambil data: ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error jaringan: ${e.message}"
                Log.e("HomeViewModel", "Error: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }
}