package com.lasockiquenon.coopertest.utils

import android.content.Context
import android.location.Location
import androidx.preference.PreferenceManager

class UnitsUtils(val context: Context) {
    fun formatDistance(distanceMeters: Double): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val useImperial = sharedPreferences.getBoolean("use_imperial", false)
        return if (!useImperial) {
            "%.0fm".format(distanceMeters)
        } else {
            "%.0fyd".format(distanceMeters * 1.09361)
        }
    }


    fun formatSpeed(speedMetersPerSecond: Float): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val useImperial = sharedPreferences.getBoolean("use_imperial", false)
        return if (!useImperial) {
            "%.1f km/h".format(speedMetersPerSecond * 3.6)
        } else {
            "%.1f mph".format(speedMetersPerSecond * 2.23694)
        }
    }

    fun calculateSpeed(start: Location, end: Location): Float {
        return (end.distanceTo(start) * 1000 / (end.time - start.time))
    }
}