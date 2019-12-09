package com.lasockiquenon.coopertest

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.lasockiquenon.coopertest.utils.LocationHandler


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startTest(v: View) = when {
        !isParametersComplete() -> completeParameters()
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

    private fun isParametersComplete(): Boolean {
        val mSharedPreference = PreferenceManager.getDefaultSharedPreferences(this)
        return (mSharedPreference.contains("birthday") && mSharedPreference.contains("name")
                && mSharedPreference.contains("gender"))

    }

    private fun completeParameters() {
        AlertDialog.Builder(this)
            .setMessage(R.string.goToSettingsMessage)
            .setCancelable(false)
            .setPositiveButton(
                R.string.acceptGoToSettings,
                DialogInterface.OnClickListener() { dialogInterface: DialogInterface, i: Int ->
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                })
            .setNegativeButton(R.string.goBackToMenu, null)
            .show()

    }


}
