package com.example.hobbyyk_new.data.api

import com.example.hobbyyk_new.data.model.Community
import com.example.hobbyyk_new.data.model.LoginRequest
import com.example.hobbyyk_new.data.model.LoginResponse
import com.example.hobbyyk_new.data.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("users")
    suspend fun register(@Body request: RegisterRequest): Response<Any>
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("communities")
    suspend fun getCommunities(): Response<List<Community>>

    // Endpoint VERIFIKASI OTP (Nanti kita buat modelnya kalau sudah sampai layar OTP)
    // Untuk sementara Login & Register dulu yang penting
}