package com.example.umc3

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SongActivity : AppCompatActivity() {

    private var isRepeatOn = false
    private var isPlayAllOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song)

        // 전달받은 데이터
        val title = intent.getStringExtra("title") ?: "제목 없음"
        val singer = intent.getStringExtra("singer") ?: "가수 없음"

        // 바인딩
        val tvTitle = findViewById<TextView>(R.id.tv_song_title)
        val tvSinger = findViewById<TextView>(R.id.tv_song_singer)
        val ivAlbum = findViewById<ImageView>(R.id.iv_song_album)

        val btnRepeat = findViewById<ImageView>(R.id.btn_repeat)
        val btnPlayAll = findViewById<ImageView>(R.id.btn_play_all)

        // UI 렌더링
        tvTitle.text = title
        tvSinger.text = singer
        ivAlbum.setImageResource(R.drawable.lilac_album) // 임시 이미지

        // 반복 버튼 클릭 시 이미지 변경
        btnRepeat.setOnClickListener {
            isRepeatOn = !isRepeatOn
            btnRepeat.setImageResource(
                if (isRepeatOn) R.drawable.ic_repeat else R.drawable.ic_repeat_on
            )
        }

        // 전체재생 버튼 클릭 시 이미지 변경
        btnPlayAll.setOnClickListener {
            isPlayAllOn = !isPlayAllOn
            btnPlayAll.setImageResource(
                if (isPlayAllOn) R.drawable.ic_random else R.drawable.ic_random_on
            )
        }
    }
}
