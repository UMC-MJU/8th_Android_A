package com.example.chapter4

import android.os.Bundle
import android.os.Message
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.chapter4.databinding.ActivityMainBinding
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    // ViewBinding 변수 선언
    private lateinit var binding: ActivityMainBinding

    // 타이머 작동 여부를 저장하는 변수
    var started = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 상태바와 내비게이션 바 영역까지 레이아웃 확장
        enableEdgeToEdge()

        // ViewBinding 초기화
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 시작, 일시정지, 초기화 버튼 클릭 리스너 설정
        binding.buttonStart.setOnClickListener { start() }
        binding.buttonPause.setOnClickListener { pause() }
        binding.buttonClear.setOnClickListener { stop() }
    }

    // 메시지 구분을 위한 상수 정의
    val SET_TIME = 51
    val RESET = 52


    val handler = object: Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                // 시간 업데이트 메시지를 받았을 경우
                SET_TIME -> {
                    binding.textTimer.text = formatTime(msg.arg1)
                }
                // 타이머 초기화 메시지를 받았을 경우
                RESET -> {
                    binding.textTimer.text = "00:00"
                }
            }
        }
    }

    // 타이머 시작 함수
    fun start() {
        started = true
        // 새로운 스레드를 시작하여 1초마다 시간을 증가시킴
        thread(start = true) {
            var total = 0
            while (started) {
                Thread.sleep(1000) // 1초 대기
                if (!started) break // 중단되었을 경우 루프 종료
                total += 1

                // 메시지를 생성해 현재 시간을 UI에 전달
                val msg = Message()
                msg.what = SET_TIME
                msg.arg1 = total
                handler.sendMessage(msg)
            }
        }
    }

    // 타이머 일시정지 함수
    fun pause() {
        started = false
    }

    // 타이머 중지 및 초기화 함수
    fun stop() {
        started = false
        binding.textTimer.text = "00:00"
    }

    // 초 단위 시간을 "MM : SS" 형식의 문자열로 변환
    fun formatTime(time: Int): String {
        val minute = String.format("%02d", time / 60)
        val second = String.format("%02d", time % 60)
        return "$minute : $second"
    }
}


