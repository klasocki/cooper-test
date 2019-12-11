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
    protected var isDarkThemeOn = isSystemThemeDark
    private lateinit var sharedPref: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isDarkThemeOn = getThemeFromSettings()
        setAppTheme(isDarkThemeOn)
    }

    override fun onResume() {
        super.onResume()
        val newTheme = getThemeFromSettings()
        if (newTheme != isDarkThemeOn) {
            recreate()
            isDarkThemeOn = newTheme
        }
    }

    private fun setAppTheme(darkThemeOn: Boolean) {
        setTheme(if (darkThemeOn) R.style.DarkTheme else R.style.LightTheme)
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