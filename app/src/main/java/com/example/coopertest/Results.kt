package com.example.coopertest

import android.content.Context
import android.location.Location
import androidx.preference.PreferenceManager
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.Expose
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Results (meters : Double, routePoints:List<Location>, avgSpeed: Double, context: Context)    {

    @Expose
    private var meters : Double=0.0
    private var age: Int=0
    private var athlete: Boolean = false
    private var gender : String = ""
    private val context:Context=context
    @Expose
    private var level: String = ""
    private var previousStep=null
    private var nextStep=null
    @Expose
    private var routePoints: List<Location> = emptyList()
    @Expose
    private var test : List<LatLng> = emptyList()
    @Expose
    private var date: Date = Date()
    @Expose
    private var averageSpeed = 0.0


    init {
        val mSharedPreference = PreferenceManager.getDefaultSharedPreferences(context)
        val dateBirthday = mSharedPreference.getString("birthday", "null")
        age=getAge(dateBirthday.toString())
        this.meters=meters
        athlete = mSharedPreference.getBoolean("athlete", false)
        gender = mSharedPreference.getString("gender", "Male")!!
        level=setResultTest()
        this.routePoints=routePoints
        this.test+=LatLng(routePoints.get(0).latitude,routePoints.get(0).longitude)
        this.averageSpeed= avgSpeed
    }

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

        dob.setTime(date)

        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        return age
    }

    fun setResultTest(): String {
        var yourResult : String =""
        if (athlete==true) {
            if ((meters < 2800 && gender == "Male") || (meters < 2100 && gender == "Female")) {
                yourResult = "Very bad"
            } else if ((meters < 3100 && gender == "Male") || (meters < 2400 && gender == "Female")) {
                yourResult ="Bad"
            } else if ((meters < 3400 && gender == "Male") || (meters < 2700 && gender == "Female")) {
                yourResult = "Average"
            } else if ((meters < 3700 && gender == "Male") || (meters < 3000 && gender == "Female")) {
                yourResult = "Good"
            } else if ((meters >= 3700 && gender == "Male") || (meters >= 3000 && gender == "Female")) {
                yourResult = "Very good"
            }
        } else {
            if (age<15) {
                if ((meters < 2100 && gender == "Male") || (meters < 1500 && gender == "Female")) {
                    yourResult = "Very bad"
                } else if ((meters < 2200 && gender == "Male") || (meters < 1600 && gender == "Female")) {
                    yourResult = "Bad"
                } else if ((meters < 2400 && gender == "Male") || (meters < 1900 && gender == "Female")) {
                    yourResult = "Average"
                } else if ((meters < 2700 && gender == "Male") || (meters < 2000 && gender == "Female")) {
                    yourResult = "Good"
                } else if ((meters >= 2700 && gender == "Male") || (meters >= 2000 && gender == "Female")) {
                    yourResult = "Very good"
                }
            } else if (age<17){
                if ((meters < 2200 && gender == "Male") || (meters < 1600 && gender == "Female")) {
                    yourResult = "Very bad"
                } else if ((meters < 2300 && gender == "Male") || (meters < 1700 && gender == "Female")) {
                    yourResult = "Bad"
                } else if ((meters < 2500 && gender == "Male") || (meters < 2000 && gender == "Female")) {
                    yourResult = "Average"
                } else if ((meters < 2800 && gender == "Male") || (meters < 2100 && gender == "Female")) {
                    yourResult = "Good"
                } else if ((meters >= 2800 && gender == "Male") || (meters >= 2100 && gender == "Female")) {
                    yourResult = "Very good"
                }
            } else if (age<20){
                if ((meters < 2300 && gender == "Male") || (meters < 1700 && gender == "Female")) {
                    yourResult = "Very bad"
                } else if ((meters < 2500 && gender == "Male") || (meters < 1800 && gender == "Female")) {
                    yourResult = "Bad"
                } else if ((meters < 2700 && gender == "Male") || (meters < 2100 && gender == "Female")) {
                    yourResult = "Average"
                } else if ((meters < 3000 && gender == "Male") || (meters < 2300 && gender == "Female")) {
                    yourResult = "Good"
                } else if ((meters >= 3000 && gender == "Male") || (meters >= 2300 && gender == "Female")) {
                    yourResult = "Very good"
                }
            } else if (age<30){
                if ((meters < 1600 && gender == "Male") || (meters < 1500 && gender == "Female")) {
                    yourResult = "Very bad"
                } else if ((meters < 2200 && gender == "Male") || (meters < 1800 && gender == "Female")) {
                    yourResult = "Bad"
                } else if ((meters < 2400 && gender == "Male") || (meters < 2200 && gender == "Female")) {
                    yourResult = "Average"
                } else if ((meters < 2800 && gender == "Male") || (meters < 2700 && gender == "Female")) {
                    yourResult = "Good"
                } else if ((meters >= 2800 && gender == "Male") || (meters >= 2700 && gender == "Female")) {
                    yourResult = "Very good"
                }
            } else if (age<40){
                if ((meters < 1500 && gender == "Male") || (meters < 1400 && gender == "Female")) {
                    yourResult = "Very bad"
                } else if ((meters < 1900 && gender == "Male") || (meters < 1700 && gender == "Female")) {
                    yourResult = "Bad"
                } else if ((meters < 2300 && gender == "Male") || (meters < 2000 && gender == "Female")) {
                    yourResult = "Average"
                } else if ((meters < 2700 && gender == "Male") || (meters < 2500 && gender == "Female")) {
                    yourResult = "Good"
                } else if ((meters >= 2700 && gender == "Male") || (meters >= 2500 && gender == "Female")) {
                    yourResult = "Very good"
                }
            } else if (age<50){
                if ((meters < 1400 && gender == "Male") || (meters < 1200 && gender == "Female")) {
                    yourResult = "Very bad"
                } else if ((meters < 1700 && gender == "Male") || (meters < 1500 && gender == "Female")) {
                    yourResult = "Bad"
                } else if ((meters < 2100 && gender == "Male") || (meters < 1900 && gender == "Female")) {
                    yourResult = "Average"
                } else if ((meters < 2500 && gender == "Male") || (meters < 2300 && gender == "Female")) {
                    yourResult = "Good"
                } else if ((meters >= 2500 && gender == "Male") || (meters >= 2300 && gender == "Female")) {
                    yourResult = "Very good"
                }
            } else if (age>50){
                if ((meters < 1300 && gender == "Male") || (meters < 1100 && gender == "Female")) {
                    yourResult = "Very bad"
                } else if ((meters < 1600 && gender == "Male") || (meters < 1400 && gender == "Female")) {
                    yourResult = "Bad"
                } else if ((meters < 2000 && gender == "Male") || (meters < 1700 && gender == "Female")) {
                    yourResult = "Average"
                } else if ((meters < 2400 && gender == "Male") || (meters < 2200 && gender == "Female")) {
                    yourResult = "Good"
                } else if ((meters >= 2400 && gender == "Male") || (meters >= 2200 && gender == "Female")) {
                    yourResult = "Very good"
                }
            }

        }
        return yourResult
    }

    fun getLevel(): String{
        return level
    }

    fun getMeters() : Double {
        return meters
    }

    fun getDate(): Date {
        return date
    }

    fun getAvgSpeed(): Double {
        return averageSpeed
    }

    fun getRoutePoints(): List<Location>{
        return routePoints
    }


}