package com.lasockiquenon.coopertest

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuItem
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.view.setPadding
import com.google.android.material.tabs.TabLayout
import com.lasockiquenon.coopertest.utils.Results
import com.lasockiquenon.coopertest.utils.UnitsUtils
import com.lasockiquenon.coopertest.utils.Storage
import java.util.*


class ResultActivity : BaseThemedActivity() {
    private val unitsUtils = UnitsUtils(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Results"
        val listOfResults = Storage().loadResults(this)
        fillTable(listOfResults)
    }

    fun fillTable(listOfResults: ArrayList<Results>?) {
        val table = findViewById<TableLayout>(R.id.tableResults)
        var placeInList = 0
        if (listOfResults != null) {
            for (i in listOfResults) {

                val newRow = TableRow(this)
                val myPlaceInList= placeInList
                val tableViewLayout = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f)

                var textview = newTextView()
                val stringDate = android.text.format.DateFormat.format("yyyy-MM-dd", i.getDate())
                textview.text = stringDate
                textview.gravity = Gravity.CENTER_HORIZONTAL
                textview.setPadding(18)
                textview.textSize=18f
                newRow.addView(textview, tableViewLayout)

                textview = newTextView()
                textview.text = unitsUtils.formatDistance(i.getMeters())
                textview.gravity = Gravity.CENTER_HORIZONTAL
                textview.textSize=18f
                newRow.addView(textview, tableViewLayout)

                textview = newTextView()
                textview.text = i.getLevel()
                textview.gravity = Gravity.CENTER_HORIZONTAL
                textview.textSize=18f
                newRow.addView(textview, tableViewLayout)

                newRow.isClickable = true
                newRow.setOnClickListener{
                    val intent = Intent(this, TestActivity::class.java)
                    intent.putExtra("Results", myPlaceInList)
                    startActivity(intent)
                }



                table.addView(newRow, TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT, 1f))
                placeInList += 1
            }
        }
    }

    private fun newTextView(): TextView {
        val textView = TextView(this)
        val typedValue = TypedValue()
        theme.resolveAttribute(R.attr.primaryTextColor, typedValue, true)
        @ColorInt val color = typedValue.data
        textView.setTextColor(color)
        return textView
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
