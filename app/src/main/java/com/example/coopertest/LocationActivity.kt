package com.example.coopertest

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_location.*


class LocationActivity : AppCompatActivity() {

    private val PERMISSION_ID = 42


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        if (LocationHandler.checkPermissions(this) && !LocationHandler.isLocationEnabled(this)) {
            ActivateLocation.text = getString(R.string.ActivateGPSButton)
        }
    }

    fun requestPermissions() {
        var perms = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= 29){
            perms += Manifest.permission.ACCESS_BACKGROUND_LOCATION
        }
        ActivityCompat.requestPermissions(this, perms, PERMISSION_ID)
    }


    fun activateLocation(v: View) {
        if (!LocationHandler.checkPermissions(this)) {
            requestPermissions()
        } else if (!LocationHandler.isLocationEnabled(this)) {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        if (LocationHandler.isLocationEnabled(this) && LocationHandler.checkPermissions(this)) {
            val intent = Intent(this, TestActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_ID && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (LocationHandler.isLocationEnabled(this)) {
                val intent = Intent(this, TestActivity::class.java)
                startActivity(intent)
            } else {
                ActivateLocation.text = getString(R.string.ActivateGPSButton)
            }
        }
    }

}

