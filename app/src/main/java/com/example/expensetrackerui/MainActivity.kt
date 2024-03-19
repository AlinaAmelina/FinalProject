package com.example.expensetrackerui

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.fragment.app.DialogFragment;

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val balanceText: TextView = findViewById(R.id.balanceText)
        val historyList: LinearLayout = findViewById(R.id.historyList)
        val amountInput: EditText = findViewById(R.id.amountInput)
        val addIncomeBtn: Button = findViewById(R.id.addIncomeBtn)
        val addExpenseBnt: Button = findViewById(R.id.addExpenseBnt)
        val undoBtn: Button = findViewById(R.id.undoBtn)

        var lastChange = 0
        var history = mutableListOf<Map<String, Any>>()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        addIncomeBtn.setOnClickListener {
            if (amountInput.text.toString() == "") {
                return@setOnClickListener
            }

            val amount: Int = amountInput.text.toString().toInt()
            val balance: Int = balanceText.text.toString().toInt()

            amountInput.setText("")

            lastChange = amount
            val lastHistoryChange = mutableMapOf("change" to lastChange,
                "dateTime" to LocalDateTime.now().format(formatter))
            history = (listOf(lastHistoryChange) + history).toMutableList()
            redrawHistory(history, historyList)

            val newBalance = balance + amount
            balanceText.text = newBalance.toString()
        }
        addExpenseBnt.setOnClickListener {
            if (amountInput.text.toString() == "") {
                return@setOnClickListener
            }

            val amount: Int = amountInput.text.toString().toInt()
            val balance: Int = balanceText.text.toString().toInt()

            amountInput.setText("")

            lastChange = -amount
            val lastHistoryChange = mutableMapOf("change" to lastChange,
                "dateTime" to LocalDateTime.now().format(formatter))
            history = (listOf(lastHistoryChange) + history).toMutableList()
            redrawHistory(history, historyList)

            val newBalance = balance - amount
            balanceText.text = newBalance.toString()
        }
        undoBtn.setOnClickListener {
            if (lastChange != 0) {
                val balance: Int = balanceText.text.toString().toInt()

                val newBalance = balance - lastChange
                balanceText.text = newBalance.toString()
                lastChange = 0
                history.removeFirst()
                redrawHistory(history, historyList)
            } else {
                val myDialogFragment = MyDialogFragment()
                val manager = supportFragmentManager
                myDialogFragment.show(manager, "myDialog")
            }
        }
    }

    private fun redrawHistory(history: MutableList<Map<String, Any>>, historyList: LinearLayout) {
        historyList.removeViews(0, historyList.childCount)

        for (h in history) {
            val lastAmountChangeText = TextView(this)
            lastAmountChangeText.layoutParams = TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            lastAmountChangeText.text = h["change"].toString()

            val lastDateTimeChangeText = TextView(this)
            lastDateTimeChangeText.layoutParams = TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            lastDateTimeChangeText.text = h["dateTime"].toString()

            val lastHistoryChangeLayout = LinearLayout(this)
            lastHistoryChangeLayout.orientation = LinearLayout.HORIZONTAL
            lastHistoryChangeLayout.setPadding(15, 15, 15, 15)
            lastHistoryChangeLayout.addView(lastAmountChangeText)
            lastHistoryChangeLayout.addView(lastDateTimeChangeText)

            historyList.addView(lastHistoryChangeLayout)
        }
    }

    class MyDialogFragment : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return activity?.let {
                val builder = AlertDialog.Builder(it)
                builder.setTitle("Oops")
                    .setMessage("The action has already been undone or has not yet been committed")
                    .setPositiveButton("OK") {
                            dialog, _ ->  dialog.cancel()
                    }
                builder.create()
            } ?: throw IllegalStateException("Activity cannot be null")
        }
    }
}