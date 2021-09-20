package com.alumnus.zebra.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * This class is responsible for creating `accelerometer_log` table in database
 */
@Entity(tableName = "accelerometer_log", indices = [Index(value = ["TS"], unique = true)])
data class AccLogEntity(

        @PrimaryKey(autoGenerate = true)
        var id: Long = 0,

        @ColumnInfo(name = "TS")
        var ts: Long = 0,

        @ColumnInfo(name = "X")
        var x: Float = 0f,

        @ColumnInfo(name = "Y")
        var y: Float = 0f,

        @ColumnInfo(name = "Z")
        var z: Float = 0f
)
