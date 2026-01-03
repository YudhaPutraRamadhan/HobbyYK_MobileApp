package com.example.hobbyyk_new.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hobbyyk_new.data.api.RetrofitClient
import com.example.hobbyyk_new.data.datastore.UserStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LandingAppViewModel(application: Application) : AndroidViewModel(application) {

    private val userStore = UserStore(application)
    var startDestination by mutableStateOf<String?>(null)

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            delay(2000)

            val token = userStore.authToken.first()
            val role = userStore.userRole.first()

            if (!token.isNullOrEmpty()) {
                RetrofitClient.authToken = token

                startDestination = when (role) {
                    "super_admin" -> "super_admin_graph"
                    "admin_komunitas" -> "admin_graph"
                    else -> "user_graph"
                }

            } else {
                startDestination = "login"
            }
        }
    }
}