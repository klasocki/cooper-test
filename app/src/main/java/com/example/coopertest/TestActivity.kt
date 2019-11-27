package com.example.coopertest

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.activity_test.*
import java.text.SimpleDateFormat
import java.util.*


class TestActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var timer: CountDownTimer
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var routePoints: List<Location> = emptyList()
    private var isMapReady: Boolean = false
    private var firstLaunch: Boolean = true

    private val PERMISSION_ID = 42
    private val testLengthMinutes = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        //Initialisation of mFusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        //Start the location in background
        timer = object : CountDownTimer((testLengthMinutes * 60 * 1000).toLong(), 100) {

            override fun onTick(millisUntilFinished: Long) {
                timerView.text = SimpleDateFormat("mm:ss.S").format(
                    Date(millisUntilFinished)
                )
            }

            override fun onFinish() {
                timerView.text = "Done!"
                backToMain()
            }
        }
    }

    fun backToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    //This function is executed when the map is ready
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        //Put the flag at one
        isMapReady = true
        getLastLocation()
        timer.start()
    }

    //Get the last location from the GPS sensor or network location
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (!checkPermissions()) {
            requestPermissions()
            return
        }

        if (!isLocationEnabled()) {
            Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
            return
        }

        mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
            val location: Location? = task.result
            if (location == null || firstLaunch) {
                requestNewLocationData()
                firstLaunch = false
            }
        }
    }

    //Create a background request to update location
    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
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
            if (!isMapReady || locationResult.lastLocation == null) return
            val currentLocation = locationResult.lastLocation
            mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        currentLocation.latitude,
                        currentLocation.longitude
                    ), 15.0f
                )
            )
            if (routePoints.count() > 0) {
                val options = PolylineOptions()
                options.color(Color.RED)
                options.width(5f)

                options.add(LatLng(routePoints.last().latitude, routePoints.last().longitude))
                options.add(LatLng(currentLocation.latitude, currentLocation.longitude))
                mMap.addPolyline(options)
            }
            routePoints = routePoints + currentLocation
        }
    }

    //Verify if the location sensor is enabled on phone
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    //Check if the position permissions is enabled
    private fun checkPermissions(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)
    }

    //Request the location permission
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    //Execute this if the permission is granted
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_ID && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLastLocation()
        }
    }
}
