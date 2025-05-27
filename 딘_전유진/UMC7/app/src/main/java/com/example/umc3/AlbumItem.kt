package com.example.umc3

data class AlbumItem(
    val title: String,
    val artist: String,
    val imageResId: Int,
    var isSwitchOn: Boolean = false
)
