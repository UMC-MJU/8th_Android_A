package com.example.flo_clone

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.LockerFragment
import com.example.flo_clone.databinding.ActivityMainBinding
import com.example.flo_clone.ui.home.HomeFragment
import com.example.flo_clone.ui.look.LookFragment
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var handler: Handler
    private lateinit var updateSeekBarRunnable: Runnable
    private lateinit var songDB: SongDatabase

    // songs 리스트로 전체 곡 관리
    private lateinit var songs: List<Song>
    private var nowPos = 0
    private var mediaPlayer: MediaPlayer? = null
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_FLOclone)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // DB 인스턴스 연결
        songDB = SongDatabase.getInstance(this)!!

        // [더미 데이터 삽입]
        inputDummyAlbums()
        inputDummySongs()

        // 하단바 초기화
        initBottomNavigation()

        // SeekBar 업데이트 루프 시작
        initSeekBarUpdater()

        // [MiniPlayer 클릭 시 SongActivity로 이동]
        binding.mainPlayerCl.setOnClickListener {
            val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
            editor.putInt("songId", songs[nowPos].id) // 현재 곡의 ID 저장
            editor.apply()

            startActivity(Intent(this, SongActivity::class.java))
        }

        // [다음곡 버튼]
        binding.mainNextBtn.setOnClickListener { moveSong(+1) }

        // [이전곡 버튼]
        binding.mainPreviousBtn.setOnClickListener { moveSong(-1) }
    }

    override fun onStart() {
        super.onStart()

        // [SharedPreference에서 저장된 songId 불러오기]
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", -1)

        // [DB에서 전체 곡 불러오기]
        songs = songDB.songDao().getSongs()

        if (songs.isEmpty()) return

        // [songId로 현재 재생 중인 곡 위치 찾아 설정]
        nowPos = getPlayingSongPosition(songId)

        // [MiniPlayer에 현재 곡 정보 세팅]
        setMiniPlayer(songs[nowPos])
    }

    // 현재 재생 곡 위치 찾기
    private fun getPlayingSongPosition(songId: Int): Int {
        return songs.indexOfFirst { it.id == songId }.takeIf { it != -1 } ?: 0
    }

    // 다음/이전 곡 이동 및 재생
    private fun moveSong(direction: Int) {
        val newPos = nowPos + direction
        if (newPos in songs.indices) {
            nowPos = newPos
            val newSong = songs[nowPos]
            setMiniPlayer(newSong)

            // 변경된 곡 정보를 SharedPreferences에 저장
            val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
            editor.putInt("songId", newSong.id)
            editor.putInt("second", 0)
            editor.apply()

            playMusic(newSong)
        }
    }

    // MiniPlayer UI에 단일 곡 세팅
    fun setMiniPlayer(song: Song) {
        binding.mainMiniplayerTitleTv.text = song.title
        binding.mainMiniplayerSingerTv.text = song.singer
        binding.mainProgressSb.max = song.playTime * 1000
        binding.mainProgressSb.progress = song.second * 1000
    }

    // MiniPlayer UI에 전체 곡 리스트로 세팅
    fun setMiniPlayer(songs: List<Song>) {
        this.songs = songs
        nowPos = 0
        val song = songs[nowPos]
        setMiniPlayer(song)
        playMusic(song)

        val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
        editor.putInt("songId", song.id)
        editor.putInt("second", 0)
        editor.apply()
    }

    // 음악 재생
    private fun playMusic(song: Song) {
        val musicResId = resources.getIdentifier(song.music, "raw", packageName)
        if (musicResId == 0) return

        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, musicResId)
        mediaPlayer?.start()
    }

    // 더미 곡 데이터 삽입
    private fun inputDummySongs() {
        val songDB = SongDatabase.getInstance(this)!!
        val songs = songDB.songDao().getSongs()
        if (songs.isNotEmpty()) return

        songDB.songDao().insert(Song("Lilac", "아이유 (IU)", 0, 200, false, "music_lilac", R.drawable.img_album_exp2, false, false, 0))
        songDB.songDao().insert(Song("Flu", "아이유 (IU)", 0, 200, false, "music_flu", R.drawable.img_album_exp2, false, false, 0))
        songDB.songDao().insert(Song("Butter", "방탄소년단 (BTS)", 0, 190, false, "music_butter", R.drawable.img_album_exp, false, false, 1))
        songDB.songDao().insert(Song("Next Level", "에스파 (AESPA)", 0, 210, false, "music_next", R.drawable.img_album_exp3, false, false, 2))
        songDB.songDao().insert(Song("Boy with Luv", "방탄소년단 (BTS)", 0, 230, false, "music_boy", R.drawable.img_album_exp4, false, false, 3))
        songDB.songDao().insert(Song("BBoom BBoom", "모모랜드 (MOMOLAND)", 0, 240, false, "music_bboom", R.drawable.img_album_exp5, false, false, 4))
    }

    // 더미 앨범 데이터 삽입
    private fun inputDummyAlbums() {
        val albums = songDB.albumDao().getAlbums()
        if (albums.isNotEmpty()) return

        songDB.albumDao().insert(Album(0, "Lilac", "아이유 (IU)", R.drawable.img_album_exp2))
        songDB.albumDao().insert(Album(1, "Butter", "방탄소년단 (BTS)", R.drawable.img_album_exp))
        songDB.albumDao().insert(Album(2, "Next Level", "에스파 (AESPA)", R.drawable.img_album_exp3))
        songDB.albumDao().insert(Album(3, "Boy with Luv", "방탄소년단 (BTS)", R.drawable.img_album_exp4))
        songDB.albumDao().insert(Album(4, "BBoom BBoom", "모모랜드 (MOMOLAND)", R.drawable.img_album_exp5))
    }

    // SeekBar 주기적 업데이트
    private fun initSeekBarUpdater() {
        handler = Handler(Looper.getMainLooper())
        updateSeekBarRunnable = object : Runnable {
            override fun run() {
                val spf = getSharedPreferences("song", MODE_PRIVATE)
                val songId = spf.getInt("songId", 0)
                val second = spf.getInt("second", 0)
                val currentSong = try {
                    songDB.songDao().getSongs(songId)
                } catch (e: Exception) {
                    null
                }

                if (currentSong != null) {
                    binding.mainProgressSb.max = currentSong.playTime * 1000
                    binding.mainProgressSb.progress = second * 1000
                }

                handler.postDelayed(this, 1000)
            }
        }
        handler.post(updateSeekBarRunnable)
    }

    // 하단 내비게이션 처리
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
                R.id.navigation_locker -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_main, LockerFragment())
                        .commitAllowingStateLoss()
                    true
                }
                else -> false
            }
        }
    }
}
