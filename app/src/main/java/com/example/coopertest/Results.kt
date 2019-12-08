package com.example.coopertest

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.preference.PreferenceManager
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.Expose
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Results (meters : Double, routePoints:List<Location>, avgSpeed : Double,
               private val context: Context, private var athlete: Boolean = false
)    {

    @Expose
    private var meters : Double=0.0
    private var age: Int=0
    private var gender : String = ""
    @Expose
    private var level: String = ""
    private var previousStep : Int?=null
    private var nextStep : Int? = null
    @Expose
    private var routePoints: List<Location> = emptyList()
    @Expose
    private var test : List<LatLng> = emptyList()
    @Expose
    private var date: Date = Date()
    @Expose
    private var avgSpeed: Double = 0.0

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
        this.avgSpeed=avgSpeed
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

        dob.setTime(date)

        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        return age
    }

    fun setResultTest(): String {
        if (athlete==true) {
            if (gender == "Male") {
                if (meters < 2800) {
                    level = "Very bad"
                    previousStep = 0
                    nextStep = 2800
                } else if (meters < 3100) {
                    previousStep = 2800
                    nextStep = 3100
                    level = "Bad"
                } else if (meters < 3400) {
                    level = "Average"
                    previousStep =3100
                    nextStep =3400
                } else if (meters < 3700) {
                    level = "Good"
                    previousStep = 3400
                    nextStep =3700
                } else if (meters >= 3700){
                    level = "Very good"
                    previousStep = 3700
                    nextStep = null
                }
            }
            else {
                if (meters < 2100) {
                    level= "Very bad"
                    previousStep = 0
                    nextStep = 2100
                } else if (meters < 2400) {
                    level = "Bad"
                    previousStep = 2100
                    nextStep =2400
                } else if (meters < 2700) {
                    level = "Average"
                    previousStep = 2400
                    nextStep =2700
                } else if (meters < 3000) {
                    level = "Good"
                    previousStep = 2700
                    nextStep =3000
                } else if (meters >= 3000) {
                    level = "Very good"
                    previousStep = 3000
                    nextStep = null
                }

            }
        } else {
            if (age<15) {
                if (gender=="Male") {
                    if (meters < 2100){
                        level = "Very bad"
                        previousStep = 0
                        nextStep = 2100
                    } else if (meters < 2200) {
                        level = "Bad"
                        previousStep = 2100
                        nextStep =2200
                    } else if (meters < 2400) {
                        level = "Average"
                        previousStep = 2200
                        nextStep =2400
                    } else if (meters < 2700) {
                        level = "Good"
                        previousStep = 2400
                        nextStep =2700
                    } else if (meters >= 2700) {
                        level = "Very good"
                        previousStep = 2700
                        nextStep = null
                    }
                }else{
                    if (meters < 1500) {
                        level = "Very bad"
                        previousStep = 0
                        nextStep =1500
                    } else if (meters < 1600) {
                        level = "Bad"
                        previousStep = 1500
                        nextStep =1600
                    } else if (meters < 1900) {
                        level = "Average"
                        previousStep = 1600
                        nextStep =1900
                    } else if (meters < 2000) {
                        level = "Good"
                        previousStep = 1900
                        nextStep =2000
                    } else if (meters >= 2000) {
                        level = "Very good"
                        previousStep = 2000
                        nextStep = null
                    }
                }
            } else if (age<17){
                if (gender=="Male") {
                    if (meters < 2200) {
                        level = "Very bad"
                        previousStep = 0
                        nextStep =2200
                    } else if (meters < 2300) {
                        level = "Bad"
                        previousStep = 2200
                        nextStep =2300
                    } else if (meters < 2500) {
                        level = "Average"
                        previousStep = 2300
                        nextStep =2500
                    } else if (meters < 2800) {
                        level = "Good"
                        previousStep = 2500
                        nextStep =2800
                    } else if (meters >= 2800)  {
                        level = "Very good"
                        previousStep = 2800
                        nextStep = null
                    }
                } else{
                    if (meters < 1600) {
                        level = "Very bad"
                        previousStep = 0
                        nextStep =1600
                    } else if (meters < 1700) {
                        level = "Bad"
                        previousStep = 1600
                        nextStep =1700
                    } else if (meters < 2000) {
                        level = "Average"
                        previousStep = 1700
                        nextStep =2000
                    } else if (meters < 2100) {
                        level = "Good"
                        previousStep = 2000
                        nextStep =2100
                    } else if (meters >= 2100) {
                        level = "Very good"
                        previousStep = 2100
                        nextStep = null
                    }
                }
            } else if (age<20){
                if (gender=="Male") {
                    if (meters < 2300) {
                        level = "Very bad"
                        previousStep = 0
                        nextStep =2300
                    } else if (meters < 2500){
                        level = "Bad"
                        previousStep = 2300
                        nextStep =2500
                    } else if (meters < 2700){
                        level = "Average"
                        previousStep = 2500
                        nextStep =2700
                    } else if (meters < 3000){
                        level = "Good"
                        previousStep = 2700
                        nextStep =3000
                    } else if (meters >= 3000){
                        level = "Very good"
                        previousStep = 3000
                        nextStep = null
                    }
                }else{
                    if (meters < 1700) {
                        level = "Very bad"
                        previousStep = 0
                        nextStep = 1700
                    } else if (meters < 1800) {
                        level = "Bad"
                        previousStep = 1700
                        nextStep =1800
                    } else if (meters < 2100) {
                        level = "Average"
                        previousStep = 1800
                        nextStep =2100
                    } else if (meters < 2300) {
                        level = "Good"
                        previousStep = 2100
                        nextStep =2300
                    } else if (meters >= 2300) {
                        level = "Very good"
                        previousStep = 2300
                        nextStep = null
                    }
                }
            } else if (age<30){
                if (gender=="Male") {
                    if (meters < 1600) {
                        level = "Very bad"
                        previousStep = 0
                        nextStep =1600
                    } else if (meters < 2200) {
                        level = "Bad"
                        previousStep = 1600
                        nextStep =2200
                    } else if (meters < 2400) {
                        level = "Average"
                        previousStep = 2200
                        nextStep =2400
                    } else if (meters < 2800) {
                        level = "Good"
                        previousStep = 2400
                        nextStep =2800
                    } else if (meters >= 2800) {
                        level = "Very good"
                        previousStep = 2800
                        nextStep = null                    }
                } else {
                    if (meters < 1500) {
                        level = "Very bad"
                        previousStep = 0
                        nextStep =1500
                    } else if (meters < 1800) {
                        level = "Bad"
                        previousStep = 1500
                        nextStep =1800
                    } else if (meters < 2200) {
                        level = "Average"
                        previousStep = 1800
                        nextStep =2200
                    } else if (meters < 2700) {
                        level = "Good"
                        previousStep = 2200
                        nextStep =2700
                    } else if (meters >= 2700) {
                        level = "Very good"
                        previousStep = 2700
                        nextStep = null
                    }
                }
            } else if (age<40){
                if (gender=="Male") {
                    if (meters < 1500){
                        level = "Very bad"
                        previousStep = 0
                        nextStep =1500
                    } else if (meters < 1900){
                        level = "Bad"
                        previousStep = 1500
                        nextStep =1900
                    } else if (meters < 2300){
                        level = "Average"
                        previousStep = 1900
                        nextStep =2300
                    } else if (meters < 2700){
                        level = "Good"
                        previousStep = 2300
                        nextStep =2700
                    } else if (meters >= 2700){
                        level = "Very good"
                        previousStep = 2700
                        nextStep = null
                    }
                }else{
                    if (meters < 1400) {
                        level = "Very bad"
                        previousStep = 0
                        nextStep =1400
                    } else if (meters < 1700) {
                        level = "Bad"
                        previousStep = 1400
                        nextStep =1700
                    } else if (meters < 2000) {
                        level = "Average"
                        previousStep = 1700
                        nextStep =2000
                    } else if (meters < 2500) {
                        level = "Good"
                        previousStep = 2000
                        nextStep =2500
                    } else if (meters >= 2500) {
                        level = "Very good"
                        previousStep = 2500
                        nextStep = null
                    } 
                }
            } else if (age<50){
                if (gender=="Male") {
                    if (meters < 1400) {
                        level = "Very bad"
                        previousStep = 0
                        nextStep =1400
                    } else if (meters < 1700) {
                        level = "Bad"
                        previousStep = 1400
                        nextStep =1700
                    } else if (meters < 2100) {
                        level = "Average"
                        previousStep = 1700
                        nextStep =2100
                    } else if (meters < 2500) {
                        level = "Good"
                        previousStep = 2100
                        nextStep =2500
                    } else if (meters >= 2500) {
                        level = "Very good"
                        previousStep = 2500
                        nextStep = null
                    }
                } else {
                    if (meters < 1200) {
                        level = "Very bad"
                        previousStep = 0
                        nextStep =1200
                    } else if (meters < 1500) {
                        level = "Bad"
                        previousStep = 1200
                        nextStep =1500
                    } else if (meters < 1900) {
                        level = "Average"
                        previousStep = 1500
                        nextStep =1900
                    } else if (meters < 2300) {
                        level = "Good"
                        previousStep = 1900
                        nextStep =2300
                    } else if (meters >= 2300) {
                        level = "Very good"
                        previousStep =2300
                        nextStep = null
                    }
                }
            } else if (age>50){
                if (gender=="Male") {
                    if (meters < 1300){
                        level = "Very bad"
                    } else if (meters < 1600){
                        level = "Bad"
                        previousStep = 1300
                        nextStep =1600
                    } else if (meters < 2000){
                        level = "Average"
                        previousStep = 1600
                        nextStep =2000
                    } else if (meters < 2400){
                        level = "Good"
                        previousStep = 2000
                        nextStep =2400
                    } else if (meters >= 2400){
                        level = "Very good"
                        previousStep =2400
                        nextStep = null
                    }
                }else{
                    if (meters < 1100) {
                        level = "Very bad"
                        previousStep = 0
                        nextStep =1100
                    } else if (meters < 1400) {
                        level = "Bad"
                        previousStep = 1100
                        nextStep =1400
                    } else if (meters < 1700) {
                        level = "Average"
                        previousStep = 1400
                        nextStep =1700
                    } else if (meters < 2200) {
                        level = "Good"
                        previousStep = 1700
                        nextStep =2200
                    } else if (meters >= 2200) {
                        level = "Very good"
                        previousStep = 2200
                        nextStep = null
                    }
                }
            }

        }
        val toReturn: String
        if (nextStep!=null){
            toReturn = level+" ("+formatMeters(previousStep!!)+"-"+formatMeters(nextStep!!)+")"
        } else {
            toReturn = level+" ( >"+formatMeters(previousStep!!)+")"
        }
        return toReturn
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
        return avgSpeed
    }

    fun getRoutePoints(): List<Location>{
        return routePoints
    }

    private fun formatMeters(meters:Int): String {
        val mSharedPreference = PreferenceManager.getDefaultSharedPreferences(context)
        val miles = mSharedPreference.getBoolean("miles", false)
        if (!miles) {
            return "%.0fm".format(meters.toFloat())
        } else {
            return "%.0fyd".format(meters*1.09361)
        }
    }


}