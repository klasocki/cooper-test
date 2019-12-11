package com.lasockiquenon.coopertest

import android.app.*
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.lasockiquenon.coopertest.utils.LocationHandler
import com.google.android.gms.location.*


class LocationService : Service() {

    private lateinit var location: Location

    lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val desiredLocationUpdateInterval: Long = 2000
    private val minLocationUpdateInterval: Long = 1000
    private var firstLaunch = true

    private val localBinder: IBinder = LocalBinder()
    private val NOTIFICATION_ID = 1
    private lateinit var notification: Notification
    private val CHANNEL_ID = "channel_test"
    private val PACKAGE_NAME = "com.example.coopertest"
    val ACTION_BROADCAST = "$PACKAGE_NAME.broadcast"
    val EXTRA_LOCATION = "$PACKAGE_NAME.location"


    private var mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            if (locationResult.lastLocation == null) return
            val currentLocation = locationResult.lastLocation
            location = currentLocation

            val intent = Intent(ACTION_BROADCAST)
            intent.putExtra(EXTRA_LOCATION, location)
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)

        }
    }

    private fun requestPermissionsAndLocationUpdates() {
        if (!LocationHandler.checkPermissions(this) || !LocationHandler.isLocationEnabled(this)) {
            requestPermissions()
            return
        }

        mFusedLocationClient.lastLocation.addOnCompleteListener { task ->
            val location: Location? = task.result
            if (!task.isSuccessful || location == null || firstLaunch) {
                initLocationUpdates()
                firstLaunch = false
            }
        }
    }

    private fun initLocationUpdates() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        mLocationRequest.interval = desiredLocationUpdateInterval
        mLocationRequest.fastestInterval = minLocationUpdateInterval

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }

    private fun requestPermissions() {
        val intent = Intent(this, LocationActivity::class.java)
        startActivity(intent)
    }

    inner class LocalBinder : Binder() {
        fun getService(): LocationService {
            return this@LocationService
        }
    }

    override fun onCreate() {
        val doNothingWhenClickedNotification = Intent()
        val pendingIntent = PendingIntent.getActivity(this, 0, doNothingWhenClickedNotification, 0)
        notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentText(getString(R.string.test_ongoing))
            .setContentTitle(getString(R.string.accessing_location))
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentIntent(pendingIntent)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel =
                NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.test_ongoing),
                    NotificationManager.IMPORTANCE_LOW
                )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(serviceChannel)
        }

    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestPermissionsAndLocationUpdates()

        startForeground(NOTIFICATION_ID, notification)

        return START_NOT_STICKY
    }


    override fun onBind(intent: Intent?): IBinder? {
        return localBinder
    }

    override fun onDestroy() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
        super.onDestroy()
    }

}
