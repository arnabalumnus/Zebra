package com.alumnus.zebra.db.dao

import androidx.room.*
import com.alumnus.zebra.db.entity.AccLogEntity

/**
 * All the required database quires related to `accelerometer_log` table is declared in this Interface
 */
@Dao
interface AccLogDao {
    @get:Query("SELECT * FROM accelerometer_log")
    val all: List<AccLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg accelerometer_log: AccLogEntity)

    @Delete
    fun delete(accelerometer_log: AccLogEntity)

    @get:Query("Select count(*) from accelerometer_log;")
    val count: Long

    @Query("Delete from accelerometer_log where ts <:time_stamp")
    fun deleteAll(time_stamp: Long)

    @Query("SELECT TS FROM accelerometer_log limit 1")
    fun getStartingTimeStamp(): Long

    @Query("Select MAX(TS) from accelerometer_log;")
    suspend fun getLastRecordTime(): Long?
    //region Get data and clear the same from DB
    /**
     * processDataChunk() takes size as params and return List of AccLogEntity
     *
     * @param data_chunk_size Size of List<AccLogEntity> data
     * @return List<AccLogEntity>
     * @usage How to call this
     * AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "database-name").build();
     * List<AccLogEntity> accLogEntities = db.accLogDao().processDataChunk(10);
     */
    //@Synchronized
    @Transaction
    fun processDataChunk(data_chunk_size: Int): List<AccLogEntity> {
        val data = getDataChunk(data_chunk_size)
        deleteChunk(data_chunk_size)
        return data
    }

    //@Synchronized
    @Query("SELECT * FROM accelerometer_log ORDER BY TS ASC limit :chunk_size")
    fun getDataChunk(chunk_size: Int): List<AccLogEntity>

    //@Synchronized
    @Query("DELETE FROM accelerometer_log WHERE id <=(SELECT id FROM accelerometer_log LIMIT 1 OFFSET :chunk_size-1)")
    fun deleteChunk(chunk_size: Int) //endregion
}