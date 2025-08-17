package com.example.alarmapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class AlarmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        // Ensure service is running and show GameActivity
        startService(Intent(this, AlarmService::class.java))
        startActivity(Intent(this, GameActivity::class.java))
        finish()
    }
}
