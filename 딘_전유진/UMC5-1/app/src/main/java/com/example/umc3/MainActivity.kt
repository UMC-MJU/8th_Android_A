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

class MainActivity : AppCompatActivity() {

    private val songActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val albumName = result.data?.getStringExtra("albumName")
            Toast.makeText(this, "받은 앨범: $albumName", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)  // 가장 먼저 호출

        // 진행선 View
        val progressBarView = findViewById<View>(R.id.progress_bar_mini)
        val seekBarMini = findViewById<SeekBar>(R.id.seekBar_mini)
        val title = findViewById<TextView>(R.id.tv_title_main)
        val singer = findViewById<TextView>(R.id.tv_singer_main)
        val miniPlayer = findViewById<LinearLayout>(R.id.mini_player)
        val btnMainPlay = findViewById<ImageView>(R.id.btn_main_play)

        btnMainPlay.setOnClickListener {
            MusicPlayerState.togglePlayPause()

            btnMainPlay.setImageResource(
                if (MusicPlayerState.isPlaying) R.drawable.ic_pause
                else R.drawable.ic_play
            )

            // 저장
            getSharedPreferences("music", MODE_PRIVATE)
                .edit()
                .putBoolean("isPlaying", MusicPlayerState.isPlaying)
                .apply()
        }

        // 프래그먼트 시작
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, HomeFragment())
            .commit()

        val sharedPrefs = getSharedPreferences("music", MODE_PRIVATE)
        val handler = Handler(Looper.getMainLooper())
        val totalDuration = 60000

        // 진행 바: 선(View) 업데이트
        handler.post(object : Runnable {
            override fun run() {
                val currentPosition = sharedPrefs.getInt("currentPosition", 0)
                val progressRatio = currentPosition.toFloat() / totalDuration
                val screenWidth = resources.displayMetrics.widthPixels
                val newWidth = (screenWidth * progressRatio).toInt()

                val params = progressBarView.layoutParams
                params.width = newWidth
                progressBarView.layoutParams = params

                // SeekBar도 동기화
                seekBarMini.progress = currentPosition

                handler.postDelayed(this, 1000)
            }
        })

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
}
