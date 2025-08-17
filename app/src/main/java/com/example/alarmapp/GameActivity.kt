package com.example.alarmapp

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

/**
 * GameActivity:
 * - Enforces a minimum play time of 60 seconds.
 * - Generates a NEW question after every attempt.
 * - After the 60s timer finishes, the next CORRECT answer stops the AlarmService.
 */
class GameActivity : AppCompatActivity() {

    private lateinit var timerTextView: TextView
    private lateinit var questionTextView: TextView
    private lateinit var answerEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var feedbackTextView: TextView

    private var currentAnswer: Int = 0
    private var timerFinished: Boolean = false
    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()


        // Block back navigation so user can't bypass the challenge.
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Intentionally left blank to block back button.
            }
        })

        setContentView(R.layout.activity_game)

        timerTextView = findViewById(R.id.timerTextView)
        questionTextView = findViewById(R.id.questionTextView)
        answerEditText = findViewById(R.id.answerEditText)
        submitButton = findViewById(R.id.submitButton)
        feedbackTextView = findViewById(R.id.feedbackTextView)

        // Start with the first question and timer
        generateNewQuestion()
        startMinuteTimer()

        submitButton.setOnClickListener {
            val text = answerEditText.text.toString().trim()
            if (text.isEmpty()) {
                Toast.makeText(this, "Enter an answer", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = text.toIntOrNull()
            if (user == null) {
                Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show()
                answerEditText.setText("")
                return@setOnClickListener
            }

            if (!timerFinished) {
                // Before 60s: accept the answer, give feedback, generate a NEW question
                if (user == currentAnswer) {
                    feedbackTextView.text = "Correct — keep going until time is up."
                } else {
                    feedbackTextView.text = "Wrong — try the next one."
                }
                // Always produce a new question after an attempt
                generateNewQuestion()
            } else {
                // After 60s: first correct answer will stop the alarm
                if (user == currentAnswer) {
                    // Stop the foreground service that plays the alarm sound
                    stopService(Intent(this, AlarmService::class.java))
                    Toast.makeText(this, "Correct — Alarm stopped.", Toast.LENGTH_SHORT).show()
                    finishAffinity() // exit the app UI stack
                } else {
                    feedbackTextView.text = "Incorrect — try another."
                    generateNewQuestion()
                }
            }

            // Clear input for the next question
            answerEditText.setText("")
        }
    }

    private fun generateNewQuestion() {
        // Use two random numbers for an addition problem; change range for difficulty
        val a = Random.nextInt(10, 100) // two-digit numbers
        val b = Random.nextInt(10, 100)
        currentAnswer = a + b
        questionTextView.text = "What is $a + $b ?"
    }

    private fun startMinuteTimer() {
        // Cancel any existing timer before starting
        countDownTimer?.cancel()
        timerFinished = false

        countDownTimer = object : CountDownTimer(60_000, 1_000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                timerTextView.text = "Time left: ${secondsLeft}s"
            }

            override fun onFinish() {
                timerFinished = true
                timerTextView.text = "Time's up — answer one correct to stop alarm"
                feedbackTextView.text = "Time finished. Now solve one correctly to stop the alarm."
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}
