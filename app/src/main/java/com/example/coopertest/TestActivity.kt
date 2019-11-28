package com.example.coopertest

import android.Manifest
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
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.activity_test.*
import java.text.SimpleDateFormat
import java.util.*

class TestActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var isMapReady: Boolean = false
    private var firstLaunch: Boolean = true

    private lateinit var timer: CountDownTimer
    private var routePoints: List<Location> = emptyList()
    private var currentDistanceMeters = 0.0

    private val PERMISSION_ID = 42
    private val testLengthMinutes = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        timer = getTimer()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val style = MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_night)
        mMap = googleMap
        mMap.setMapStyle(style)
        //Put the flag at one
        isMapReady = true
        requestPermissionsAndLocationUpdates()
        timer.start()
        currentDistanceTextView.text = currentDistanceString()
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            if (!isMapReady || locationResult.lastLocation == null) return
            val currentLocation = locationResult.lastLocation

            processNewLocation(currentLocation)
        }
    }

    private fun processNewLocation(newLocation: Location) {
        mMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(newLocation.latitude, newLocation.longitude), 15.0f
            )
        )

        currentSpeedView.text = when {
            newLocation.hasSpeed() -> formatSpeed(newLocation.speed)
            routePoints.count() > 0 -> formatSpeed(calculateSpeed(routePoints.last(), newLocation))
            else -> formatSpeed(0.toFloat())
        }

        if (routePoints.count() > 0) {
            updateMapAndSpeed(newLocation)
        }
        routePoints = routePoints + newLocation
    }

    private fun updateMapAndSpeed(newLocation: Location) {
        currentDistanceMeters += newLocation.distanceTo(routePoints.last())
        currentDistanceTextView.text = currentDistanceString()

        val avgSpeedSoFar =
            currentDistanceMeters * 1000 / (newLocation.time - routePoints.first().time)
        avgSpeedView.text = formatSpeed(avgSpeedSoFar.toFloat())

        val options = PolylineOptions()
        options.color(Color.RED)
        options.width(5f)
        options.add(LatLng(routePoints.last().latitude, routePoints.last().longitude))
        options.add(LatLng(newLocation.latitude, newLocation.longitude))
        mMap.addPolyline(options)
    }

    private fun requestPermissionsAndLocationUpdates() {
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
                initLocationUpdates()
                firstLaunch = false
            }
        }
    }

    private fun initLocationUpdates() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        // Location request interval: max and min
        mLocationRequest.interval = 2500
        mLocationRequest.fastestInterval = 1500

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }

    private fun getTimer(): CountDownTimer {
        return object : CountDownTimer((testLengthMinutes * 60 * 1000).toLong(), 100) {

            override fun onTick(millisUntilFinished: Long) {
                timerView.text = SimpleDateFormat("mm:ss.S").format(
                    Date(millisUntilFinished)
                )
            }

            override fun onFinish() {
                mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                timerView.text = "Result:"
            }
        }
    }

    private fun currentDistanceString(): String {
        return "%.0fm".format(currentDistanceMeters)
    }

    private fun formatSpeed(speed: Float): String {
        return "%.1f m/s".format(speed)
    }

    private fun calculateSpeed(start: Location, end: Location): Float {
        return (end.distanceTo(start) * 1000 / (end.time - start.time)).toFloat()
    }

    private fun backToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) ==
                PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) ==
                PackageManager.PERMISSION_GRANTED)
    }

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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_ID && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestPermissionsAndLocationUpdates()
        }
    }
}
