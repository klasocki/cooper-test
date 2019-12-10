package com.lasockiquenon.coopertest

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

@SuppressLint("Registered")
open class BaseThemedActivity : AppCompatActivity() {
    private var isSystemThemeDark = true
    private var darkThemeOn = isSystemThemeDark
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        darkThemeOn = getThemeFromSettings()
        setAppTheme(darkThemeOn)
    }

    override fun onResume() {
        super.onResume()
        val newTheme = getThemeFromSettings()
        if (newTheme != darkThemeOn) {
            recreate()
            darkThemeOn = newTheme
        }
    }

    private fun setAppTheme(darkThemeOn: Boolean) {
        if (darkThemeOn)
            setTheme(R.style.DarkTheme)
        else
            setTheme(R.style.LightTheme)
    }

    private fun getThemeFromSettings(): Boolean {
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> isSystemThemeDark = true
            Configuration.UI_MODE_NIGHT_NO -> isSystemThemeDark = false
        }

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        return sharedPref.getBoolean("dark_theme", isSystemThemeDark)
    }
}