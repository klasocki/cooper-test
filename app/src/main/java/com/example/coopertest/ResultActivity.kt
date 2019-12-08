package com.example.coopertest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import java.util.ArrayList
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.children
import androidx.preference.PreferenceManager


class ResultActivity : AppCompatActivity() {

    private val objectContext : App = App(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("Results")
        val listOfResults= Storage().loadResults(this)
        fillTable(listOfResults)
    }

    fun fillTable(listOfResults : ArrayList<Results>?){
        val table = findViewById<TableLayout>(R.id.tableResults)
        var placeInList=0
        if (listOfResults != null) {
            for (i in listOfResults) {

                val newRow = TableRow(this)
                //val myPlaceInList= placeInList

                var textview = TextView(this)
                val stringDate = android.text.format.DateFormat.format("dd-MM-yyyy", i.getDate())
                textview.setText(stringDate)
                textview.setGravity(Gravity.CENTER_HORIZONTAL)
                newRow.addView(textview)


                textview = TextView(this)
                textview.setText(formatSpeed(i.getAvgSpeed()))
                textview.setGravity(Gravity.CENTER_HORIZONTAL)
                newRow.addView(textview)

                textview = TextView(this)
                textview.setText(formatDistance(i.getMeters()))
                textview.setGravity(Gravity.CENTER_HORIZONTAL)
                newRow.addView(textview)

                textview = TextView(this)
                textview.setText(i.getLevel())
                textview.setGravity(Gravity.CENTER_HORIZONTAL)
                newRow.addView(textview)


                /*newRow.setClickable(true)
                newRow.setOnClickListener{
                    val intent = Intent(this, TestActivity::class.java)
                    intent.putExtra("Results", myPlaceInList)
                    startActivity(intent)
                }*/



                table.addView(newRow)
                placeInList+=1
            }
        }
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
        val mSharedPreference = PreferenceManager.getDefaultSharedPreferences(objectContext.getContext())
        val miles = mSharedPreference.getBoolean("miles", false)
        if (!miles) {
            return "%.0f m".format(meter)
        } else {
            return "%.0f yd".format(meter*1.094)
        }
    }

    private fun formatSpeed(speed: Double): String {
        val mSharedPreference = PreferenceManager.getDefaultSharedPreferences(objectContext.getContext())
        val miles = mSharedPreference.getBoolean("miles", false)
        if (!miles){
            return "%.1f km/h".format(speed*3.6)
        } else {
            return "%.1f mph".format(speed*2.23694)
        }
    }




}
