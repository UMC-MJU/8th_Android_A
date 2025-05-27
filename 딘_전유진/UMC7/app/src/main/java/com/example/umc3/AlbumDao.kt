package com.example.umc3

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AlbumDao {
    @Insert
    fun insertAlbums(vararg albums: Album)

    @Query("SELECT * FROM AlbumTable")
    fun getAlbums(): List<Album>

    @Query("SELECT * FROM AlbumTable WHERE id = :id")
    fun getAlbum(id: Int): Album

    @Query("SELECT * FROM AlbumTable WHERE title = :title LIMIT 1")
    fun getAlbumByTitle(title: String): Album?

}
