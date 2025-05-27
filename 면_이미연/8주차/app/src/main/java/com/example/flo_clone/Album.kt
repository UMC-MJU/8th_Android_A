package com.example.flo_clone

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "AlbumTable")
data class Album(
    @PrimaryKey(autoGenerate = false) var id: Int = 0,
    val title: String,
    val singer: String,
    val coverImg: Int? = null,
    var isLiked: Boolean = false // 좋아요 여부 추가
)
