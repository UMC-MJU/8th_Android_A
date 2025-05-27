package com.example.umc3

import androidx.room.*

@Dao
interface SongDao {

    @Insert
    fun insertSongs(vararg songs: Song)

    @Query("SELECT * FROM SongTable")
    fun getSongs(): List<Song>

    @Query("SELECT * FROM SongTable WHERE id = :id")
    fun getSong(id: Int): Song

    @Query("SELECT * FROM SongTable WHERE isLike = 1")
    fun getLikedSongs(): List<Song>

    @Query("SELECT * FROM SongTable WHERE albumIdx = :albumId")
    fun getSongsByAlbum(albumId: Int): List<Song>

    @Update
    fun updateSong(song: Song)
}
