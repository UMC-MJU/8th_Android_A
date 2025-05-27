package com.example.umc3

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SongTable")
data class Song(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val singer: String,
    var isLike: Boolean = false,
    val playTime: Int = 60, // 초 단위
    val albumIdx: Int = 1 // 향후 앨범 연결용
)
