package com.example.umc3

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "AlbumTable")
data class Album(
    @PrimaryKey val id: Int,  // autoGenerate
    val title: String,
    val singer: String,
    val coverImg: Int  // drawable 리소스 ID
)
