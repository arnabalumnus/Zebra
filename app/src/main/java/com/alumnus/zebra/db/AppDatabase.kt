package com.alumnus.zebra.db

import androidx.room.Database
import com.alumnus.zebra.db.entity.AccLogEntity
import com.alumnus.zebra.db.entity.CsvFileLogEntity
import androidx.room.RoomDatabase
import com.alumnus.zebra.db.dao.AccLogDao
import com.alumnus.zebra.db.dao.CsvFileLogDao

/**
 * This is the main class that provides the database instance.
 * All Dao should be mentioned as an abstract function over here
 *
 * @param entities      All tables of database are mentioned in entities array.                 (Mandatory)
 * @param views         We don't have any views in this database, So its an empty array         (Optional)
 * @param version       Its maintain database version. Need to be updated on                    (Mandatory)
 *                      any table changes in final release. Needed migration rule as well
 * @param exportSchema  Its an optional parameter if you want to export database schema as      (Optional)
 *                      a json. By default its false.
 */
@Database(entities = [AccLogEntity::class, CsvFileLogEntity::class], views = [], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun accLogDao(): AccLogDao

    abstract fun csvFileLogDao(): CsvFileLogDao

}