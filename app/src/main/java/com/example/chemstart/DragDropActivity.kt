package com.example.chemstart

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.ContextThemeWrapper
import android.view.DragEvent
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chemstart.data.ElementDatabase
import com.example.chemstart.databinding.ActivityDragDropBinding

class DragDropActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDragDropBinding
    private lateinit var elements: List<ElementDatabase.Element>
    private var score = 0
    private lateinit var countDownTimer: CountDownTimer
    private var correctPlacements = 0
    private var totalElements = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDragDropBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val level = intent.getIntExtra("LEVEL", 1)
        elements = ElementDatabase.getElementsForLevel(level)
        totalElements = elements.size

        setupGame(level)
        startTimer(level)

        binding.btnCheck.setOnClickListener { checkAnswers() }
        binding.btnReset.setOnClickListener { resetGame() }
    }

    @SuppressLint("SetTextI18n")
    private fun setupGame(level: Int) {
        binding.tvTimer.text = "Time: ${when (level) {
            1 -> 90
            2 -> 75
            3 -> 60
            4 -> 45
            5 -> 120
            else -> 90
        }}"
        binding.tvTimer.setTextColor(ContextCompat.getColor(this, R.color.timer_default))
        binding.tvScore.text = "Score: $score"

        // Setup grid
        binding.gridPeriodicTable.layoutManager = GridLayoutManager(this, 18)
        binding.gridPeriodicTable.adapter = PeriodicTableAdapter(level)

        // Create draggable tiles
        createElementTiles(elements)
    }

    private fun createElementTiles(elements: List<ElementDatabase.Element>) {
        binding.elementTray.removeAllViews()

        elements.forEach { element ->
            val tile = TextView(this).apply {
                text = element.symbol
                textSize = 16f
                setTextColor(Color.WHITE)
                setBackgroundColor(getColorForElement(element))
                setPadding(8, 8, 8, 8)
                tag = element

                setOnLongClickListener { v ->
                    val dragShadow = View.DragShadowBuilder(v)
                    v.startDragAndDrop(null, dragShadow, v, 0)
                    v.visibility = View.INVISIBLE
                    true
                }
            }
            binding.elementTray.addView(tile)
        }
    }

    private fun setupDragAndDrop() {
        binding.gridPeriodicTable.setOnDragListener { view, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> true
                DragEvent.ACTION_DRAG_ENTERED -> {
                    view.setBackgroundColor(ContextCompat.getColor(this, R.color.drag_target_highlight))
                    true
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    view.setBackgroundColor(Color.TRANSPARENT)
                    true
                }
                DragEvent.ACTION_DROP -> {
                    val draggedView = event.localState as TextView
                    val element = draggedView.tag as ElementDatabase.Element
                    val targetCell = view as TextView

                    if (isCorrectPlacement(element, targetCell)) {
                        score += 10
                        correctPlacements++
                        binding.tvScore.text = "Score: $score"
                        targetCell.text = element.symbol
                        targetCell.setBackgroundColor(getColorForElement(element))
                    } else {
                        score = maxOf(0, score - 5)
                        binding.tvScore.text = "Score: $score"
                        draggedView.visibility = View.VISIBLE
                    }
                    view.setBackgroundColor(Color.TRANSPARENT)
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    view.setBackgroundColor(Color.TRANSPARENT)
                    val draggedView = event.localState as? TextView
                    draggedView?.visibility = View.VISIBLE
                    true
                }
                else -> false
            }
        }
    }

    private fun isCorrectPlacement(element: ElementDatabase.Element, targetCell: TextView): Boolean {
        // Implement your logic to check if element is placed in correct cell
        // This would compare element's group/period with target cell's position
        return true // Placeholder - implement your actual logic
    }

    private fun getColorForElement(element: ElementDatabase.Element): Int {
        return when (element.category) {
            "metal" -> Color.parseColor("#FFA500") // Orange
            "nonmetal" -> Color.parseColor("#ADD8E6") // Light Blue
            "metalloid" -> Color.parseColor("#90EE90") // Light Green
            "noble gas" -> Color.parseColor("#FFC0CB") // Pink
            else -> Color.GRAY
        }
    }

    private fun checkAnswers() {
        if (correctPlacements == totalElements) {
            showResultDialog(true)
        } else {
            AlertDialog.Builder(this)
                .setTitle("Incomplete")
                .setMessage("You've placed $correctPlacements out of $totalElements elements correctly")
                .setPositiveButton("OK", null)
                .show()
        }
    }

    private fun resetGame() {
        score = 0
        correctPlacements = 0
        binding.tvScore.text = "Score: $score"
        setupGame(intent.getIntExtra("LEVEL", 1))
    }

    private fun startTimer(level: Int) {
        val timeLimitMillis = when (level) {
            1 -> 90_000L  // Level 1: 90 seconds
            2 -> 75_000L  // Level 2: 75 seconds
            3 -> 60_000L  // Level 3: 60 seconds
            4 -> 45_000L  // Level 4: 45 seconds
            5 -> 120_000L // Level 5: 120 seconds
            else -> 90_000L // Default
        }

        countDownTimer = object : CountDownTimer(timeLimitMillis, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                // Update timer display every second
                val secondsRemaining = millisUntilFinished / 1000
                binding.tvTimer.text = "Time: $secondsRemaining"

                // Change color when time is running low
                if (secondsRemaining <= 10) {
                    binding.tvTimer.setTextColor(ContextCompat.getColor(this@DragDropActivity, R.color.red))
                }
            }

            override fun onFinish() {
                // Time's up!
                binding.tvTimer.text = "Time: 0"
                binding.tvTimer.setTextColor(ContextCompat.getColor(this@DragDropActivity, R.color.red))

                // Show results dialog
                showResultDialog(false)

                // Disable further interactions
                disableGameInteraction()
            }
        }.start()
    }

    // Helper function to disable game interaction
    private fun disableGameInteraction() {
        binding.elementTray.isEnabled = false
        binding.gridPeriodicTable.isEnabled = false
        binding.btnCheck.isEnabled = false
        binding.btnReset.isEnabled = false
    }

    private fun showResultDialog(success: Boolean = false) {
        countDownTimer.cancel()

        AlertDialog.Builder(this)
            .setTitle(if (success) "Congratulations!" else "Time's Up!")
            .setMessage("Your final score: $score")
            .setPositiveButton("OK") { _: DialogInterface, _: Int ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun getLevelTitle(level: Int): String {
        return when (level) {
            1 -> getString(R.string.level_1_title) // "First 20 Elements"
            2 -> getString(R.string.level_2_title) // "Alkali Metals"
            3 -> getString(R.string.level_3_title) // "Transition Metals"
            4 -> getString(R.string.level_4_title) // "Halogens"
            5 -> getString(R.string.level_5_title) // "Full Periodic Table"
            else -> getString(R.string.unknown_level) // "Unknown Level"
        }
    }
}

// Simple adapter for the grid
class PeriodicTableAdapter(private val level: Int) : RecyclerView.Adapter<PeriodicTableAdapter.ViewHolder>() {

    class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView) {
        fun bind(position: Int) {
            // Custom cell content can be added here
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val textView = TextView(
            ContextThemeWrapper(parent.context, R.style.PeriodicTableCell),
            null,
            0
        )
        return ViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = when (level) {
        1 -> 20
        2 -> 6
        3 -> 4
        4 -> 6
        5 -> 118
        else -> 20
    }
}