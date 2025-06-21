package com.example.chemstart

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.DragEvent
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.chemstart.data.ElementDatabase
import com.example.chemstart.databinding.ActivityDragDropBinding

class DragDropActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDragDropBinding
    private lateinit var elements: List<ElementDatabase.Element>
    private var correctPlacements = 0
    private var incorrectPlacements = 0
    private lateinit var countDownTimer: CountDownTimer
    private var totalElements = 0
    private var remainingElements = mutableListOf<ElementDatabase.Element>()
    private var currentElement: ElementDatabase.Element? = null

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
        binding.tvTimer.text = "Time: ${getTimeForLevel(level) / 1000}"
        binding.tvTimer.setTextColor(ContextCompat.getColor(this, R.color.timer_default))
        binding.tvCorrectScore.text = "0"
        binding.tvIncorrectScore.text = "0"

        renderPeriodicTable(elements)
        setupGridDropTargets()

        remainingElements = elements.shuffled().toMutableList()
        createNextElementTile()
    }

    private fun createNextElementTile() {
        binding.draggableElementContainer.removeAllViews()

        if (remainingElements.isEmpty()) {
            checkAnswers()
            return
        }

        currentElement = remainingElements.removeFirst()
        binding.tvDraggableSymbol.text = currentElement!!.symbol
        binding.tvDraggableName.text = currentElement!!.name

        val tile = TextView(this).apply {
            text = currentElement!!.symbol
            textSize = 24f
            setTextColor(Color.WHITE)
            setBackgroundColor(getColorForElement(currentElement!!))
            gravity = Gravity.CENTER
            setPadding(16, 16, 16, 16)
            tag = currentElement

            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            setOnLongClickListener { v ->
                val dragShadow = View.DragShadowBuilder(v)
                v.startDragAndDrop(null, dragShadow, v, 0)
                v.visibility = View.INVISIBLE
                true
            }
        }

        binding.draggableElementContainer.addView(tile)
    }

    private fun renderPeriodicTable(elements: List<ElementDatabase.Element>) {
        val grid = binding.periodicTableGrid
        grid.removeAllViews()
        val elementMap = elements.associateBy { Pair(it.period, it.group) }

        for (period in 1..7) {
            for (group in 1..18) {
                val key = Pair(period, group)
                val element = elementMap[key]

                val cell = TextView(this).apply {
                    text = ""
                    tag = element
                    gravity = Gravity.CENTER
                    textSize = 14f
                    setPadding(2, 2, 2, 2)
                    setBackgroundResource(R.drawable.cell_border)

                    layoutParams = GridLayout.LayoutParams(
                        GridLayout.spec(period - 1), GridLayout.spec(group - 1)
                    ).apply {
                        width = dpToPx(40)
                        height = dpToPx(40)
                        setMargins(dpToPx(1), dpToPx(1), dpToPx(1), dpToPx(1))
                    }
                }

                grid.addView(cell)
            }
        }
    }

    private fun setupGridDropTargets() {
        for (i in 0 until binding.periodicTableGrid.childCount) {
            val cell = binding.periodicTableGrid.getChildAt(i) as TextView
            cell.setOnDragListener { view, event ->
                when (event.action) {
                    DragEvent.ACTION_DROP -> {
                        val draggedElement = currentElement ?: return@setOnDragListener true
                        val targetCell = view as TextView
                        val targetElement = targetCell.tag as? ElementDatabase.Element ?: return@setOnDragListener true

                        if (targetElement.group == draggedElement.group &&
                            targetElement.period == draggedElement.period
                        ) {
                            targetCell.text = draggedElement.symbol
                            targetCell.setBackgroundColor(getColorForElement(draggedElement))
                            correctPlacements++
                            binding.tvCorrectScore.text = correctPlacements.toString()
                        } else {
                            incorrectPlacements++
                            binding.tvIncorrectScore.text = incorrectPlacements.toString()
                        }

                        createNextElementTile()
                        true
                    }

                    DragEvent.ACTION_DRAG_ENDED -> {
                        binding.draggableElementContainer.getChildAt(0)?.visibility = View.VISIBLE
                        true
                    }

                    else -> true
                }
            }
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
        correctPlacements = 0
        incorrectPlacements = 0
        setupGame(intent.getIntExtra("LEVEL", 1))
    }

    private fun startTimer(level: Int) {
        countDownTimer = object : CountDownTimer(getTimeForLevel(level), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                binding.tvTimer.text = "Time: $seconds"
                if (seconds <= 10) {
                    binding.tvTimer.setTextColor(ContextCompat.getColor(this@DragDropActivity, R.color.red))
                }
            }

            override fun onFinish() {
                binding.tvTimer.text = "Time: 0"
                showResultDialog(false)
            }
        }.start()
    }

    private fun getTimeForLevel(level: Int): Long {
        return when (level) {
            1 -> 90_000L
            2 -> 75_000L
            3 -> 60_000L
            4 -> 45_000L
            5 -> 120_000L
            else -> 90_000L
        }
    }

    private fun showResultDialog(success: Boolean) {
        countDownTimer.cancel()
        AlertDialog.Builder(this)
            .setTitle(if (success) "Congratulations!" else "Time's Up!")
            .setMessage("Correct: $correctPlacements\nIncorrect: $incorrectPlacements")
            .setPositiveButton("OK") { _: DialogInterface, _: Int -> finish() }
            .setCancelable(false)
            .show()
    }

    private fun getColorForElement(element: ElementDatabase.Element): Int {
        return when (element.category.lowercase()) {
            "metal" -> Color.parseColor("#FFA500")
            "nonmetal" -> Color.parseColor("#ADD8E6")
            "metalloid" -> Color.parseColor("#90EE90")
            "noble gas" -> Color.parseColor("#FFC0CB")
            else -> Color.GRAY
        }
    }

    private fun dpToPx(dp: Int): Int =
        (dp * Resources.getSystem().displayMetrics.density).toInt()
}
