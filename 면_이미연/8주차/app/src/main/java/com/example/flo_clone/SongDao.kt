package com.example.flo_clone

import androidx.room.*

@Dao
interface SongDao {

    @Insert
    fun insert(song: Song)

    @Update
    fun update(song: Song)

    @Delete
    fun delete(song: Song)

    @Query("SELECT * FROM SongTable")
    fun getSongs(): List<Song> // 전체 곡 조회

    @Query("SELECT * FROM SongTable WHERE id = :id")
    fun getSongById(id: Int): Song // 단일 곡 조회

    @Query("UPDATE SongTable SET isLike = :isLike WHERE id = :id")
    fun updateIsLikeById(isLike: Boolean, id: Int)

    @Query("SELECT * FROM SongTable WHERE isLike = :isLike")
    fun getLikedSongs(isLike: Boolean): List<Song>

    @Query("SELECT * FROM SongTable WHERE albumId = :albumId")
    fun getSongsByAlbumId(albumId: Int): List<Song>
}

