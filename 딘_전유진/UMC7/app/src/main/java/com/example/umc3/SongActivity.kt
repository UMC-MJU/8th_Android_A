package com.example.umc3

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase


class SongActivity : AppCompatActivity() {

    private var isRepeatOn = false
    private var isPlayAllOn = false
    private var isPlaying = false

    private lateinit var seekBar: SeekBar
    private lateinit var startTimeText: TextView
    private lateinit var endTimeText: TextView
    private var currentPosition = 0
    private val totalDuration = 60000 // 1분 (밀리초)
    private var playerThread: Thread? = null
    private lateinit var songDB: AppDatabase
    private lateinit var songs: MutableList<Song>
    private var nowPos: Int = 0
    private lateinit var btnLike: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song)

        // 1️DB 인스턴스
        songDB = AppDatabase.getInstance(this)!!

        // 2️전체 곡 리스트 불러오기
        songs = songDB.songDao().getSongs().toMutableList()

        // 3️저장된 songId 불러오기
        val songId = getSharedPreferences("music", MODE_PRIVATE).getInt("songId", 0)

        // 4️songId에 해당하는 인덱스를 nowPos로 찾기
        nowPos = getPlayingSongPosition(songId)

        val title = intent.getStringExtra("title") ?: "제목 없음"
        val singer = intent.getStringExtra("singer") ?: "가수 없음"

        val tvTitle = findViewById<TextView>(R.id.tv_song_title)
        val tvSinger = findViewById<TextView>(R.id.tv_song_singer)
        val ivSongAlbum = findViewById<ImageView>(R.id.iv_song_album)
        val btnRepeat = findViewById<ImageView>(R.id.btn_repeat)
        val btnPlayAll = findViewById<ImageView>(R.id.btn_play_all)
        val btnPlay = findViewById<ImageView>(R.id.btn_play)
        val btnBack = findViewById<ImageView>(R.id.btn_album_back)
        val tvLyrics = findViewById<TextView>(R.id.tv_lyrics)
        val btnPrev = findViewById<ImageView>(R.id.btn_previous)
        val btnNext = findViewById<ImageView>(R.id.btn_next)

        btnPrev.setOnClickListener {
            if (nowPos > 0) {
                stopSeekBarThread()
                nowPos--
                setPlayer(songs[nowPos])
                startSeekBarThread()
                updateSharedSongId(songs[nowPos].id)
            }
        }

        btnNext.setOnClickListener {
            if (nowPos < songs.size - 1) {
                stopSeekBarThread()
                nowPos++
                setPlayer(songs[nowPos])
                startSeekBarThread()
                updateSharedSongId(songs[nowPos].id)
            }
        }



        btnBack.setOnClickListener {
            finish() // SongActivity 종료
        }

        // SharedPreferences로부터 앨범 이미지와 가사 불러오기
        val shared = getSharedPreferences("music", MODE_PRIVATE)
        val albumImageResId = shared.getInt("imageResId", R.drawable.lilac_album)
        val lyrics = shared.getString("lyrics", "기본 가사입니다")

        // UI 적용
        tvTitle.text = title
        tvSinger.text = singer
        ivSongAlbum.setImageResource(albumImageResId)
        tvLyrics.text = lyrics

        seekBar = findViewById(R.id.seekBar)
        startTimeText = findViewById(R.id.tv_start_time)
        endTimeText = findViewById(R.id.tv_end_time)

        seekBar.max = totalDuration
        endTimeText.text = formatTime(totalDuration)

        val savedPosition = shared.getInt("currentPosition", 0)
        currentPosition = savedPosition
        seekBar.progress = currentPosition
        startTimeText.text = formatTime(currentPosition)

        btnRepeat.setOnClickListener {
            isRepeatOn = !isRepeatOn
            btnRepeat.setImageResource(
                if (isRepeatOn) R.drawable.ic_repeat_on else R.drawable.ic_repeat
            )
        }

        btnPlayAll.setOnClickListener {
            isPlayAllOn = !isPlayAllOn
            btnPlayAll.setImageResource(
                if (isPlayAllOn) R.drawable.ic_random_on else R.drawable.ic_random
            )
        }

        btnPlay.setOnClickListener {
            isPlaying = !isPlaying
            if (isPlaying) {
                startSeekBarThread()
                btnPlay.setImageResource(R.drawable.ic_pause)
            } else {
                stopSeekBarThread()
                btnPlay.setImageResource(R.drawable.ic_play)
            }
        }

        MusicPlayerState.registerListener {
            isPlaying = MusicPlayerState.isPlaying
            if (isPlaying) {
                startSeekBarThread()
            } else {
                stopSeekBarThread()
            }
        }
        btnLike = findViewById(R.id.btn_like)

