package com.example.hobbyyk_new.data.model

data class Community(
    val id: Int,
    val nama_komunitas: String,
    val deskripsi: String,
    val lokasi: String,
    val link_grup: String,
    val kategori: String,
    val kontak: String,
    val foto_url: String?,
    val banner_url: String?,
    val total_anggota: Int,
    val is_joined: Boolean,
    val is_liked: Boolean,
    val total_likes: Int = 0
)