package com.example.flo_clone

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.flo_clone.databinding.ActivityMainBinding
import com.example.flo_clone.ui.home.HomeFragment
import com.example.flo_clone.ui.look.LookFragment
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var handler: Handler
    private lateinit var updateSeekBarRunnable: Runnable

    private var song: Song = Song()
    private var gson: Gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_FLOclone)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inputDummySongs()
        initBottomNavigation()

        binding.mainPlayerCl.setOnClickListener {
            val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
            editor.putInt("songId", song.id)
            editor.apply()

            val intent = Intent(this, SongActivity::class.java)
            startActivity(intent)
        }

        initSeekBarUpdater()
    }

    override fun onStart() {
        super.onStart()

        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 0)
        val songDB = SongDatabase.getInstance(this)!!

        song = if (songId == 0) {
            songDB.songDao().getSongs(1) ?: Song()
        } else {
            songDB.songDao().getSongs(songId) ?: Song()
        }

        Log.d("song ID", song.id.toString())
        setMiniPlayer(song)
        startSeekBarUpdater()
    }

    override fun onStop() {
        super.onStop()
        stopSeekBarUpdater()
    }

    private fun initBottomNavigation() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_activity_main, HomeFragment())
            .commitAllowingStateLoss()

        binding.navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_main, HomeFragment())
                        .commitAllowingStateLoss()
                    true
                }
                R.id.navigation_look -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_main, LookFragment())
                        .commitAllowingStateLoss()
                    true
                }
                else -> false
            }
        }
    }

    private fun setMiniPlayer(song: Song) {
        binding.mainMiniplayerTitleTv.text = song.title
        binding.mainMiniplayerSingerTv.text = song.singer
        binding.mainProgressSb.max = song.playTime * 1000
        binding.mainProgressSb.progress = song.second * 1000
    }

    private fun inputDummySongs() {
        val songDB = SongDatabase.getInstance(this)!!
        val songs = songDB.songDao().getSongs()

        if (songs.isNotEmpty()) return

        songDB.songDao().insert(
            Song("Lilac", "아이유 (IU)", 0, 200, false, "music_lilac", R.drawable.img_album_exp2, false)
        )
        songDB.songDao().insert(
            Song("Flu", "아이유 (IU)", 0, 200, false, "music_flu", R.drawable.img_album_exp2, false)
        )
        songDB.songDao().insert(
            Song("Butter", "방탄소년단 (BTS)", 0, 190, false, "music_butter", R.drawable.img_album_exp, false)
        )
        songDB.songDao().insert(
            Song("Next Level", "에스파 (AESPA)", 0, 210, false, "music_next", R.drawable.img_album_exp3, false)
        )
        songDB.songDao().insert(
            Song("Boy with Luv", "music_boy", 0, 230, false, "music_lilac", R.drawable.img_album_exp4, false)
        )
        songDB.songDao().insert(
            Song("BBoom BBoom", "모모랜드 (MOMOLAND)", 0, 240, false, "music_bboom", R.drawable.img_album_exp5, false)
        )

        val _songs = songDB.songDao().getSongs()
        Log.d("DB data", _songs.toString())
    }

    private fun initSeekBarUpdater() {
        handler = Handler(Looper.getMainLooper())
        updateSeekBarRunnable = object : Runnable {
            override fun run() {
                val spf = getSharedPreferences("song", MODE_PRIVATE)
                val songId = spf.getInt("songId", 0)
                val second = spf.getInt("second", 0)

                val songDB = SongDatabase.getInstance(this@MainActivity)!!
                val currentSong = songDB.songDao().getSongs(songId)

                if (currentSong != null) {
                    // 노래의 총 재생 시간
                    binding.mainProgressSb.max = currentSong.playTime * 1000
                    // SeekBar의 현재 진행 상태를 저장된 second 값으로 설정
                    binding.mainProgressSb.progress = second * 1000
                } else {
                    Log.e("SeekBarUpdater", "currentSong is null for songId=$songId")
                }

                // 1초마다 이 작업을 반복 실행 (재귀 호출)
                handler.postDelayed(this, 1000)
            }
        }
    }

    private fun startSeekBarUpdater() {
        handler.post(updateSeekBarRunnable)
    }

    private fun stopSeekBarUpdater() {
        handler.removeCallbacks(updateSeekBarRunnable)
    }
}
