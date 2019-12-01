package com.example.coopertest

import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings.Global.getString
import com.example.coopertest.R
import androidx.preference.PreferenceManager
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Results (meters : Double, context: Context){

    var meters : Double=0.0
    var age: Int=0
    var athlete: Boolean = false
    var gender : String = ""
    val context:Context=context

    init {
        val mSharedPreference = PreferenceManager.getDefaultSharedPreferences(context)
        val dateBirthday = mSharedPreference.getString("birthday", "null")
        age=getAge(dateBirthday.toString())
        this.meters=meters
        athlete = mSharedPreference.getBoolean("athlete", false)
        gender = mSharedPreference.getString("gender", "Male")!!

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

    fun getLevel(): String {
        var yourResult : String=""
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

}