package com.example.umc3

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

        // 진행선 View도 반영
        val totalDuration = 60000
        val ratio = currentPosition.toFloat() / totalDuration
        val screenWidth = resources.displayMetrics.widthPixels
        val newWidth = (screenWidth * ratio).toInt()
        val params = progressBarView.layoutParams
        params.width = newWidth
        progressBarView.layoutParams = params

        // 재생 상태 기억
        MusicPlayerState.isPlaying = isPlaying
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
        /*val btnStartService = findViewById<Button>(R.id.btn_start_service)
        btnStartService.setOnClickListener {
            val intent = Intent(this, CounterService::class.java)
            ContextCompat.startForegroundService(this, intent)
        }*/
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val shared = getSharedPreferences("music", MODE_PRIVATE)
        shared.edit()
            .putString("title", "LILAC")
            .putString("singer", "아이유 (IU)")
            .putInt("imageResId", R.drawable.lilac_album)
            .putString("lyrics", "내리는 꽃가루에 눈이 따끔해 아야")
            .putBoolean("isPlaying", true)
            .putInt("currentPosition", 0)
            .apply()

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

}
