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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.lasockiquenon.coopertest.utils.AudioNotifier
import com.lasockiquenon.coopertest.utils.Results
import com.lasockiquenon.coopertest.utils.Storage
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.activity_test.*
import java.text.SimpleDateFormat
import java.util.*


class TestActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

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

    private lateinit var locationService: LocationService
    private var isServiceBound = false
    private var isTestRunning = false

    private var printResultNumber : Int =-1
    private var routePointsLatLng : List<LatLng> = emptyList()
    var cu : CameraUpdate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        printResultNumber= getIntent().getIntExtra("Results", -1)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if (printResultNumber!=-1){
            val listOfResults = Storage().loadResults(this)
            val myResult= listOfResults!!.get(printResultNumber)

            val stringDate = myResult.getName()
            timerView.text=stringDate
            currentDistanceTextView.text=formatDistance(myResult.getMeters())
            labelCurrSpeed.text=android.text.format.DateFormat.format("yyyy-MM-dd", myResult.getDate())
            currentSpeedView.setVisibility(TextView.INVISIBLE)
            avgSpeedView.text=formatSpeed(myResult.getAvgSpeed().toFloat())
            resultTextView.text=myResult.getLevel()
            rangeTextView.text=myResult.getRange(this)
            myResult.convertLocationAndString()
            routePointsLatLng=myResult.getRoutePointsLatLong()
        }else {

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            notifier = AudioNotifier(this)
            testTimer = getTestTimer()
            startTimer = getStartTimer()

            val intent = Intent(this, LocationService::class.java)
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            startService(intent)
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        val style = MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_night)
        mMap = googleMap
        mMap.setMapStyle(style)
        isMapReady = true
        if (printResultNumber!=-1) {
            if (routePointsLatLng.isNotEmpty()){
                val builder : LatLngBounds.Builder = LatLngBounds.Builder()
                for (i in 0..routePointsLatLng.size-2){
                    drawLineOnMap(routePointsLatLng.get(i),routePointsLatLng.get(i+1))
                    builder.include(routePointsLatLng.get(i))
                }
                builder.include(routePointsLatLng.get(routePointsLatLng.lastIndex))
                val bounds : LatLngBounds = builder.build()
                val padding = 50
                cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
                mMap.setOnMapLoadedCallback(this)
            }
        } else{
            startTest()
        }
    }


    override fun onMapLoaded() {
        if (printResultNumber!=-1) {
            if (cu!=null) {
                mMap.animateCamera(cu)
            }
        }
    }

    private fun startTest() {
        isTestRunning=true
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
        isTestRunning=false
        if (isServiceBound) {
            unbindService(serviceConnection)
            isServiceBound = false
        }
        val intent = Intent(this, LocationService::class.java)
        stopService(intent)

        timerView.text = getString(R.string.result)

        if (completed && routePoints.count() > 0) {
            val builder : LatLngBounds.Builder = LatLngBounds.Builder()
            for (point in routePoints){
                builder.include(LatLng(point.latitude,point.longitude))
            }
            val bounds : LatLngBounds = builder.build()
            val padding = 50
            cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
            mMap.animateCamera(cu)

            val objectResult = Results(
                currentDistanceMeters,
                routePoints,
                avgSpeed,
                this
            )
            resultTextView.text = objectResult.getLevel()
            rangeTextView.text= objectResult.getRange(this)
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
        currentDistanceMeters += distanceChange
        currentDistanceTextView.text = formatDistance(currentDistanceMeters)

        avgSpeed = currentDistanceMeters * 1000 / (newLocation.time - routePoints.first().time)
        avgSpeedView.text = formatSpeed(avgSpeed.toFloat())

        val lastPoint = LatLng(routePoints.last().latitude, routePoints.last().longitude)
        val newPoint= LatLng(newLocation.latitude, newLocation.longitude)
        drawLineOnMap(lastPoint,newPoint)
    }

    private fun drawLineOnMap(firtPoint : LatLng,secondPoint : LatLng){
        val options = PolylineOptions()
        options.color(Color.RED)
        options.width(5f)
        options.add(firtPoint)
        options.add(secondPoint)
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
        if (isTestRunning) {
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
        } else{
            if (isServiceBound){
                unbindService(serviceConnection)
                isServiceBound=false
            }
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }



    private fun formatDistance(distance : Double): String{
        val mSharedPreference = PreferenceManager.getDefaultSharedPreferences(this)
        val miles = mSharedPreference.getBoolean("miles", false)
        if (!miles) {
            return "%.0f m".format(distance)
        } else {
            return "%.0f yd".format(distance * 1.09361)
        }
    }


    private fun formatSpeed(speed: Float): String {
        val mSharedPreference = PreferenceManager.getDefaultSharedPreferences(this)
        val miles = mSharedPreference.getBoolean("miles", false)
        if (!miles) {
            return "%.1f km/h".format(speed * 3.6)
        } else {
            return "%.1f mph".format(speed * 2.23694)
        }
    }

    private fun calculateSpeed(start: Location, end: Location): Float {
        return (end.distanceTo(start) * 1000 / (end.time - start.time))
    }

}
