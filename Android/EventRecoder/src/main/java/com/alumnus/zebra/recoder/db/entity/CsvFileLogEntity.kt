package com.alumnus.zebra.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * This class is responsible for creating `csv_file_log` table in database
 */
@Entity(tableName = "csv_file_log", indices = [Index(value = ["file_name"], unique = true)])
data class CsvFileLogEntity(

        @ColumnInfo(name = "file_name")
        var file_name: String,

        @ColumnInfo(name = "count")
        var count: Long,

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        var id: Long = 0
)