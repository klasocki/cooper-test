package com.example.coopertest

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


    }

    fun goToTest(v: View) {
        if (isParametersComplete()) {
            val intent = Intent(this, TestActivity::class.java)
            startActivity(intent)
        } else {
            completeParameters()
        }
    }

    fun goToSettings(v: View) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    fun isParametersComplete(): Boolean {
        val mSharedPreference = PreferenceManager.getDefaultSharedPreferences(this)
        val dateBirthday = mSharedPreference.getString("birthday", "null")
        val gender = mSharedPreference.getString("gender", "Male")!!

        return (mSharedPreference.contains("birthday") && mSharedPreference.contains("name")
            && mSharedPreference.contains("gender"))

    }

    fun completeParameters(){
        AlertDialog.Builder(this)
            .setMessage(R.string.goToSettingsMessage)
            .setCancelable(false)
            .setPositiveButton(R.string.acceptGoToSettings, DialogInterface.OnClickListener() { dialogInterface: DialogInterface, i: Int ->
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            })
            .setNegativeButton(R.string.goBackToMenu, null)
            .show()

    }


}
