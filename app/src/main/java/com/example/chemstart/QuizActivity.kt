package com.example.chemstart

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chemstart.data.ElementDatabase
import com.example.chemstart.databinding.ActivityQuizBinding

class QuizActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuizBinding
    private lateinit var currentElement: ElementDatabase.Element
    private var currentQuestion = 0
    private var score = 0
    private val totalQuestions = 10
    private lateinit var elements: List<ElementDatabase.Element>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val level = intent.getIntExtra("LEVEL", 1)
        elements = ElementDatabase.getElementsForLevel(level).shuffled()

        showNextQuestion()
        setupAnswerButtons()

        binding.btnNext.setOnClickListener {
            checkAnswer()
            showNextQuestion()
        }
    }

    private fun showNextQuestion() {
        if (currentQuestion >= totalQuestions) {
            showResult()
            return
        }

        currentElement = elements[currentQuestion % elements.size]
        binding.tvQuestion.text = "What is the symbol for ${currentElement.name}?"
        binding.tvQuestionCount.text = "${currentQuestion + 1}/$totalQuestions"

        // Set radio button options
        val options = listOf(
            binding.rbOption1,
            binding.rbOption2,
            binding.rbOption3,
            binding.rbOption4
        )

        // Set correct answer in random position
        val randomPosition = (0..3).random()
        options[randomPosition].text = currentElement.symbol

        // Fill other options with random wrong answers
        options.forEachIndexed { index, radioButton ->
            if (index != randomPosition) {
                radioButton.text = getRandomWrongSymbol(currentElement.symbol)
            }
        }

        // Clear selection
        binding.rgOptions.clearCheck()

        // Update progress
        binding.progressBar.progress = currentQuestion + 1
    }

    private fun getRandomWrongSymbol(correctSymbol: String): String {
        val wrongSymbols = listOf("X", "Y", "Z", "A", "B", "C") // Add more if needed
        return wrongSymbols.filter { it != correctSymbol }.random()
    }

    private fun setupAnswerButtons() {
        // RadioGroup handles selection automatically
    }

    private fun checkAnswer() {
        val selectedId = binding.rgOptions.checkedRadioButtonId
        if (selectedId == -1) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedRadioButton = findViewById<RadioButton>(selectedId)
        if (selectedRadioButton.text == currentElement.symbol) {
            score++
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Wrong! It's ${currentElement.symbol}", Toast.LENGTH_SHORT).show()
        }

        currentQuestion++
    }

    private fun showResult() {
        val accuracy = score.toDouble() / totalQuestions
        if (accuracy >= 0.7) {
            saveQuizCompleted(intent.getIntExtra("LEVEL", 1))
        }

        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra("SCORE", score)
            putExtra("TOTAL", totalQuestions)
        }
        startActivity(intent)
        finish()
    }

    private fun saveQuizCompleted(level: Int) {
        getSharedPreferences("ChemStartPrefs", MODE_PRIVATE)
            .edit()
            .putBoolean("quiz_level_$level", true)
            .apply()
    }
}