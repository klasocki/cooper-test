package com.example.coopertest

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Looper
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
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
    private var testStarted: Boolean = false

    private lateinit var testTimer: CountDownTimer
    private lateinit var startTimer: CountDownTimer
    private var routePoints: List<Location> = emptyList()
    private var currentDistanceMeters = 0.0
    private val maxLocationUpdateInterval: Long = 2500
    private val minLocationUpdateInterval: Long = 1500

    private val testLengthMinutes = 2
    private val maxDistanceChangeBetweenLocations = 50

    private lateinit var notifier: AudioNotifier


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        notifier = AudioNotifier(this)
        testTimer = getTestTimer()
        startTimer = getStartTimer()

    }

    override fun onMapReady(googleMap: GoogleMap) {
        val style = MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_night)
        mMap = googleMap
        mMap.setMapStyle(style)
        isMapReady = true
        startTest()
    }

    private fun startTest(){
        requestPermissionsAndLocationUpdates()
        startTimer.start()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (Build.VERSION.SDK_INT >= 27) {
            setShowWhenLocked(true)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        }
        notifier.playTestStartFile()
    }

    private fun finishTest(){
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
        timerView.text = getString(R.string.result)
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (Build.VERSION.SDK_INT >= 27) {
            setShowWhenLocked(false)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        }
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

        if (!testStarted) { return }

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
        val distanceChange = newLocation.distanceTo(routePoints.last())
//        if (distanceChange > )
        currentDistanceMeters += distanceChange
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

    private fun getTestTimer(): CountDownTimer {
        return getTimer(testLengthMinutes * 60 * 1000, onFinishFun = { finishTest() },
            onTickFun = {
            notifier.notifyAboutTimeLeft(it)
        })
    }

    private fun getStartTimer(): CountDownTimer {
        return getTimer(10 * 1000, onFinishFun = {
            currentDistanceTextView.text = getString(R.string.go)
            testStarted = true
            testTimer.start()
        }, onTickFun = {
            currentDistanceTextView.text = getString(R.string.ready)
        })
    }

    private fun getTimer(
        length: Int,
        onFinishFun: () -> Unit,
        onTickFun: (Long) -> Unit = { _ -> ; }
    ): CountDownTimer {
        return object : CountDownTimer((length).toLong(), 100) {

            override fun onTick(millisUntilFinished: Long) {
                timerView.text = SimpleDateFormat("mm:ss.S").format(
                    Date(millisUntilFinished)
                )
                onTickFun(millisUntilFinished)
            }

            override fun onFinish() {
                onFinishFun()
            }
        }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setMessage(R.string.ConfirmExitMessage)
            .setCancelable(false)
            .setPositiveButton(R.string.ConfirmExitAccept) { _: DialogInterface, _: Int ->
                finishTest()
                startTimer.cancel()
                testTimer.cancel()
                notifier.stop()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            .setNegativeButton(R.string.ConfirmExitRefuse, null)
            .show()
    }


    private fun currentDistanceString(): String {
        return "%.0fm".format(currentDistanceMeters)
    }


    private fun formatSpeed(speed: Float): String {
        return "%.1f m/s".format(speed)
    }

    private fun calculateSpeed(start: Location, end: Location): Float {
        return (end.distanceTo(start) * 1000 / (end.time - start.time))
    }

    private fun initLocationUpdates() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        // Location request interval: max and min
        mLocationRequest.interval = maxLocationUpdateInterval
        mLocationRequest.fastestInterval = minLocationUpdateInterval

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }


    private fun requestPermissionsAndLocationUpdates() {
        if (!LocationHandler.checkPermissions(this) || !LocationHandler.isLocationEnabled(this)) {
            requestPermissions()
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


    private fun requestPermissions() {
        val intent = Intent(this, LocationActivity::class.java)
        startActivity(intent)
    }

}
