package com.alumnus.zebra.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.alumnus.zebra.service.LifeTimeService

/**
 * This BootReceiver class is Broadcast Receiver that triggers its
 * onReceive() at the time of booting android device.
 *
 * Some times this trigger is take some time daley android 3-5 mints
 * based on load on the system at the launch time.
 *
 * Next in this onReceive() the LifeTimeService starts
 * with user defined frequency. And if frequency is not set
 * the default value is set to 5.
 *
 *
 * @author Arnab Kundu
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context, "Booting", Toast.LENGTH_LONG).show()
        val sp = context.getSharedPreferences("Zebra", Context.MODE_PRIVATE)
        val frequency = sp.getInt("frequency", 5)
        val serviceIntent = Intent(context, LifeTimeService::class.java)
        serviceIntent.putExtra("frequency", frequency)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            ContextCompat.startForegroundService(context, serviceIntent)
        else
            context.startService(intent)
    }

}