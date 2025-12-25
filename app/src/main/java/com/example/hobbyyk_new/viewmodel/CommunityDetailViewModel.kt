package com.example.hobbyyk_new.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hobbyyk_new.data.api.ApiService
import com.example.hobbyyk_new.data.api.RetrofitClient
import com.example.hobbyyk_new.data.model.Community
import kotlinx.coroutines.launch

class CommunityDetailViewModel : ViewModel() {

    var community by mutableStateOf<Community?>(null)
    var isLoading by mutableStateOf(true)
    var errorMessage by mutableStateOf<String?>(null)

    var isJoined by mutableStateOf(false)
    var isLiked by mutableStateOf(false)
    var memberCount by mutableIntStateOf(0)

    var likeCount by mutableIntStateOf(0)

    fun getDetail(id: Int) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.instance.getCommunityDetail(id)
                if (response.isSuccessful) {
                    community = response.body()

                    community?.let {
                        isJoined = it.is_joined
                        isLiked = it.is_liked
                        memberCount = it.total_anggota
                        likeCount = it.total_likes
                    }
                } else {
                    errorMessage = "Gagal memuat data: ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun toggleJoin(communityId: Int) {
        viewModelScope.launch {
            val oldState = isJoined
            isJoined = !isJoined
            if (isJoined) memberCount++ else memberCount--

            try {
                val request = ApiService.ActionRequest(communityId)
                val response = RetrofitClient.instance.toggleJoin(request)

                if (!response.isSuccessful) {
                    isJoined = oldState
                    if (isJoined) memberCount++ else memberCount--
                }
            } catch (e: Exception) {
                isJoined = oldState
                e.printStackTrace()
            }
        }
    }

    fun toggleLike(communityId: Int) {
        viewModelScope.launch {
            val oldState = isLiked
            isLiked = !isLiked

            if (isLiked) likeCount++ else likeCount--

            try {
                val request = ApiService.ActionRequest(communityId)
                val response = RetrofitClient.instance.toggleLike(request)

                if (!response.isSuccessful) {
                    isLiked = oldState
                }
            } catch (e: Exception) {
                isLiked = oldState
                e.printStackTrace()
            }
        }
    }
}