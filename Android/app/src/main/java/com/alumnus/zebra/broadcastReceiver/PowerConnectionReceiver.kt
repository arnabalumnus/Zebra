package com.alumnus.zebra.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.alumnus.zebra.machineLearning.utils.ExportFiles.prepareDataChunk
import com.alumnus.zebra.service.LifeTimeService

/**
 * This PowerConnectionReceiver is Broadcast Receiver that triggers its
 * onReceive() on connecting and disconnecting power to android device.
 *
 * On connecting power prepareDataChunk() helps to move data from
 * accelerometer_log database table to a timestamp.csv file.
 *
 * On power disconnection nothing implemented. This can be changed based
 * on client requirements.
 *
 * @author Arnab Kundu
 */
class PowerConnectionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_POWER_CONNECTED) {
            Toast.makeText(context, "POWER CONNECTED", Toast.LENGTH_SHORT).show()

            //region Stop service on power connect
            /*val serviceIntent = Intent(context, LifeTimeService::class.java)
            context.stopService(serviceIntent)*/
            //endregion
            prepareDataChunk(context, false)
        }

        if (intent.action == Intent.ACTION_POWER_DISCONNECTED) {
            Toast.makeText(context, "POWER DISCONNECTED", Toast.LENGTH_SHORT).show()

            //region Start service on power disconnect
            val sp = context.getSharedPreferences("Zebra", Context.MODE_PRIVATE)
            val frequency = sp.getInt("frequency", 5)
            val serviceIntent = Intent(context, LifeTimeService::class.java)
            serviceIntent.putExtra("frequency", frequency)
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                ContextCompat.startForegroundService(context, serviceIntent)
            else context.startService(intent)*/

            //endregion
        }
        if (intent.action == Intent.ACTION_BATTERY_LOW) {
            Toast.makeText(context, "BATTERY LOW", Toast.LENGTH_LONG).show()
        }
        if (intent.action == Intent.ACTION_BATTERY_OKAY) {
            Toast.makeText(context, "BATTERY OKAY", Toast.LENGTH_LONG).show()
        }

        //Calling repeatedly
        /*if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
            Toast.makeText(context, "BATTERY CHANGED", Toast.LENGTH_LONG).show()
        }*/
    }
}