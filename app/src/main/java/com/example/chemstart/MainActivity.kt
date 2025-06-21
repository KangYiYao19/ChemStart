package com.example.chemstart

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chemstart.databinding.ActivityMainBinding

// MainActivity.kt
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val PREF_NAME = "ChemStartPrefs"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set click listeners for all level buttons
        setupLevelButtons()
    }

    private fun setupLevelButtons() {
        val buttons = listOf(
            Pair(binding.btnLevel1Drag, binding.btnLevel1Quiz),
            Pair(binding.btnLevel2Drag, binding.btnLevel2Quiz),
            Pair(binding.btnLevel3Drag, binding.btnLevel3Quiz),
            Pair(binding.btnLevel4Drag, binding.btnLevel4Quiz),
            Pair(binding.btnLevel5Drag, binding.btnLevel5Quiz)
        )

        for (i in buttons.indices) {
            val level = i + 1
            val (dragBtn, quizBtn) = buttons[i]
            val unlocked = if (level == 1) true else isLevelCompleted(level - 1)

            dragBtn.isEnabled = unlocked
            quizBtn.isEnabled = unlocked

            if (isLevelCompleted(level)) {
                dragBtn.text = "✅ Drag & Drop"
                quizBtn.text = "✅ Quiz"
            }

            dragBtn.setOnClickListener {
                startDragDropActivity(level)
            }

            quizBtn.setOnClickListener {
                startQuizActivity(level)
            }
        }
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

    private fun isLevelCompleted(level: Int): Boolean {
        val prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        val dragPassed = prefs.getBoolean("dragdrop_level_$level", false)
        val quizPassed = prefs.getBoolean("quiz_level_$level", false)
        return dragPassed && quizPassed
    }

    override fun onResume() {
        super.onResume()
        setupLevelButtons() // Refresh buttons & progress when coming back
    }
}