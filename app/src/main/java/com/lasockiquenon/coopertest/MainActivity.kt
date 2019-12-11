package com.lasockiquenon.coopertest

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.lasockiquenon.coopertest.utils.LocationHandler


class MainActivity : BaseThemedActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startTest(v: View) = when {
        !(LocationHandler.checkPermissions(this) && LocationHandler.isLocationEnabled(this)) -> {
            val intent = Intent(this, LocationActivity::class.java)
            startActivity(intent)
        }
        else -> {
            val intent = Intent(this, TestActivity::class.java)
            startActivity(intent)
        }
    }


    fun goToSettings(v: View) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    fun goToInfo(v: View) {
        val intent = Intent(this, InfoActivity::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        finishAffinity()
    }

    fun goToResults(v: View) {
        val intent = Intent(this, ResultActivity::class.java)
        startActivity(intent)
    }




}
