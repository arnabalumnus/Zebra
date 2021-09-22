package com.alumnus.zebra.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alumnus.zebra.db.dao.AccLogDao
import com.alumnus.zebra.db.dao.CsvFileLogDao
import com.alumnus.zebra.db.entity.AccLogEntity
import com.alumnus.zebra.db.entity.CsvFileLogEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {

    lateinit var db: AppDatabase
    lateinit var accLogDao: AccLogDao
    lateinit var csvFileLogDao: CsvFileLogDao

    @Before
    fun setUp() {
        val context: Context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        accLogDao = db.accLogDao()
        csvFileLogDao = db.csvFileLogDao()
    }

    @Test
    fun testAccDao() {

        val accLogEntity = AccLogEntity()
        val firstTS = System.currentTimeMillis()
        accLogEntity.ts = firstTS
        accLogEntity.x = 9.81F
        accLogEntity.y = 0.0F
        accLogEntity.z = 0.0F

        runBlocking {
            accLogDao.insert(accLogEntity)                              // 1st insert
            accLogDao.insert(accLogEntity)                              // Replace 1st insert
            accLogEntity.ts = System.currentTimeMillis()
            accLogDao.insert(accLogEntity)                              // 2nd insert
            accLogEntity.ts = System.currentTimeMillis()
            accLogDao.insert(accLogEntity)                              // 3rd insert

            assert(accLogDao.count == 3L)                               // Size 3
            assert(accLogDao.getStartingTimeStamp() == firstTS)

            val lastTs = System.currentTimeMillis()
            accLogEntity.ts = lastTs
            accLogDao.insert(accLogEntity)                              // 4th insert
            assert(accLogDao.getLastRecordTime() == lastTs)

            val dataList = accLogDao.getDataChunk(5)
            assert(dataList.size <= 5)
            assert(dataList.size == 4)                                  // Size 4

            accLogDao.deleteChunk(2)                           // Delete 2
            assert(accLogDao.all.size == 2)                             // Size 2

            accLogDao.deleteAll(System.currentTimeMillis())             // Delete all
            assert(accLogDao.count == 0L)                               // Size 0
        }
    }


    @Test
    fun testCsvDao() {
        runBlocking {
            val csvFileLogEntity: CsvFileLogEntity = CsvFileLogEntity("Dummy file name", 100L)
            csvFileLogDao.insert(csvFileLogEntity)                      // 1st insert
            csvFileLogDao.insert(csvFileLogEntity)                      // Replace 1st insert
            assert(csvFileLogDao.getAll().size == 1)                         // Test size should be 1
            val csvFileLogEntity1: CsvFileLogEntity = CsvFileLogEntity("Dummy file name ${System.currentTimeMillis()}", 100L)
            csvFileLogDao.insert(csvFileLogEntity1)                     // 2nd insert
            assert(csvFileLogDao.csvFileCount == 2)                     // Test size should be 2
            assert(csvFileLogDao.totalRecordOfAllCSVFile == 200L)       // Test total record inserted. Should be 100 + 100 = 200
            assert(csvFileLogDao.oldestCSVFile == "Dummy file name")    // Test oldestCSVFile() method
            csvFileLogDao.deleteCSVRecord("Dummy file name")    // Delete csv file with given filename
            assert(csvFileLogDao.oldestCSVFile != "Dummy file name")    // Verify specified file deletion
            assert(csvFileLogDao.getAll().size == 1)                         // Test size should be 1
            csvFileLogDao.deleteAllCSV()                                // Delete all
            assert(csvFileLogDao.getAll().size == 0)                         // Test size should be 0
        }
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        db.close()
    }
}