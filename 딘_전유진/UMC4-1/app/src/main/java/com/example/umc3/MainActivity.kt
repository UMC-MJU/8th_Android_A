package com.example.umc3

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
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
        setContentView(R.layout.activity_main)

        // 처음 시작 시 홈 프래그먼트 표시
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, HomeFragment())
            .commit()

        // 🔽 mini_player 클릭 시 SongActivity 이동 처리
        val miniPlayer = findViewById<LinearLayout>(R.id.mini_player)
        val title = findViewById<TextView>(R.id.tv_title_main)
        val singer = findViewById<TextView>(R.id.tv_singer_main)

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
