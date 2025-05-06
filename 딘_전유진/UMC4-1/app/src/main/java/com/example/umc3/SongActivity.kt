package com.example.umc3

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var seekRunnable: Runnable
    private var currentPosition = 0
    private val totalDuration = 60000 // 1분 (밀리초)

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

        seekBar = findViewById(R.id.seekBar)
        startTimeText = findViewById(R.id.tv_start_time)
        endTimeText = findViewById(R.id.tv_end_time)

        // 초기화
        tvTitle.text = title
        tvSinger.text = singer
        ivAlbum.setImageResource(R.drawable.lilac_album)
        seekBar.max = totalDuration
        endTimeText.text = formatTime(totalDuration)
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
                startSeekBarTimer()
                btnPlay.setImageResource(R.drawable.ic_pause)
            } else {
                pauseSeekBarTimer()
                btnPlay.setImageResource(R.drawable.ic_play)
            }
        }
    }

    private fun startSeekBarTimer() {
        seekRunnable = object : Runnable {
            override fun run() {
                if (currentPosition >= totalDuration) {
                    handler.removeCallbacks(this)
                    isPlaying = false
                    return
                }
                currentPosition += 1000
                seekBar.progress = currentPosition
                startTimeText.text = formatTime(currentPosition)
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(seekRunnable)
    }

    private fun pauseSeekBarTimer() {
        handler.removeCallbacks(seekRunnable)
    }

    private fun formatTime(millis: Int): String {
        val seconds = millis / 1000
        val minutes = seconds / 60
        val remainSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainSeconds)
    }
}