// 현재 곡의 좋아요 상태 반영
        setLike(songs[nowPos].isLike)

// 클릭 시 상태 변경
        btnLike.setOnClickListener {
            val isLiked = !songs[nowPos].isLike
            songs[nowPos].isLike = isLiked
            setLike(isLiked)

            // DB 업데이트
            songDB.songDao().updateSong(songs[nowPos])

            // Firebase 저장 또는 삭제
            if (isLiked) {
                saveLikeToFirebase(songs[nowPos].id)
            } else {
                removeLikeFromFirebase(songs[nowPos].id)
            }

            // 여기에서 커스텀 Toast 호출
            showCustomToast(
                if (isLiked) "좋아요가 등록되었습니다" else "좋아요가 취소되었습니다",
                if (isLiked) R.drawable.ic_my_like_on else R.drawable.ic_my_like_off
            )
        }
    }

    private fun saveLikeToFirebase(songId: Int) {
        val db = FirebaseDatabase.getInstance().reference
        val userId = "user123" // 사용자 구분 ID, Firebase Auth와 연동하면 실제 사용자 ID로 바꿔도 됨
        db.child("likes").child(userId).child(songId.toString()).setValue(true)
    }

    private fun removeLikeFromFirebase(songId: Int) {
        val db = FirebaseDatabase.getInstance().reference
        val userId = "user123"
        db.child("likes").child(userId).child(songId.toString()).removeValue()
    }


    private fun showCustomToast(message: String, iconResId: Int) {
        val layout = LayoutInflater.from(this).inflate(R.layout.custom_toast, null)
        val text = layout.findViewById<TextView>(R.id.tv_toast_message)
        val icon = layout.findViewById<ImageView>(R.id.img_toast_icon)
        text.text = message
        icon.setImageResource(iconResId)

        val toast = Toast(this)
        toast.view = layout
        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 200)
        toast.duration = Toast.LENGTH_SHORT
        toast.show()
    }


    private fun getPlayingSongPosition(songId: Int): Int {
        return songs.indexOfFirst { it.id == songId }.coerceAtLeast(0)
    }

    private fun startSeekBarThread() {
        // 중복 실행 방지
        if (playerThread?.isAlive == true) return

        playerThread = Thread {
            try {
                while (isPlaying && currentPosition < totalDuration) {
                    Thread.sleep(1000)
                    currentPosition += 1000

                    runOnUiThread {
                        seekBar.progress = currentPosition
                        startTimeText.text = formatTime(currentPosition)
                    }

                    getSharedPreferences("music", MODE_PRIVATE)
                        .edit()
                        .putInt("currentPosition", currentPosition)
                        .apply()
                }
            } catch (e: InterruptedException) {
                // 안전한 종료 처리
                runOnUiThread {
                    isPlaying = false
                    startTimeText.text = formatTime(currentPosition)
                }
            }
        }

        playerThread?.start()
    }

    private fun stopSeekBarThread() {
        isPlaying = false

        // 안전하게 스레드 중단 처리
        try {
            if (playerThread?.isAlive == true) {
                playerThread?.interrupt()
            }
        } catch (e: Exception) {
            e.printStackTrace() // 예외 로그만 출력
        } finally {
            playerThread = null
        }
    }



    private fun formatTime(millis: Int): String {
        val seconds = millis / 1000
        val minutes = seconds / 60
        val remainSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainSeconds)
    }
    private fun setPlayer(song: Song) {
        val tvTitle = findViewById<TextView>(R.id.tv_song_title)
        val tvSinger = findViewById<TextView>(R.id.tv_song_singer)
        val ivSongAlbum = findViewById<ImageView>(R.id.iv_song_album)
        val tvLyrics = findViewById<TextView>(R.id.tv_lyrics)

        tvTitle.text = song.title
        tvSinger.text = song.singer
        ivSongAlbum.setImageResource(
            if (song.title == "ARMAGEDDON") R.drawable.armageddon_album
            else R.drawable.lilac_album
        )
        tvLyrics.text = if (song.title == "ARMAGEDDON")
            "We rise above it all (ARMAGEDDON)" else "내리는 꽃가루에 눈이 따끔해 아야"

        currentPosition = 0
        seekBar.progress = 0
        startTimeText.text = formatTime(0)
    }
    private fun updateSharedSongId(songId: Int) {
        getSharedPreferences("music", MODE_PRIVATE)
            .edit()
            .putInt("songId", songId)
            .apply()
    }

    private fun setLike(isLike: Boolean) {
        btnLike.setImageResource(
            if (isLike) R.drawable.ic_my_like_on else R.drawable.ic_my_like_off
        )
    }
}
