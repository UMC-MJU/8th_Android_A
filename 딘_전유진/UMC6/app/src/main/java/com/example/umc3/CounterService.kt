package com.example.umc3  // 패키지 경로는 본인 프로젝트에 맞게 조정

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

class CounterService : Service() {

    private var isRunning = false
    private var count = 0

    private val channelId = "counter_channel"
    private val notificationId = 1

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isRunning = true

        startForeground(notificationId, createNotification("Counting started..."))

        CoroutineScope(Dispatchers.Default).launch {
            while (isRunning && count < 1000) {
                delay(1000)
                count++

                val updatedNotification = createNotification("현재 숫자: $count")
                val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                manager.notify(notificationId, updatedNotification)
            }

            stopSelf()
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(content: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Foreground 카운터 실행 중")
            .setContentText(content)
            .setSmallIcon(R.drawable.img)  // 존재하는 아이콘으로 교체
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .setProgress(1000, count, false)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Foreground Counter Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
