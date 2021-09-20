package com.alumnus.zebra.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alumnus.zebra.db.entity.CsvFileLogEntity

/**
 * All the required database quires related to `csv_file_log` table is declared in this Interface
 */
@Dao
interface CsvFileLogDao {
    @Query("SELECT * FROM csv_file_log")
    suspend fun getAll(): List<CsvFileLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg csvFileLogs: CsvFileLogEntity)

    @Query("DELETE FROM csv_file_log WHERE file_name=:file_name")
    fun deleteCSVRecord(file_name: String)

    @Query("DELETE FROM csv_file_log WHERE 1=1")
    fun deleteAllCSV()

    @get:Query("SELECT file_name FROM csv_file_log ORDER BY id ASC LIMIT 1")
    val oldestCSVFile: String

    @get:Query("SELECT SUM(count) as total_records FROM csv_file_log")
    val totalRecordOfAllCSVFile: Long

    @get:Query("SELECT count(*) FROM csv_file_log")
    val csvFileCount: Int
}