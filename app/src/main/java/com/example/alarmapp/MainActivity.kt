package com.example.alarmapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var timePicker: TimePicker
    private lateinit var alarmManager: AlarmManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()


        timePicker = findViewById(R.id.timePicker)
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        // Dark theme background
        window.decorView.setBackgroundColor(resources.getColor(android.R.color.background_dark))

        findViewById<android.widget.Button>(R.id.setAlarmButton).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    setAlarm()
                } else {
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    startActivity(intent)
                    Toast.makeText(
                        this,
                        "Please grant permission to schedule exact alarms",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                setAlarm()
            }
        }
    }

    private fun setAlarm() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, timePicker.hour)
            set(Calendar.MINUTE, timePicker.minute)
            set(Calendar.SECOND, 0)
        }

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            // If selected time is before current time, set for next day
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
            Toast.makeText(this, "Alarm set successfully", Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            Toast.makeText(this, "Exact alarm permission not granted", Toast.LENGTH_LONG).show()
        }
    }
}
