package com.example.umc5

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val memoText = intent.getStringExtra("memo")
        val textViewMemo: TextView = findViewById(R.id.textViewMemo)
        textViewMemo.text = memoText
    }
}
