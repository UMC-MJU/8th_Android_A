package com.example.umc4

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.umc3.R

class MainActivity : AppCompatActivity() {

    private lateinit var tvTimer: TextView
    private lateinit var btnStart: Button
    private lateinit var btnPause: Button
    private lateinit var btnClear: Button

    private var isRunning = false
    private var elapsedTime = 0
    private var thread: Thread? = null

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvTimer = findViewById(R.id.tv_timer)
        btnStart = findViewById(R.id.btn_start)
        btnPause = findViewById(R.id.btn_pause)
        btnClear = findViewById(R.id.btn_clear)

        btnStart.setOnClickListener {
            if (!isRunning) {
                isRunning = true
                startStopwatch()
            }
        }

        btnPause.setOnClickListener {
            isRunning = false
        }

        btnClear.setOnClickListener {
            if (isRunning) {
                // Start → Clear: 시간은 계속 흐르면서 0초부터 다시 시작
                elapsedTime = 0
            } else {
                // Pause → Clear: 멈추고 초기화
                elapsedTime = 0
                updateTimerText(0)
            }
        }
    }

    private fun startStopwatch() {
        thread = Thread {
            while (isRunning) {
                Thread.sleep(1000)
                elapsedTime++
                handler.post {
                    updateTimerText(elapsedTime)
                }
            }
        }
        thread?.start()
    }

    private fun updateTimerText(seconds: Int) {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        tvTimer.text = String.format("%02d:%02d:%02d", h, m, s)
    }
}
