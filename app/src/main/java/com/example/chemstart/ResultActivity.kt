package com.example.chemstart

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chemstart.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get score data from intent
        val score = intent.getIntExtra("SCORE", 0)
        val totalQuestions = intent.getIntExtra("TOTAL", 10)

        // Calculate percentage
        val percentage = (score.toFloat() / totalQuestions.toFloat() * 100).toInt()

        // Display results
        binding.tvScore.text = "$score/$totalQuestions"
        binding.tvPercentage.text = "$percentage%"
        binding.progressBar.max = totalQuestions
        binding.progressBar.progress = score

        // Set result message based on performance
        val message = when {
            percentage >= 90 -> "Excellent! You're a chemistry master!"
            percentage >= 70 -> "Good job! You know your elements well!"
            percentage >= 50 -> "Not bad! Keep practicing!"
            else -> "Keep learning! You'll get better!"
        }
        binding.tvMessage.text = message

        // Set up button listeners
        binding.btnRestart.setOnClickListener {
            finish() // Go back to quiz activity which will restart
        }

        binding.btnMenu.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }
}