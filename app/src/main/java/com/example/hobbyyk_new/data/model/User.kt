package com.example.hobbyyk_new.data.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val role: String,
    @SerializedName("is_verified")
    val isVerified: Boolean,
    val bio: String?,
    val no_hp: String?,
    val profile_pic: String?
)

data class ProfileResponse(
    val user: User,
    val managed_community: CommunitySimple?
)

data class CommunitySimple(
    val id: Int,
    val nama_komunitas: String,
    val logo: String?
)