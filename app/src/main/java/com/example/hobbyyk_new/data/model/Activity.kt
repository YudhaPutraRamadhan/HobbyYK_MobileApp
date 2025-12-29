package com.example.hobbyyk_new.data.model

data class Activity(
    val id: Int,
    val judul_kegiatan: String,
    val deskripsi: String,
    val lokasi: String,
    val tanggal: String,
    val waktu: String,
    val foto_kegiatan: String?,
    val communityId: Int,
    val createdAt: String,
    val updatedAt: String,
    val community: CommunitySimple?
)