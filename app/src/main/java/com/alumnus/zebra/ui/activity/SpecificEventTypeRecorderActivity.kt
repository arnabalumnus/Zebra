package com.alumnus.zebra.ui.activity

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.room.Room
import com.alumnus.zebra.R
import com.alumnus.zebra.db.AppDatabase
import com.alumnus.zebra.db.entity.AccLogEntity
import com.alumnus.zebra.machineLearning.DataAnalysis
import com.alumnus.zebra.machineLearning.utils.ExportFiles
import com.alumnus.zebra.pojo.AccelerationNumericData
import com.alumnus.zebra.utils.Constant
import com.alumnus.zebra.utils.Constant.DATABASE_NAME
import com.alumnus.zebra.utils.CsvFileOperator
import com.alumnus.zebra.utils.DateFormatter
import com.alumnus.zebra.utils.FolderFiles
import kotlinx.android.synthetic.main.activity_specfice_event_type_recorder.*
import kotlinx.coroutines.*
import java.io.File

/**
 * This Activity used for Event recording App. Do the flowing changes to enable this class as LAUNCHER Activity
 *  1. In Manifest flow the TODO
 *  2. app/build.gradle file change the package name "com.alumnus.zebra" to "com.alumnus.zebra.recoder"
 */
class SpecificEventTypeRecorderActivity : AppCompatActivity(), SensorEventListener {

    private val TAG = "SpecificEventTypeRecord"
    private var db: AppDatabase? = null
    private var accLogEntity: AccLogEntity? = null
    private var G_towards_X = 0f
    private var G_towards_Y = 0f
    private var G_towards_Z = 0f
    lateinit var job: Job
    private lateinit var sp: SharedPreferences

    private var trackingUpToTime: Long = 0
    private lateinit var fileName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_specfice_event_type_recorder)

        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, DATABASE_NAME).allowMainThreadQueries().build()
        accLogEntity = AccLogEntity()

        sp = getSharedPreferences(Constant.SP, MODE_PRIVATE)
        if (isStoragePermissionGranted) {
            if (!sp.getBoolean(Constant.isAutoStartPermissionGranted, false)) {
                val editor = sp.edit()
                editor.putBoolean(Constant.isAutoStartPermissionGranted, true)
                editor.apply()
            }
        }
    }

    /**
     * Button Click
     */
    fun startTracking(view: View) {
        if (et_time_span.text.toString().isNotBlank()) {
            btn_start_tracking.isEnabled = false
            val mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
            val mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_STATUS_ACCURACY_LOW)
            trackingUpToTime = System.currentTimeMillis() + et_time_span.text.toString().toLong() * 1000 ?: 0
            fileName = et_event_type.text.toString() ?: "UndefinedFileName"
        } else {
            et_time_span.error = "Cannot be empty"
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (System.currentTimeMillis() > trackingUpToTime) {
            fetchRecordFromDBAndExportIntoCsvFile(fileName)
            val mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
            mSensorManager.unregisterListener(this)
            return
        }

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
            }
        } catch (e: Exception) {
            //Log.e(TAG, "${e.message}")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun fetchRecordFromDBAndExportIntoCsvFile(fileName: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val deferred = CoroutineScope(Dispatchers.IO).async {
                val accLogEntities: List<AccLogEntity> = db!!.accLogDao().all
                val accelerationsDataList: ArrayList<AccelerationNumericData> = ArrayList()
                for (accLogEntity in accLogEntities) {
                    accelerationsDataList.add(AccelerationNumericData(accLogEntity.ts, accLogEntity.x, accLogEntity.y, accLogEntity.z))
                }
                FolderFiles.createFolder(this@SpecificEventTypeRecorderActivity, "KnownTypesEvent")
                CsvFileOperator.writeCsvFile(this@SpecificEventTypeRecorderActivity, accelerationsDataList, folderName = "KnownTypesEvent", fileName = "$fileName-${DateFormatter.getTimeStampFileName(System.currentTimeMillis())}")
                db!!.accLogDao().deleteAll(System.currentTimeMillis())
                Log.d("msg", "fetchRecordFromDBAndExportIntoCsvFile: $fileName ${DateFormatter.getTimeStampFileName(System.currentTimeMillis())}")
                FolderFiles.deleteFile(this@SpecificEventTypeRecorderActivity, "logs", "log-cacheLog", ".html")
                DataAnalysis().startEventAnalysis(accelerationsDataList, this@SpecificEventTypeRecorderActivity, "cacheLog")
            }
            deferred.await()
            btn_start_tracking.isEnabled = true

            val logHtmlFilePath = "/sdcard/ZebraApp/logs/log-cacheLog.html"
            openHtmlUsingChrome(logHtmlFilePath)
        }
    }

    //permission is automatically granted on sdk<23 upon installation
    private val isStoragePermissionGranted: Boolean
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted")
                true
            } else {
                Log.v(TAG, "Permission is revoked")
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted")
            true
        }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0])
            //resume tasks needing this permission
            ExportFiles.prepareDataChunk(this, true)
            if (!sp.getBoolean(Constant.isAutoStartPermissionGranted, false)) {
                val editor = sp.edit()
                editor.putBoolean(Constant.isAutoStartPermissionGranted, true)
                editor.apply()
            }
        }
    }

    /**
     * Opens any web URL using browser app through intent.
     *
     * @param url
     */
    private fun openWebPage(url: String) {
        try {
            val webpage: Uri = Uri.parse(url)
            val myIntent = Intent(Intent.ACTION_VIEW, webpage)
            startActivity(myIntent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }


    /**
     * Opens .jpg file using Photos or Gallery app
     *
     * @param imageFilePath     Sample_path: /sdcard/Download/test.jpg
     */
    private fun openImageUsingGallery(imageFilePath: String) {
        val file = File(imageFilePath)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val apkURI = FileProvider.getUriForFile(applicationContext, "$packageName.provider", file)
            intent.setDataAndType(apkURI, "image/jpg")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            intent.setDataAndType(Uri.fromFile(file), "image/jpg")
        }
        startActivity(intent)
    }


    /**
     * Opens .html files using browser app through intent.
     *
     * @param htmlFilePath              Sample_path: /sdcard/ZebraApp/logs/log-cacheLog.html"
     */
    private fun openHtmlUsingChrome(htmlFilePath: String) {
        try {
            val file = File(htmlFilePath)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val apkURI = FileProvider.getUriForFile(applicationContext, "$packageName.provider", file)
                intent.setDataAndType(apkURI, "text/plain")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } else {
                intent.setDataAndType(Uri.fromFile(file), "text/plain")
            }
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            Log.e(TAG, "openHtmlUsingChrome: ${e.message}")
        }
    }
}