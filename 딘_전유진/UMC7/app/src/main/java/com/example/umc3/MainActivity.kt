package com.example.umc3

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val songActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val albumName = result.data?.getStringExtra("albumName")
            Toast.makeText(this, "받은 앨범: $albumName", Toast.LENGTH_SHORT).show()
        }
    }

    private lateinit var songs: MutableList<Song>
    private var nowPos = 0
    private lateinit var songDB: AppDatabase

    override fun onResume() {
        super.onResume()

        val shared = getSharedPreferences("music", MODE_PRIVATE)
        val title = shared.getString("title", "제목 없음")
        val singer = shared.getString("singer", "가수 없음")
        val isPlaying = shared.getBoolean("isPlaying", false)
        val currentPosition = shared.getInt("currentPosition", 0)

        val tvTitle = findViewById<TextView>(R.id.tv_title_main)
        val tvSinger = findViewById<TextView>(R.id.tv_singer_main)
        val seekBar = findViewById<SeekBar>(R.id.seekBar_mini)
        val progressBarView = findViewById<View>(R.id.progress_bar_mini)

        tvTitle.text = title
        tvSinger.text = singer
        seekBar.progress = currentPosition

        val totalDuration = 60000
        val ratio = currentPosition.toFloat() / totalDuration
        val screenWidth = resources.displayMetrics.widthPixels
        val newWidth = (screenWidth * ratio).toInt()
        val params = progressBarView.layoutParams
        params.width = newWidth
        progressBarView.layoutParams = params

        MusicPlayerState.isPlaying = isPlaying
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initSongs()

        val shared = getSharedPreferences("music", MODE_PRIVATE)
        songDB = AppDatabase.getInstance(this)!!
        // ✅ 앨범 더미 데이터 삽입
        val existingAlbums = songDB.albumDao().getAlbums()
        if (existingAlbums.isEmpty()) {
            songDB.albumDao().insertAlbums(
                Album(1, "LILAC", "아이유", R.drawable.lilac_album),
                Album(2, "ARMAGEDDON", "aespa", R.drawable.armageddon_album),
                Album(3, "Bad Boy", "Red Velvet", R.drawable.badboy_album),
                Album(4, "봄날", "방탄소년단", R.drawable.springday_album),
                Album(5, "Weekend", "태연", R.drawable.weekend_album),
                Album(6, "예뻤어", "Day6", R.drawable.daysix_album),
                Album(7, "Firefly", "엔플라잉", R.drawable.firefly_album)
            )
        }

        val existing = songDB.songDao().getSongs()
        if (existing.isEmpty()) {
            songDB.songDao().insertSongs(
                Song(title = "LILAC", singer = "아이유", playTime = 60, albumIdx = 1),
                Song(title = "Celebrity", singer = "아이유", playTime = 60, albumIdx = 1),
                Song(title = "ARMAGEDDON", singer = "aespa", playTime = 60, albumIdx = 2)
            )
            shared.edit().putInt("songId", 0).apply()
        }

        val progressBarView = findViewById<View>(R.id.progress_bar_mini)
        val seekBarMini = findViewById<SeekBar>(R.id.seekBar_mini)
        val title = findViewById<TextView>(R.id.tv_title_main)
        val singer = findViewById<TextView>(R.id.tv_singer_main)
        val miniPlayer = findViewById<LinearLayout>(R.id.mini_player)
        val btnMainPlay = findViewById<ImageView>(R.id.btn_main_play)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val btnPrev = findViewById<ImageView>(R.id.btn_previous)
        val btnNext = findViewById<ImageView>(R.id.btn_next)

        btnPrev.setOnClickListener {
            if (nowPos > 0) {
                nowPos--
                setMiniPlayer(songs[nowPos])
                saveSongId(songs[nowPos].id)
            }
        }

        btnNext.setOnClickListener {
            if (nowPos < songs.size - 1) {
                nowPos++
                setMiniPlayer(songs[nowPos])
                saveSongId(songs[nowPos].id)
            }
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, HomeFragment())
                        .commit()
                    true
                }
                R.id.nav_profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, LibraryFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }

        btnMainPlay.setOnClickListener {
            MusicPlayerState.togglePlayPause()
            btnMainPlay.setImageResource(
                if (MusicPlayerState.isPlaying) R.drawable.ic_pause else R.drawable.ic_play
            )
            shared.edit().putBoolean("isPlaying", MusicPlayerState.isPlaying).apply()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, HomeFragment())
            .commit()

        val handler = Handler(Looper.getMainLooper())
        val totalDuration = 60000
        handler.post(object : Runnable {
            override fun run() {
                val currentPosition = shared.getInt("currentPosition", 0)
                val progressRatio = currentPosition.toFloat() / totalDuration
                val screenWidth = resources.displayMetrics.widthPixels
                val newWidth = (screenWidth * progressRatio).toInt()
                val params = progressBarView.layoutParams
                params.width = newWidth
                progressBarView.layoutParams = params
                seekBarMini.progress = currentPosition
                handler.postDelayed(this, 1000)
            }
        })

        val songId = shared.getInt("songId", 0)
        val currentSong = songDB.songDao().getSong(songId)
        if (currentSong != null) {
            title.text = currentSong.title
            singer.text = currentSong.singer
            seekBarMini.max = currentSong.playTime * 1000
        } else {
            title.text = "재생할 곡이 없습니다"
            singer.text = "-"
            seekBarMini.max = 60000
        }

        miniPlayer.setOnClickListener {
            openSongActivity(title.text.toString(), singer.text.toString())
        }
    }

    fun openSongActivity(title: String, singer: String) {
        val intent = Intent(this, SongActivity::class.java).apply {
            putExtra("title", title)
            putExtra("singer", singer)
        }
        songActivityLauncher.launch(intent)
    }

    fun updateMiniPlayerUI() {
        val shared = getSharedPreferences("music", MODE_PRIVATE)
        val title = shared.getString("title", "제목 없음")
        val singer = shared.getString("singer", "가수 없음")
        val isPlaying = shared.getBoolean("isPlaying", false)

        findViewById<TextView>(R.id.tv_title_main).text = title
        findViewById<TextView>(R.id.tv_singer_main).text = singer
        findViewById<ImageView>(R.id.btn_main_play).setImageResource(
            if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        )
    }

    private fun initSongs() {
        songDB = AppDatabase.getInstance(this)!!
        songs = songDB.songDao().getSongs().toMutableList()
        val songId = getSharedPreferences("music", MODE_PRIVATE).getInt("songId", 0)
        nowPos = songs.indexOfFirst { it.id == songId }.coerceAtLeast(0)
    }

    private fun setMiniPlayer(song: Song) {
        val title = findViewById<TextView>(R.id.tv_title_main)
        val singer = findViewById<TextView>(R.id.tv_singer_main)

        title.text = song.title
        singer.text = song.singer

        findViewById<SeekBar>(R.id.seekBar_mini).progress = 0

        getSharedPreferences("music", MODE_PRIVATE).edit()
            .putString("title", song.title)
            .putString("singer", song.singer)
            .putInt("imageResId", if (song.title == "ARMAGEDDON") R.drawable.armageddon_album else R.drawable.lilac_album)
            .putString("lyrics", if (song.title == "ARMAGEDDON") "We rise above it all (ARMAGEDDON)" else "내리는 꽃가루에 눈이 따끔해 아야")
            .putInt("currentPosition", 0)
            .apply()
    }

    private fun saveSongId(id: Int) {
        getSharedPreferences("music", MODE_PRIVATE).edit()
            .putInt("songId", id)
            .apply()
    }
}
