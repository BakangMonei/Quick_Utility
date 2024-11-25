package com.madassignment.quickutility.Fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.madassignment.quickutility.R
import kotlin.math.*

class CalculatorFragment : Fragment() {

    private lateinit var display: EditText
    private lateinit var calculationHistory: TextView
    private lateinit var simplePanel: GridLayout
    private lateinit var scientificPanel: GridLayout
    private lateinit var toggleModeButton: ToggleButton

    private var currentInput = ""
    private var operator = ""
    private var firstValue = 0.0
    private var calculationHistoryList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calculator, container, false)

        display = view.findViewById(R.id.display)
        calculationHistory = view.findViewById(R.id.calculationHistory)
        simplePanel = view.findViewById(R.id.simplePanel)
        scientificPanel = view.findViewById(R.id.scientificPanel)
        toggleModeButton = view.findViewById(R.id.btnToggleMode)

        setupModeToggle()
        setupNumberButtons(view)
        setupOperationButtons(view)
        setupScientificButtons(view)

        return view
    }

    private fun setupModeToggle() {
        toggleModeButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Switch to Scientific
                simplePanel.visibility = View.GONE
                scientificPanel.visibility = View.VISIBLE
                showToast("Scientific Mode Activated")
            } else {
                // Switch to Simple
                simplePanel.visibility = View.VISIBLE
                scientificPanel.visibility = View.GONE
                showToast("Simple Mode Activated")
            }
        }
    }

    private fun setupNumberButtons(view: View) {
        val buttons = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDot
        )
        buttons.forEach { id ->
            view.findViewById<Button>(id).setOnClickListener {
                val buttonText = (it as Button).text.toString()
                handleNumberInput(buttonText)
            }
        }
    }

    private fun handleNumberInput(input: String) {
        if (input == "." && currentInput.contains(".")) return

        currentInput += input
        display.setText(currentInput)
    }

    private fun setupOperationButtons(view: View) {
        val operationButtons = listOf(
            R.id.btnAdd, R.id.btnSubtract,
            R.id.btnMultiply, R.id.btnDivide
        )
        operationButtons.forEach { id ->
            view.findViewById<Button>(id).setOnClickListener {
                val op = (it as Button).text.toString()
                performOperation(op)
            }
        }

        view.findViewById<Button>(R.id.btnEquals).setOnClickListener { calculateResult() }
        view.findViewById<Button>(R.id.btnClear).setOnClickListener { clearCalculator() }
    }

    private fun setupScientificButtons(view: View) {
        val scientificOperations = mapOf(
            R.id.btnSin to "sin",
            R.id.btnCos to "cos",
            R.id.btnTan to "tan",
            R.id.btnLog to "log",
            R.id.btnSqrt to "√",
            R.id.btnPower to "x²",
            R.id.btnFactorial to "!"
        )

        scientificOperations.forEach { (id, operation) ->
            view.findViewById<Button>(id).setOnClickListener {
                performScientificOperation(operation)
            }
        }

        view.findViewById<Button>(R.id.btnPi).setOnClickListener {
            currentInput += PI.toString()
            display.setText(currentInput)
        }
    }

    private fun performOperation(op: String) {
        if (currentInput.isNotEmpty()) {
            firstValue = currentInput.toDouble()
            currentInput = ""
            operator = op
        }
    }

    private fun calculateResult() {
        if (currentInput.isNotEmpty() && operator.isNotEmpty()) {
            val secondValue = currentInput.toDouble()
            val result = when (operator) {
                "+" -> firstValue + secondValue
                "-" -> firstValue - secondValue
                "*" -> firstValue * secondValue
                "/" -> {
                    if (secondValue == 0.0) {
                        showErrorDialog("Division by zero is not allowed")
                        return
                    }
                    firstValue / secondValue
                }

                else -> 0.0
            }

            val calculationString = "$firstValue $operator $secondValue = $result"
            calculationHistoryList.add(calculationString)
            updateCalculationHistory()

            display.setText(result.toString())
            currentInput = result.toString()
            operator = ""
            showToast("Calculation Complete")
        }
    }

    private fun updateCalculationHistory() {
        val displayText = calculationHistoryList.takeLast(5).joinToString("\n")
        calculationHistory.text = displayText
    }

    private fun performScientificOperation(op: String) {
        if (currentInput.isNotEmpty()) {
            val value = currentInput.toDouble()
            val result = when (op) {
                "sin" -> sin(Math.toRadians(value))
                "cos" -> cos(Math.toRadians(value))
                "tan" -> tan(Math.toRadians(value))
                "log" -> log10(value)
                "√" -> sqrt(value)
                "x²" -> value.pow(2)
                "!" -> factorial(value.toInt())
                else -> value
            }

            val calculationString = "$op($value) = $result"
            calculationHistoryList.add(calculationString)
            updateCalculationHistory()

            display.setText(result.toString())
            currentInput = result.toString()
            showToast("Scientific Operation Complete")
        }
    }

    private fun clearCalculator() {
        currentInput = ""
        operator = ""
        firstValue = 0.0
        display.setText("")
        calculationHistoryList.clear()
        calculationHistory.text = ""
        showToast("Calculator Reset")
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun factorial(n: Int): Double {
        return if (n <= 1) 1.0 else n * factorial(n - 1)
    }
}