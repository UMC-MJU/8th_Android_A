package com.example.mission1

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // XML 파일을 로드

        // ImageView 클릭 이벤트 설정
        val happyIcon = findViewById<ImageView>(R.id.icon1)
        happyIcon.setOnClickListener {
            val intent = Intent(this, ResultActivity::class.java)
            startActivity(intent)  // 액티비티 이동
        }
    }
}
