package com.lasockiquenon.coopertest.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.text.TextUtils.split
import androidx.preference.PreferenceManager
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.Expose
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Results(
    meters: Double, routePoints: List<Location>, avgSpeed: Double,
    context: Context, private var athlete: Boolean = false
) {

    @Expose
    private var meters: Double = 0.0
    private var age: Int = 0
    @Expose
    private var name: String = ""
    private var gender: String = ""
    @Expose
    private var level: String = ""
    @Expose
    private var previousStep: Int = 0
    @Expose
    private var nextStep: Int = 0
    private var routePoints: List<Location> = emptyList()
    private var routePointsLatLong: List<LatLng> = emptyList()
    @Expose
    private var routePointsString: String = ""
    @Expose
    private var date: Date = Date()
    @Expose
    private var avgSpeed: Double = 0.0

    init {
        val mSharedPreference = PreferenceManager.getDefaultSharedPreferences(context)
        val dateBirthday = mSharedPreference.getString("birthday", "null")
        name = mSharedPreference.getString("name", "null").toString()
        age = getAge(dateBirthday.toString())
        this.meters = meters
        athlete = mSharedPreference.getBoolean("athlete", false)
        gender = mSharedPreference.getString("gender", "Male")!!
        setResultTest()
        this.routePoints = routePoints
        convertLocationAndString()
        this.avgSpeed = avgSpeed
    }

    @SuppressLint("SimpleDateFormat")
    private fun getAge(dobString: String): Int {

        var date: Date? = null
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        try {
            date = sdf.parse(dobString)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        if (date == null) return 0

        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()

        dob.time = date

        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        return age
    }


    fun getLevel(): String {
        return level
    }

    fun getRange(context: Context): String {
        val range: String = when {
            nextStep == 0 -> {
                " ( >" + formatMeters(previousStep, context) + ")"
            }
            previousStep == 0 -> {
                " ( <" + formatMeters(nextStep, context) + ")"
            }
            else -> {
                " (" + formatMeters(nextStep, context) + "-" + formatMeters(nextStep, context) + ")"
            }
        }
        return range
    }

    fun getMeters(): Double {
        return meters
    }

    fun getDate(): Date {
        return date
    }

    fun getAvgSpeed(): Double {
        return avgSpeed
    }

    fun getRoutePointsLatLong(): List<LatLng> {
        return routePointsLatLong
    }

    fun getName(): String {
        return name
    }

    private fun formatMeters(meters: Int, context: Context): String {
        val mSharedPreference = PreferenceManager.getDefaultSharedPreferences(context)
        val miles = mSharedPreference.getBoolean("miles", false)
        return if (!miles) {
            "%.0fm".format(meters.toFloat())
        } else {
            "%.0fyd".format(meters * 1.09361)
        }
    }

    internal fun convertLocationAndString() {
        if (routePointsString == "") {
            for (points in routePoints) {
                routePointsString += points.latitude.toString() + "/" + points.longitude.toString() + "!"
            }
        } else if (routePointsLatLong.isNullOrEmpty()) {
            routePointsLatLong = emptyList()
            val points = split(routePointsString, "!")
            for (point in points) {
                if (point.contains("/")) {
                    val pointSplited = split(point, "/")
                    routePointsLatLong = routePointsLatLong + LatLng(
                        pointSplited[0].toDouble(),
                        pointSplited[1].toDouble()
                    )
                }
            }
        }
    }

    private fun setResultTest() {
        if (athlete) {
            if (gender == "Male") {
                when {
                    meters < 2800 -> {
                        level = "Very bad"
                        previousStep = 0
                        nextStep = 2800
                    }
                    meters < 3100 -> {
                        previousStep = 2800
                        nextStep = 3100
                        level = "Bad"
                    }
                    meters < 3400 -> {
                        level = "Average"
                        previousStep = 3100
                        nextStep = 3400
                    }
                    meters < 3700 -> {
                        level = "Good"
                        previousStep = 3400
                        nextStep = 3700
                    }
                    meters >= 3700 -> {
                        level = "Very good"
                        previousStep = 3700
                        nextStep = 0
                    }
                }
            } else {
                when {
                    meters < 2100 -> {
                        level = "Very bad"
                        previousStep = 0
                        nextStep = 2100
                    }
                    meters < 2400 -> {
                        level = "Bad"
                        previousStep = 2100
                        nextStep = 2400
                    }
                    meters < 2700 -> {
                        level = "Average"
                        previousStep = 2400
                        nextStep = 2700
                    }
                    meters < 3000 -> {
                        level = "Good"
                        previousStep = 2700
                        nextStep = 3000
                    }
                    meters >= 3000 -> {
                        level = "Very good"
                        previousStep = 3000
                        nextStep = 0
                    }
                }

            }
        } else {
            when {
                age < 15 -> {
                    if (gender == "Male") {
                        when {
                            meters < 2100 -> {
                                level = "Very bad"
                                previousStep = 0
                                nextStep = 2100
                            }
                            meters < 2200 -> {
                                level = "Bad"
                                previousStep = 2100
                                nextStep = 2200
                            }
                            meters < 2400 -> {
                                level = "Average"
                                previousStep = 2200
                                nextStep = 2400
                            }
                            meters < 2700 -> {
                                level = "Good"
                                previousStep = 2400
                                nextStep = 2700
                            }
                            meters >= 2700 -> {
                                level = "Very good"
                                previousStep = 2700
                                nextStep = 0
                            }
                        }
                    } else {
                        when {
                            meters < 1500 -> {
                                level = "Very bad"
                                previousStep = 0
                                nextStep = 1500
                            }
                            meters < 1600 -> {
                                level = "Bad"
                                previousStep = 1500
                                nextStep = 1600
                            }
                            meters < 1900 -> {
                                level = "Average"
                                previousStep = 1600
                                nextStep = 1900
                            }
                            meters < 2000 -> {
                                level = "Good"
                                previousStep = 1900
                                nextStep = 2000
                            }
                            meters >= 2000 -> {
                                level = "Very good"
                                previousStep = 2000
                                nextStep = 0
                            }
                        }
                    }
                }
                age < 17 -> {
                    if (gender == "Male") {
                        when {
                            meters < 2200 -> {
                                level = "Very bad"
                                previousStep = 0
                                nextStep = 2200
                            }
                            meters < 2300 -> {
                                level = "Bad"
                                previousStep = 2200
                                nextStep = 2300
                            }
                            meters < 2500 -> {
                                level = "Average"
                                previousStep = 2300
                                nextStep = 2500
                            }
                            meters < 2800 -> {
                                level = "Good"
                                previousStep = 2500
                                nextStep = 2800
                            }
                            meters >= 2800 -> {
                                level = "Very good"
                                previousStep = 2800
                                nextStep = 0
                            }
                        }
                    } else {
                        when {
                            meters < 1600 -> {
                                level = "Very bad"
                                previousStep = 0
                                nextStep = 1600
                            }
                            meters < 1700 -> {
                                level = "Bad"
                                previousStep = 1600
                                nextStep = 1700
                            }
                            meters < 2000 -> {
                                level = "Average"
                                previousStep = 1700
                                nextStep = 2000
                            }
                            meters < 2100 -> {
                                level = "Good"
                                previousStep = 2000
                                nextStep = 2100
                            }
                            meters >= 2100 -> {
                                level = "Very good"
                                previousStep = 2100
                                nextStep = 0
                            }
                        }
                    }
                }
                age < 20 -> {
                    if (gender == "Male") {
                        when {
                            meters < 2300 -> {
                                level = "Very bad"
                                previousStep = 0
                                nextStep = 2300
                            }
                            meters < 2500 -> {
                                level = "Bad"
                                previousStep = 2300
                                nextStep = 2500
                            }
                            meters < 2700 -> {
                                level = "Average"
                                previousStep = 2500
                                nextStep = 2700
                            }
                            meters < 3000 -> {
                                level = "Good"
                                previousStep = 2700
                                nextStep = 3000
                            }
                            meters >= 3000 -> {
                                level = "Very good"
                                previousStep = 3000
                                nextStep = 0
                            }
                        }
                    } else {
                        when {
                            meters < 1700 -> {
                                level = "Very bad"
                                previousStep = 0
                                nextStep = 1700
                            }
                            meters < 1800 -> {
                                level = "Bad"
                                previousStep = 1700
                                nextStep = 1800
                            }
                            meters < 2100 -> {
                                level = "Average"
                                previousStep = 1800
                                nextStep = 2100
                            }
                            meters < 2300 -> {
                                level = "Good"
                                previousStep = 2100
                                nextStep = 2300
                            }
                            meters >= 2300 -> {
                                level = "Very good"
                                previousStep = 2300
                                nextStep = 0
                            }
                        }
                    }
                }
                age < 30 -> {
                    if (gender == "Male") {
                        when {
                            meters < 1600 -> {
                                level = "Very bad"
                                previousStep = 0
                                nextStep = 1600
                            }
                            meters < 2200 -> {
                                level = "Bad"
                                previousStep = 1600
                                nextStep = 2200
                            }
                            meters < 2400 -> {
                                level = "Average"
                                previousStep = 2200
                                nextStep = 2400
                            }
                            meters < 2800 -> {
                                level = "Good"
                                previousStep = 2400
                                nextStep = 2800
                            }
                            meters >= 2800 -> {
                                level = "Very good"
                                previousStep = 2800
                                nextStep = 0
                            }
                        }
                    } else {
                        when {
                            meters < 1500 -> {
                                level = "Very bad"
                                previousStep = 0
                                nextStep = 1500
                            }
                            meters < 1800 -> {
                                level = "Bad"
                                previousStep = 1500
                                nextStep = 1800
                            }
                            meters < 2200 -> {
                                level = "Average"
                                previousStep = 1800
                                nextStep = 2200
                            }
                            meters < 2700 -> {
                                level = "Good"
                                previousStep = 2200
                                nextStep = 2700
                            }
                            meters >= 2700 -> {
                                level = "Very good"
                                previousStep = 2700
                                nextStep = 0
                            }
                        }
                    }
                }
                age < 40 -> {
                    if (gender == "Male") {
                        when {
                            meters < 1500 -> {
                                level = "Very bad"
                                previousStep = 0
                                nextStep = 1500
                            }
                            meters < 1900 -> {
                                level = "Bad"
                                previousStep = 1500
                                nextStep = 1900
                            }
                            meters < 2300 -> {
                                level = "Average"
                                previousStep = 1900
                                nextStep = 2300
                            }
                            meters < 2700 -> {
                                level = "Good"
                                previousStep = 2300
                                nextStep = 2700
                            }
                            meters >= 2700 -> {
                                level = "Very good"
                                previousStep = 2700
                                nextStep = 0
                            }
                        }
                    } else {
                        when {
                            meters < 1400 -> {
                                level = "Very bad"
                                previousStep = 0
                                nextStep = 1400
                            }
                            meters < 1700 -> {
                                level = "Bad"
                                previousStep = 1400
                                nextStep = 1700
                            }
                            meters < 2000 -> {
                                level = "Average"
                                previousStep = 1700
                                nextStep = 2000
                            }
                            meters < 2500 -> {
                                level = "Good"
                                previousStep = 2000
                                nextStep = 2500
                            }
                            meters >= 2500 -> {
                                level = "Very good"
                                previousStep = 2500
                                nextStep = 0
                            }
                        }
                    }
                }
                age < 50 -> {
                    if (gender == "Male") {
                        when {
                            meters < 1400 -> {
                                level = "Very bad"
                                previousStep = 0
                                nextStep = 1400
                            }
                            meters < 1700 -> {
                                level = "Bad"
                                previousStep = 1400
                                nextStep = 1700
                            }
                            meters < 2100 -> {
                                level = "Average"
                                previousStep = 1700
                                nextStep = 2100
                            }
                            meters < 2500 -> {
                                level = "Good"
                                previousStep = 2100
                                nextStep = 2500
                            }
                            meters >= 2500 -> {
                                level = "Very good"
                                previousStep = 2500
                                nextStep = 0
                            }
                        }
                    } else {
                        when {
                            meters < 1200 -> {
                                level = "Very bad"
                                previousStep = 0
                                nextStep = 1200
                            }
                            meters < 1500 -> {
                                level = "Bad"
                                previousStep = 1200
                                nextStep = 1500
                            }
                            meters < 1900 -> {
                                level = "Average"
                                previousStep = 1500
                                nextStep = 1900
                            }
                            meters < 2300 -> {
                                level = "Good"
                                previousStep = 1900
                                nextStep = 2300
                            }
                            meters >= 2300 -> {
                                level = "Very good"
                                previousStep = 2300
                                nextStep = 0
                            }
                        }
                    }
                }
                age > 50 -> {
                    if (gender == "Male") {
                        when {
                            meters < 1300 -> {
                                level = "Very bad"
                            }
                            meters < 1600 -> {
                                level = "Bad"
                                previousStep = 1300
                                nextStep = 1600
                            }
                            meters < 2000 -> {
                                level = "Average"
                                previousStep = 1600
                                nextStep = 2000
                            }
                            meters < 2400 -> {
                                level = "Good"
                                previousStep = 2000
                                nextStep = 2400
                            }
                            meters >= 2400 -> {
                                level = "Very good"
                                previousStep = 2400
                                nextStep = 0
                            }
                        }
                    } else {
                        when {
                            meters < 1100 -> {
                                level = "Very bad"
                                previousStep = 0
                                nextStep = 1100
                            }
                            meters < 1400 -> {
                                level = "Bad"
                                previousStep = 1100
                                nextStep = 1400
                            }
                            meters < 1700 -> {
                                level = "Average"
                                previousStep = 1400
                                nextStep = 1700
                            }
                            meters < 2200 -> {
                                level = "Good"
                                previousStep = 1700
                                nextStep = 2200
                            }
                            meters >= 2200 -> {
                                level = "Very good"
                                previousStep = 2200
                                nextStep = 0
                            }
                        }
                    }
                }
            }

        }
    }
}