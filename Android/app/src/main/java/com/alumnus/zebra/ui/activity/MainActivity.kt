package com.alumnus.zebra.ui.activity

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.alumnus.zebra.R
import com.alumnus.zebra.broadcastReceiver.PowerConnectionReceiver
import com.alumnus.zebra.machineLearning.utils.ExportFiles.prepareDataChunk
import com.alumnus.zebra.utils.Constant

/**
 * This is the Launcher Activity for this Zebra app.
 * Its a Dashboard for this with couple of buttons. Utility of this buttons explained below:
 *
 * 1. Accelerometer -   It displays the current accelerometer data in 2 graph in `AccelerometerActivity` screen
 * 2. Service -         It is responsible for start the `LifeTimeService` with user specified frequency
 * 3. Export Data -     It exports data from database. In details it exports all rows of `accelerometer_log` table into a csv file.
 *                      Follow the README for exported file location.
 * 4. Database -        It navigates user to `DatabaseActivity` which gives user a quick glance of database entry/status.
 */
class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sp = getSharedPreferences(Constant.SP, MODE_PRIVATE)
        if (isStoragePermissionGranted) {
            if (!sp.getBoolean(Constant.isAutoStartPermissionGranted, false)) {
                val editor = sp.edit()
                editor.putBoolean(Constant.isAutoStartPermissionGranted, true)
                editor.apply()
            }
        }
        val receiver = PowerConnectionReceiver()
        val iFilter = IntentFilter()
        iFilter.addAction(Intent.ACTION_POWER_CONNECTED)
        iFilter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        iFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
        iFilter.addAction(Intent.ACTION_BATTERY_LOW)
        iFilter.addAction(Intent.ACTION_BATTERY_OKAY)
        registerReceiver(receiver, iFilter)
    }

    fun goToAccelerometer(view: View?) {
        startActivity(Intent(this, AccelerometerActivity::class.java))
    }

    fun startService(v: View?) {
        val intent = Intent(this, ServiceActivity::class.java)
        startActivity(intent)
    }

    fun navigateToDatabase(view: View?) {
        startActivity(Intent(this, DatabaseActivity::class.java))
    }

    //region Export Data and save into SD card or Phone Storage
    fun exportDataButton(view: View?) {
        if (isStoragePermissionGranted) {
            prepareDataChunk(this, true)
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
            prepareDataChunk(this, true)
            if (!sp.getBoolean(Constant.isAutoStartPermissionGranted, false)) {
                val editor = sp.edit()
                editor.putBoolean(Constant.isAutoStartPermissionGranted, true)
                editor.apply()
            }
        }
    } //endregion

}