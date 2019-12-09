package com.lasockiquenon.coopertest

import android.app.AlertDialog
import android.content.*
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.*
import android.view.WindowManager
import androidx.preference.PreferenceManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.lasockiquenon.coopertest.utils.AudioNotifier
import com.lasockiquenon.coopertest.utils.Results
import com.lasockiquenon.coopertest.utils.Storage
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.lasockiquenon.coopertest.utils.UnitsUtils
import kotlinx.android.synthetic.main.activity_test.*
import java.text.SimpleDateFormat
import java.util.*


class TestActivity : BaseThemedActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var isMapReady: Boolean = false
    private var testStarted: Boolean = false

    private lateinit var testTimer: CountDownTimer
    private lateinit var startTimer: CountDownTimer
    private var routePoints: List<Location> = emptyList()
    private var currentDistanceMeters = 0.0
    private var avgSpeed: Double = 0.0

    private val testLengthMinutes = 1

    private lateinit var notifier: AudioNotifier
    private val unitsUtils = UnitsUtils(this)

    private lateinit var locationService: LocationService
    private var isServiceBound = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        notifier = AudioNotifier(this)
        testTimer = getTestTimer()
        startTimer = getStartTimer()

        val intent = Intent(this, LocationService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        startService(intent)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val darkThemeOn = sharedPref.getBoolean("dark_theme", true)
        if (darkThemeOn) {
            val style = MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_night)
            mMap.setMapStyle(style)
        }
        isMapReady = true
        startTest()
    }

    private fun startTest() {
        startTimer.start()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (Build.VERSION.SDK_INT >= 27) {
            setShowWhenLocked(true)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        }
        notifier.playTestStartFile()
    }

    private fun finishTest(completed: Boolean) {
        unbindService(serviceConnection)
        val intent = Intent(this, LocationService::class.java)
        stopService(intent)
        isServiceBound = false

        timerView.text = getString(R.string.result)

        if (completed && routePoints.count() > 0) {
            val firstLocation = LatLng(routePoints.first().latitude, routePoints.first().longitude)
            val lastLocation = LatLng(routePoints.last().latitude, routePoints.last().longitude)
            val middleLocation =
                LatLngBounds.builder().include(firstLocation).include(lastLocation).build().center
            mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(middleLocation.latitude, middleLocation.longitude), 12.5f
                )
            )
            val objectResult = Results(
                currentDistanceMeters,
                routePoints,
                avgSpeed,
                this
            )
            val yourLevel = objectResult.getLevel()
            resultTextView.text = yourLevel
            Storage()
                .addResult(this, objectResult)

        }

        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (Build.VERSION.SDK_INT >= 27) {
            setShowWhenLocked(false)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        }
    }

    private fun processNewLocation(newLocation: Location) {
        mMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(newLocation.latitude, newLocation.longitude), 15.0f
            )
        )

        if (!testStarted) {
            return
        }

        currentSpeedView.text = when {
            newLocation.hasSpeed() -> unitsUtils.formatSpeed(newLocation.speed)
            routePoints.count() > 0 -> unitsUtils.formatSpeed(
                unitsUtils.calculateSpeed(
                    routePoints.last(),
                    newLocation
                )
            )
            else -> unitsUtils.formatSpeed(0.toFloat())
        }

        if (routePoints.count() > 0) {
            updateMapAndSpeed(newLocation)
        }
        routePoints = routePoints + newLocation
    }

    private fun updateMapAndSpeed(newLocation: Location) {
        val distanceChange = newLocation.distanceTo(routePoints.last())
        currentDistanceMeters += distanceChange
        currentDistanceTextView.text = unitsUtils.formatDistance(currentDistanceMeters)

        avgSpeed = currentDistanceMeters * 1000 / (newLocation.time - routePoints.first().time)
        avgSpeedView.text = unitsUtils.formatSpeed(avgSpeed.toFloat())

        val options = PolylineOptions()
        options.color(Color.RED)
        options.width(5f)
        options.add(LatLng(routePoints.last().latitude, routePoints.last().longitude))
        options.add(LatLng(newLocation.latitude, newLocation.longitude))
        mMap.addPolyline(options)
    }

    private fun getTestTimer(): CountDownTimer {
        return getTimer(testLengthMinutes * 60 * 1000, onFinishFun = { finishTest(true) },
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

            @SuppressLint("SimpleDateFormat")
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

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LocationService.LocalBinder
            locationService = binder.getService()
            isServiceBound = true
            LocalBroadcastManager.getInstance(this@TestActivity).registerReceiver(
                locationReceiver,
                IntentFilter(locationService.ACTION_BROADCAST)
            )
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
        }

    }

    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val location: Location? = intent!!.getParcelableExtra(locationService.EXTRA_LOCATION)
            if (location != null)
                processNewLocation(location)
        }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setMessage(R.string.ConfirmExitMessage)
            .setCancelable(false)
            .setPositiveButton(R.string.ConfirmExitAccept) { _: DialogInterface, _: Int ->
                finishTest(false)
                startTimer.cancel()
                testTimer.cancel()
                notifier.stop()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            .setNegativeButton(R.string.ConfirmExitRefuse, null)
            .show()
    }

}
