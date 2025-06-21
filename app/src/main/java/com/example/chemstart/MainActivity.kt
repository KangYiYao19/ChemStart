package com.example.chemstart

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chemstart.databinding.ActivityMainBinding

// MainActivity.kt
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set click listeners for all level buttons
        setupLevelButtons()
    }

    private fun setupLevelButtons() {
        // Level 1
        binding.btnLevel1Drag.setOnClickListener { startDragDropActivity(1) }
        binding.btnLevel1Quiz.setOnClickListener { startQuizActivity(1) }

        // Level 2
        binding.btnLevel2Drag.setOnClickListener { startDragDropActivity(2) }
        binding.btnLevel2Quiz.setOnClickListener { startQuizActivity(2) }

        // Level 3
        binding.btnLevel3Drag.setOnClickListener { startDragDropActivity(3) }
        binding.btnLevel3Quiz.setOnClickListener { startQuizActivity(3) }

        // Level 4
        binding.btnLevel4Drag.setOnClickListener { startDragDropActivity(4) }
        binding.btnLevel4Quiz.setOnClickListener { startQuizActivity(4) }

        // Level 5
        binding.btnLevel5Drag.setOnClickListener { startDragDropActivity(5) }
        binding.btnLevel5Quiz.setOnClickListener { startQuizActivity(5) }
    }

    private fun startDragDropActivity(level: Int) {
        val intent = Intent(this, DragDropActivity::class.java).apply {
            putExtra("LEVEL", level)
        }
        startActivity(intent)
    }

    private fun startQuizActivity(level: Int) {
        val intent = Intent(this, QuizActivity::class.java).apply {
            putExtra("LEVEL", level)
        }
        startActivity(intent)
    }
}