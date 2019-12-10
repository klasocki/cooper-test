package com.lasockiquenon.coopertest

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuItem
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.annotation.ColorInt
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

                var textview = newTextView()
                val stringDate = android.text.format.DateFormat.format("yyyy-MM-dd", i.getDate())
                textview.text = stringDate
                textview.gravity = Gravity.CENTER_HORIZONTAL
                newRow.addView(textview)

                textview = newTextView()
                textview.text = unitsUtils.formatSpeed(i.getAvgSpeed().toFloat())
                textview.gravity = Gravity.CENTER_HORIZONTAL
                newRow.addView(textview)

                textview = newTextView()
                textview.text = unitsUtils.formatDistance(i.getMeters())
                textview.gravity = Gravity.CENTER_HORIZONTAL
                newRow.addView(textview)

                textview = newTextView()
                textview.text = i.getLevel()
                textview.gravity = Gravity.CENTER_HORIZONTAL
                newRow.addView(textview)

                newRow.setClickable(true)
                newRow.setOnClickListener{
                    val intent = Intent(this, TestActivity::class.java)
                    intent.putExtra("Results", myPlaceInList)
                    startActivity(intent)
                }



                table.addView(newRow)
                placeInList += 1
            }
        }
    }

    private fun newTextView(): TextView {
        val textview = TextView(this)
        val typedValue = TypedValue()
        theme.resolveAttribute(R.attr.primaryTextColor, typedValue, true)
        @ColorInt val color = typedValue.data
        textview.setTextColor(color)
        return textview
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
}
