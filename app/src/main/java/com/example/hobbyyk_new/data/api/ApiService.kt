package com.example.hobbyyk_new.data.api

import com.example.hobbyyk_new.data.model.Activity
import com.example.hobbyyk_new.data.model.Community
import com.example.hobbyyk_new.data.model.CreateUserRequest
import com.example.hobbyyk_new.data.model.GenericResponse
import com.example.hobbyyk_new.data.model.LoginRequest
import com.example.hobbyyk_new.data.model.LoginResponse
import com.example.hobbyyk_new.data.model.ProfileResponse
import com.example.hobbyyk_new.data.model.RegisterRequest
import com.example.hobbyyk_new.data.model.UpdateCommunityRequest
import com.example.hobbyyk_new.data.model.UpdateUserRequest
import com.example.hobbyyk_new.data.model.User
import com.example.hobbyyk_new.data.model.VerifyOtpRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("users")
    suspend fun register(@Body request: RegisterRequest): Response<Void>

    @POST("verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<Void>
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @Multipart
    @POST("communities")
    suspend fun createCommunity(
        @Part("nama_komunitas") nama: RequestBody,
        @Part("lokasi") lokasi: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part("kategori") kategori: RequestBody,
        @Part("kontak") kontak: RequestBody,
        @Part("link_grup") linkGrup: RequestBody,
        @Part file: MultipartBody.Part,
        @Part banner: MultipartBody.Part
    ): Response<Void>
    @GET("communities")
    suspend fun getCommunities(): Response<List<Community>>
    @GET("communities/{id}")
    suspend fun getCommunityDetail(@Path("id") id: Int): Response<Community>
    @GET("my-community")
    suspend fun getMyCommunity(): Response<Community?>
    @GET("users")
    suspend fun getUsers(): Response<List<User>>
    @DELETE("communities/{id}")
    suspend fun deleteCommunity(@Path("id") id: Int): Response<Void>
    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<Void>
    @PATCH("users/{id}")
    suspend fun updateUser(
        @Path("id") id: Int,
        @Body request: UpdateUserRequest
    ): Response<Void>
    @POST("users/admin")
    suspend fun createUser(@Body request: CreateUserRequest): Response<Void>
    @Multipart
    @PATCH("communities/{id}")
    suspend fun updateCommunity(
        @Path("id") id: Int,
        @Part("nama_komunitas") nama: RequestBody,
        @Part("lokasi") lokasi: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part("kategori") kategori: RequestBody,
        @Part("kontak") kontak: RequestBody,
        @Part("link_grup") linkGrup: RequestBody,

        @Part newLogo: MultipartBody.Part?,
        @Part newBanner: MultipartBody.Part?
    ): Response<Void>
    data class ActionRequest(val communityId: Int)
    @POST("like")
    suspend fun toggleLike(@Body request: ActionRequest): Response<Void>
    @POST("join")
    suspend fun toggleJoin(@Body request: ActionRequest): Response<Void>
    data class ResendOtpRequest(val email: String)
    @POST("resend-otp")
    suspend fun resendOtp(@Body request: ResendOtpRequest): Response<Void>

    @GET("activities/community/{communityId}")
    suspend fun getActivitiesByCommunity(
        @Path("communityId") communityId: Int
    ): Response<List<Activity>>

    @GET("activities/{id}")
    suspend fun getActivityById(
        @Path("id") id: Int
    ): Response<Activity>

    @Multipart
    @POST("activities")
    suspend fun createActivity(
        @Part("judul_kegiatan") judul: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part("lokasi") lokasi: RequestBody,
        @Part("tanggal") tanggal: RequestBody,
        @Part("waktu") waktu: RequestBody,
        @Part("communityId") communityId: RequestBody,
        @Part foto_kegiatan: List<MultipartBody.Part>
    ): Response<GenericResponse>

    @Multipart
    @PATCH("activities/{id}")
    suspend fun updateActivity(
        @Path("id") id: Int,
        @Part("judul_kegiatan") judul: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part("lokasi") lokasi: RequestBody,
        @Part("tanggal") tanggal: RequestBody,
        @Part("waktu") waktu: RequestBody,
        @Part foto_kegiatan: List<MultipartBody.Part>?
    ): Response<GenericResponse>

    @DELETE("activities/{id}")
    suspend fun deleteActivity(
        @Path("id") id: Int
    ): Response<GenericResponse>

    @GET("users/me")
    suspend fun getMyProfile(): Response<ProfileResponse>

    @Multipart
    @PATCH("users/profile")
    suspend fun updateProfile(
        @Part("username") username: RequestBody,
        @Part("bio") bio: RequestBody,
        @Part("no_hp") noHp: RequestBody,
        @Part profile_pic: MultipartBody.Part?
    ): Response<GenericResponse>

    @POST("users/me/password/otp")
    suspend fun reqChangePassOtp(): Response<GenericResponse>

    @PATCH("users/me/password/verify")
    suspend fun verifyChangePass(
        @Body request: com.example.hobbyyk_new.data.model.ChangePasswordRequest
    ): Response<GenericResponse>

    @POST("users/me/email/otp")
    suspend fun reqChangeEmailOtp(
        @Body request: com.example.hobbyyk_new.data.model.EmailRequest
    ): Response<GenericResponse>

    @PATCH("users/me/email/verify")
    suspend fun verifyChangeEmail(
        @Body request: com.example.hobbyyk_new.data.model.VerifyEmailRequest
    ): Response<GenericResponse>

    @GET("activities/feed")
    suspend fun getAllActivities(): Response<List<Activity>>
}