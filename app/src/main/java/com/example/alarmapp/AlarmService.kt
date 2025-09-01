package com.example.alarmapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log

class AlarmService : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var audioManager: AudioManager
    private var maxVolume: Int = 0

    private val volumeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            if (currentVolume < maxVolume) {
                // Reset back to max if user lowers it
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    maxVolume,
                    AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE
                )
                Log.d("AlarmService", "Volume reset to max")
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()

        val notification: Notification = Notification.Builder(this, "alarm_channel")
            .setContentTitle("Alarm is ringing")
            .setContentText("Solve the game to stop it")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(1, notification)

        // Set up AudioManager
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

        // Force volume to max
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            maxVolume,
            AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE
        )

        // Register receiver to monitor volume changes
        val filter = IntentFilter("android.media.VOLUME_CHANGED_ACTION")
        registerReceiver(volumeReceiver, filter)

        // Start alarm sound
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound)
        mediaPlayer.isLooping = true
        mediaPlayer.start()

        // Launch game activity
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
        unregisterReceiver(volumeReceiver) // Clean up volume lock listener
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
