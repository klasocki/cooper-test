package com.example.coopertest

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import java.util.ArrayList
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.preference.PreferenceManager


class ResultActivity : AppCompatActivity() {

    private val objectContext: App = App(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("Results")
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
        if(Build.VERSION.SDK_INT >= 23){
            textview.setTextColor(getColor(R.color.colorPrimary))
        } else {
            textview.setTextColor(resources.getColor(R.color.colorPrimary))
        }
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
            PreferenceManager.getDefaultSharedPreferences(objectContext.getContext())
        val miles = mSharedPreference.getBoolean("miles", false)
        return if (!miles) {
            "%.0f m".format(meter)
        } else {
            "%.0f yd".format(meter * 1.094)
        }
    }

    private fun formatSpeed(speed: Double): String {
        val mSharedPreference =
            PreferenceManager.getDefaultSharedPreferences(objectContext.getContext())
        val miles = mSharedPreference.getBoolean("miles", false)
        return if (!miles) {
            "%.1f km/h".format(speed * 3.6)
        } else {
            "%.1f mph".format(speed * 2.23694)
        }
    }


}
