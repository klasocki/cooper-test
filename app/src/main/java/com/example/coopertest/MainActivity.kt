package com.example.coopertest

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


    }

    fun startTest(v: View) {
        val intent = Intent(this, TestActivity::class.java)
        startActivity(intent)
    }

    fun settings(v: View) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }


}
