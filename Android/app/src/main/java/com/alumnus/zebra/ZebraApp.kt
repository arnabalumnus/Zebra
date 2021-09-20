package com.alumnus.zebra

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.alumnus.zebra.broadcastReceiver.BootReceiver

/**
 * This is the application class.
 * It launch before the Launcher Activity.
 * And its become alive as long as app keep alive.
 * When app gets killed by user this class also get killed.
 *
 * @author Arnab Kundu
 */
class ZebraApp : Application() {

    override fun onCreate() {
        super.onCreate()
        /*PowerConnectionReceiver receiver = new PowerConnectionReceiver();

        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        iFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        iFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        iFilter.addAction(Intent.ACTION_BATTERY_LOW);
        iFilter.addAction(Intent.ACTION_BATTERY_OKAY);
        registerReceiver(receiver, iFilter);*/
        val bootReceiver = BootReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_BOOT_COMPLETED)
        intentFilter.addAction(Intent.ACTION_REBOOT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intentFilter.addAction(Intent.ACTION_LOCKED_BOOT_COMPLETED)
        }
        registerReceiver(bootReceiver, intentFilter)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                    CHANNEL_ID,
                    "Example Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    companion object {
        const val CHANNEL_ID = "EventTrackingServiceChannel"
    }
}