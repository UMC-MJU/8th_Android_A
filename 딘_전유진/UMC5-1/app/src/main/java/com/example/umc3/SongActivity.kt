package com.example.umc3

import android.os.Bundle
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song)

        val title = intent.getStringExtra("title") ?: "제목 없음"
        val singer = intent.getStringExtra("singer") ?: "가수 없음"

        val tvTitle = findViewById<TextView>(R.id.tv_song_title)
        val tvSinger = findViewById<TextView>(R.id.tv_song_singer)
        val ivAlbum = findViewById<ImageView>(R.id.iv_song_album)
        val btnRepeat = findViewById<ImageView>(R.id.btn_repeat)
        val btnPlayAll = findViewById<ImageView>(R.id.btn_play_all)
        val btnPlay = findViewById<ImageView>(R.id.btn_play)
        val btnBack = findViewById<ImageView>(R.id.btn_album_back)
        btnBack.setOnClickListener {
            finish() // SongActivity 종료
        }

        seekBar = findViewById(R.id.seekBar)
        startTimeText = findViewById(R.id.tv_start_time)
        endTimeText = findViewById(R.id.tv_end_time)

        // 총 재생시간 설정
        seekBar.max = totalDuration
        endTimeText.text = formatTime(totalDuration)

        // SharedPreferences에서 이전 진행 위치 복원
        val savedPosition = getSharedPreferences("music", MODE_PRIVATE)
            .getInt("currentPosition", 0)
        currentPosition = savedPosition
        seekBar.progress = currentPosition
        startTimeText.text = formatTime(currentPosition)

        // 기본 UI 세팅
        tvTitle.text = title
        tvSinger.text = singer
        ivAlbum.setImageResource(R.drawable.lilac_album)

        // 버튼 기능
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
}
