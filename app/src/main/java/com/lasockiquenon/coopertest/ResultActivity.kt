package com.lasockiquenon.coopertest

import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuItem
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.preference.PreferenceManager
import com.lasockiquenon.coopertest.utils.Results
import com.lasockiquenon.coopertest.utils.Storage
import java.util.*


class ResultActivity : BaseThemedActivity() {

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
                //val myPlaceInList= placeInList

                var textview = newTextView()
                val stringDate = android.text.format.DateFormat.format("dd-MM-yyyy", i.getDate())
                textview.text = stringDate
                textview.gravity = Gravity.CENTER_HORIZONTAL
                newRow.addView(textview)

                textview = newTextView()
                textview.text = formatSpeed(i.getAvgSpeed())
                textview.gravity = Gravity.CENTER_HORIZONTAL
                newRow.addView(textview)

                textview = newTextView()
                textview.text = formatDistance(i.getMeters())
                textview.gravity = Gravity.CENTER_HORIZONTAL
                newRow.addView(textview)

                textview = newTextView()
                textview.text = i.getLevel().split("(")[0]
                textview.gravity = Gravity.CENTER_HORIZONTAL
                newRow.addView(textview)


                /*newRow.setClickable(true)
                newRow.setOnClickListener{
                    val intent = Intent(this, TestActivity::class.java)
                    intent.putExtra("Results", myPlaceInList)
                    startActivity(intent)
                }*/



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

    private fun formatDistance(meter: Double): String {
        val mSharedPreference =
            PreferenceManager.getDefaultSharedPreferences(this)
        val miles = mSharedPreference.getBoolean("miles", false)
        return if (!miles) {
            "%.0fm".format(meter)
        } else {
            "%.0fyd".format(meter * 1.094)
        }
    }

    private fun formatSpeed(speed: Double): String {
        val mSharedPreference =
            PreferenceManager.getDefaultSharedPreferences(this)
        val miles = mSharedPreference.getBoolean("miles", false)
        return if (!miles) {
            "%.1fkm/h".format(speed * 3.6)
        } else {
            "%.1fmph".format(speed * 2.23694)
        }
    }


}
