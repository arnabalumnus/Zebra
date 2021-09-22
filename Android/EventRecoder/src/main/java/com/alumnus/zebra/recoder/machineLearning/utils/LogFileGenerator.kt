package com.alumnus.zebra.machineLearning.utils

import android.content.Context
import android.os.Build
import android.os.Environment
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException


/**
 * @author Arnab Kundu
 * TODO use FolderFiles.createLogFile()
 */
object LogFileGenerator {

    /**
     * Create logs folder and write log files
     */
    fun appendLog(context: Context, mFileName: String, text: String) {
        val logFolder: File = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
            File(Environment.getExternalStorageDirectory(), "ZebraApp/logs")
        else
            File(context.getExternalFilesDir("ZebraApp"), "logs")

        if (!logFolder.exists()) {
            try {
                logFolder.mkdirs()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }


        val logFile: File = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
            File(Environment.getExternalStorageDirectory(), "ZebraApp/logs/log-$mFileName.html")
        else
            File(context.getExternalFilesDir("ZebraApp"), "logs/log-$mFileName.html")

        if (!logFile.exists()) {
            try {
                logFile.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            val buf = BufferedWriter(FileWriter(logFile, true))
            buf.append(text)
            buf.newLine()
            buf.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}