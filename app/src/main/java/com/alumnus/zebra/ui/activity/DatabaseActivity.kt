package com.alumnus.zebra.ui.activity

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.alumnus.zebra.R
import com.alumnus.zebra.db.AppDatabase
import com.alumnus.zebra.db.entity.CsvFileLogEntity
import com.alumnus.zebra.utils.Constant
import com.alumnus.zebra.utils.Constant.DATABASE_NAME
import com.alumnus.zebra.utils.DateFormatter
import com.alumnus.zebra.utils.EncryptManager
import com.alumnus.zebra.utils.ZipManager
import kotlinx.coroutines.*
import java.io.File

/**
 * Contains info about available data in Database accLog table
 * &
 * Exported .csv files available in storage @ZebraApp folder
 *
 * @author Arnab Kundu
 */
class DatabaseActivity : AppCompatActivity() {

    //private val TAG = javaClass.simpleName

    var tv_db_record_count_event_table: TextView? = null
    var tv_db_record_count_acc_table: TextView? = null
    var tv_csv_list_table: TextView? = null
    private var db: AppDatabase? = null

    lateinit var context: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        setContentView(R.layout.activity_database)
        tv_db_record_count_event_table = findViewById(R.id.tv_db_record_count_event_table)
        tv_db_record_count_acc_table = findViewById(R.id.tv_db_record_count_acc_table)
        tv_csv_list_table = findViewById(R.id.tv_csv_list_table)
    }

    override fun onResume() {
        super.onResume()
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, DATABASE_NAME).build()
    }

    fun getTotalCount(view: View) {
        CoroutineScope(Dispatchers.Main).launch {
            val deferredLong: Deferred<Long> = CoroutineScope(Dispatchers.IO).async {
                getRowCountOfAccelerometerTable()
            }
            val rowCountLong = deferredLong.await()
            tv_db_record_count_acc_table!!.text = "Accelerometer table row count: $rowCountLong"
        }
    }

    fun getLastTimeStamp(v: View) {
        CoroutineScope(Dispatchers.Main).launch {
            val deferredString: Deferred<String> = CoroutineScope(Dispatchers.IO).async {
                fetchTimeStampDBTask()
            }
            val timeStampString = deferredString.await()
            tv_db_record_count_event_table!!.text = timeStampString
        }
    }

    fun archive(view: View) {
        CoroutineScope(Dispatchers.IO).launch {
            deleteAfterZippingFiles()
        }
    }

    fun getCsvListTable(view: View) {
        CoroutineScope(Dispatchers.Main).launch {
            val deferredString: Deferred<String> = CoroutineScope(Dispatchers.IO).async {
                getRecordOfCsvFilesAvailableInSdCardFromDatabase()
            }
            val csvFileListString = deferredString.await()
            tv_csv_list_table!!.text = csvFileListString
        }
        Toast.makeText(this, "Chunk size: ${Constant.DATA_CHUNK_SIZE} \nRetain files count: ${Constant.RETAIN_NUMBER_OF_CSV_FILE}", Toast.LENGTH_SHORT).show()
    }

    /**
     * Suspended function. Must be running in background Thread always.
     * @param listOfCsv ArrayList of [CsvFileLogEntity]
     */
    private fun zipAllCSVFiles(listOfCsv: List<CsvFileLogEntity>) {
        val s = Array<String>(listOfCsv.size) { "it = $it" }
        val filePath = "/storage/emulated/0/ZebraApp/csvData/" //TODO Android R filePath
        for (row in listOfCsv.indices) {
            s[row] = filePath + listOfCsv[row].file_name + ".csv"
        }

        // Check recoded file in Database is actually available in SD card or not.
        var isFilesAvailableForZipping = false
        for (stringFilePath in s) {
            val f = File(stringFilePath)
            if (f.exists()) {
                isFilesAvailableForZipping = true
                break
            }
        }
        var zipFilePath = ""
        // Zip and delete available csvFiles
        if (isFilesAvailableForZipping) {
            val zipManager = ZipManager()
            zipFilePath = zipManager.zip(s)
            for (stringFilePath in s) {
                val f = File(stringFilePath)
                f.delete()
            }
        }

        // Delete csvFiles record from DB
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, DATABASE_NAME).build()
        db!!.csvFileLogDao().deleteAllCSV()
        if (zipFilePath.isNotEmpty())
            EncryptManager.saveEncryptedFile(this, zipFilePath)
    }


    /**
     * Get Accelerometer Table row count from RoomDB
     * @return Long
     */
    private suspend fun getRowCountOfAccelerometerTable(): Long {
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, DATABASE_NAME).build()
        return db!!.accLogDao().count
    }


    /**
     * Get Last inserted row TimeStamp of Accelerometer Table
     * @return String
     */
    private suspend fun fetchTimeStampDBTask(): String {
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, DATABASE_NAME).build()
        val startingTimeStamp = db!!.accLogDao().getStartingTimeStamp()
        val lastRecordTime: Long = db!!.accLogDao().getLastRecordTime() ?: 0
        return "Starting timestamp: ${DateFormatter.getTimeStamp(startingTimeStamp)}\nLast timestamp: ${DateFormatter.getTimeStamp(lastRecordTime)}"
    }


    /**
     * Get list of csv file of Accelerometer data in a TABLE format
     * @return String
     */
    private suspend fun getRecordOfCsvFilesAvailableInSdCardFromDatabase(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("| FileName                                  |  rows |\n")
        stringBuilder.append("|-----------------------------------------------|----------|\n")
        val db: AppDatabase = Room.databaseBuilder(applicationContext, AppDatabase::class.java, DATABASE_NAME).build()
        val listOfCsv = db.csvFileLogDao().getAll()
        for (row in listOfCsv.indices) {
            stringBuilder.append(String.format("| %20s | %5s |\n", listOfCsv[row].file_name, listOfCsv[row].count))
        }
        println(stringBuilder.toString())
        return stringBuilder.toString()
    }


    /**
     * Zip those csv file and delete to clear storage
     */
    private suspend fun deleteAfterZippingFiles() {
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, DATABASE_NAME).build()
        val listOfCsv = db!!.csvFileLogDao().getAll()
        if (listOfCsv.isNotEmpty()) {             // Avoid creating 0 byte Zip file, When no csvFile available.
            /** Zip CSV files and delete the original .csv files  */
            zipAllCSVFiles(listOfCsv)
        }
    }

}