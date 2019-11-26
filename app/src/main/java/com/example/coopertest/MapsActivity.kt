package com.example.coopertest

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import android.provider.Settings
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.maps.model.PolylineOptions


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {


    private lateinit var mMap: GoogleMap
    private var lastLocation : Location? =null
    private var isMapReady : Boolean = false
    private var firstLunch : Boolean = true
    private var fisrtLocation : Location? = null

    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        //Initialisation of mFusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        //Start the location in background
        getLastLocation()

    }

    //This function is executed when the map is ready
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        //Put the flag at one
        isMapReady=true
    }

    //Get the last location from the GPS sensor or network location
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null || firstLunch) {
                        requestNewLocationData()
                        firstLunch=false
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    //Create a background request to update location
    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        //When we took location : max and min
        mLocationRequest.interval = 10000
        mLocationRequest.fastestInterval = 5000
        //mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    //At each new location received, we add a line on the map
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {

            if (isMapReady && lastLocation!=null){
                if (fisrtLocation==null){
                    fisrtLocation=lastLocation
                    mMap.addMarker(MarkerOptions().position(LatLng(fisrtLocation!!.latitude, fisrtLocation!!.longitude)).title("StartPoint"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(fisrtLocation!!.latitude, fisrtLocation!!.longitude), 16.0f))
                }
                var currentlocation = locationResult.lastLocation
                val options = PolylineOptions()
                options.color(Color.RED)
                options.width(5f)

                if (currentlocation != null) {
                    options.add(LatLng(lastLocation!!.latitude, lastLocation!!.longitude))
                    options.add(LatLng(currentlocation.latitude, currentlocation.longitude))
                    mMap.addPolyline(options)
                    //Use only for debuging, remove after
                    //mMap.addMarker(MarkerOptions().position(LatLng(currentlocation.latitude, currentlocation.longitude)).title("DEBUG"))
                }
            }
            lastLocation=locationResult.lastLocation
        }
    }

    //Verify if the location sensor is enabled on phone
    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    //Check if the position permissions is enabled
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    //Request the location permission
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }

    //Execute this if the permission is granted
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }
}
