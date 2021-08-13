package com.alumnus.zebra.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.room.Room
import com.alumnus.zebra.R
import com.alumnus.zebra.db.AppDatabase
import com.alumnus.zebra.db.entity.AccLogEntity
import com.alumnus.zebra.utils.Constant.DATABASE_NAME
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

/**
 * LifeTimeService is running 24 x 7 as foregroundService with a notification.
 * and
 * Saving accelerometer data into database continuously.
 * So, memory management for this class is very important
 *
 * **Notes: For memory management**
 *  1. Avoid using TAG, Log messages in this class for memory management.
 *  2. Follow Singleton design pattern i.e.
 *      - Avoid repetitive object creation by calling constructor.
 *      - Use getter(), setter() method.
 * @author Arnab
 */
class LifeTimeService : Service(), SensorEventListener {

    //private val TAG = javaClass.simpleName

    private var db: AppDatabase? = null
    private var accLogEntity: AccLogEntity? = null
    private var G_towards_X = 0f
    private var G_towards_Y = 0f
    private var G_towards_Z = 0f
    lateinit var job: Job
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val frequency = intent.extras!!.getInt("frequency", 5)

        //Log.d(TAG, "onStartCommand: frequency=$frequency")
        val mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (frequency == 50) mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_STATUS_ACCURACY_LOW) //Value=1 , Frequency: ~50/sec
        else if (frequency == 15) mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM) //Value=2 , Frequency: ~15/sec
        else if (frequency == 5) mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_STATUS_ACCURACY_HIGH) //Value=3 , Frequency: ~5/sec
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, DATABASE_NAME).build()
        accLogEntity = AccLogEntity()
        runAsForeground()
        return START_NOT_STICKY
    }

    private fun runAsForeground() {
        val notificationIntent = Intent(this, this.javaClass)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT)
        val mNotificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel("secondary", " secondary Channel", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "secondary Channel"
            channel.setShowBadge(true)
            mNotificationManager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(this, "secondary")
                .setNumber(0)
                .setOngoing(true)
                .setColorized(true)
                .setColor(Color.parseColor("#085A0B"))
                .setSmallIcon(R.drawable.ic_notification_small)
                .setSubText("Accelerometer is running...")
                .setContentTitle("Zebra")
                .setContentText("Accelerometer is running...")
                .setSilent(true)
                .setContentIntent(pendingIntent)
        startForeground(12345, notification.build())
    }

    override fun onSensorChanged(event: SensorEvent) {
        try {
            G_towards_X = event.values[0]
            G_towards_Y = event.values[1]
            G_towards_Z = event.values[2]
            //Log.i(TAG, "onSensorChanged: X:$G_towards_X  Y:$G_towards_Y  Z:$G_towards_Z");
            accLogEntity!!.ts = System.currentTimeMillis()
            accLogEntity!!.x = G_towards_X
            accLogEntity!!.y = G_towards_Y
            accLogEntity!!.z = G_towards_Z

            if (db != null) {
                job = CoroutineScope(Dispatchers.IO).launch {
                    db!!.accLogDao().insert(accLogEntity!!)
                }
                /*thread {
                    db!!.accLogDao().insert(accLogEntity!!)
                }*/
            }
        } catch (e: Exception) {
            //Log.e(TAG, "${e.message}")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
}