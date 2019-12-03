package com.example.coopertest

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startTest(v: View) {
        if (LocationHandler.checkPermissions(this) && LocationHandler.isLocationEnabled(this)) {
            val intent = Intent(this, TestActivity::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, LocationActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }

}
