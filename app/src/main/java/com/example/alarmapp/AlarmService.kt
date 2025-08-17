package com.example.alarmapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder

class AlarmService : Service() {

    private lateinit var mediaPlayer: MediaPlayer

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()

        val notification: Notification = Notification.Builder(this, "alarm_channel")
            .setContentTitle("Alarm is ringing")
            .setContentText("Solve the game to stop it")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(1, notification)

        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound)
        mediaPlayer.isLooping = true
        mediaPlayer.start()

        val gameIntent = Intent(this, GameActivity::class.java)
        gameIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(gameIntent)

        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "alarm_channel",
                "Alarm Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
