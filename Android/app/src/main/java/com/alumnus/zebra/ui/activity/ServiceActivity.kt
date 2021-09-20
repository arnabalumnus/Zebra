package com.alumnus.zebra.ui.activity

import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alumnus.zebra.R
import com.alumnus.zebra.service.LifeTimeService
import com.alumnus.zebra.utils.Constant

/**
 * Helps to Starts background service to record accelerometer data with specified frequency
 *
 * @author Arnab Kundu
 */
class ServiceActivity : AppCompatActivity() {

    private val TAG = "ServiceActivity"
    var frequency = 50 // Default Records per second from accelerometer(If frequency not selected)
    private lateinit var sp: SharedPreferences
    private lateinit var editor:SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service)
        sp = getSharedPreferences(Constant.SP, MODE_PRIVATE)
        val freqId = sp.getInt("frequencyId", 0)
        val frequency_radio_group = findViewById<RadioGroup>(R.id.frequency_radio_group)
        if (freqId != 0) {
            frequency_radio_group.check(freqId)
            when (frequency_radio_group.checkedRadioButtonId) {
                R.id.low_freq -> frequency = 5
                R.id.mid_freq -> frequency = 15
                R.id.high_freq -> frequency = 50
            }
        } else {
            Toast.makeText(this, "Frequency not selected", Toast.LENGTH_SHORT).show()
        }
        frequency_radio_group.setOnCheckedChangeListener { group, checkedId ->
            editor = sp.edit()
            when (checkedId) {
                R.id.low_freq -> {
                    frequency = 5
                    Log.d(TAG, "low_freq: ")
                    editor.putInt("frequencyId", R.id.low_freq)
                    editor.putInt("frequency", 5)
                }
                R.id.mid_freq -> {
                    frequency = 15
                    Log.d(TAG, "mid_freq: ")
                    editor.putInt("frequencyId", R.id.mid_freq)
                    editor.putInt("frequency", 15)
                }
                R.id.high_freq -> {
                    frequency = 50
                    Log.d(TAG, "high_freq: ")
                    editor.putInt("frequencyId", R.id.high_freq)
                    editor.putInt("frequency", 50)
                }
                else -> Log.d(TAG, "default: ")
            }
            editor.apply()
        }
    }

    fun startLifeTimeService(v: View?) {
        val intent = Intent(this, LifeTimeService::class.java)
        intent.putExtra("frequency", frequency)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(intent) else startService(intent)
        val myIntent = Intent(this, LifeTimeService::class.java)
        myIntent.putExtra("ALARM", true)
        val pendingIntent = PendingIntent.getService(this, 0, myIntent, 0)
        /**
         * AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
         * long firstTime = SystemClock.elapsedRealtime();
         * alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, 5 * 60 * 1000, pendingIntent);
         */
    }
}