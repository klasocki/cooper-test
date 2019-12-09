package com.lasockiquenon.coopertest

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

@SuppressLint("Registered")
open class BaseThemedActivity : AppCompatActivity() {
    private var darkThemeOn = true
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        darkThemeOn = sharedPref.getBoolean("dark_theme", true)
        setAppTheme(darkThemeOn)
    }

    override fun onResume() {
        super.onResume()
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val newTheme = sharedPref.getBoolean("dark_theme", true)
        if (newTheme != darkThemeOn)
            recreate()
    }
    private fun setAppTheme(darkThemeOn: Boolean) {
        if (darkThemeOn)
            setTheme(R.style.DarkTheme)
        else
            setTheme(R.style.LightTheme)
    }
}