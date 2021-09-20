package com.alumnus.zebra.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Formats date and time from millisecond
 */
object DateFormatter {
    /**
     * Note: Contains spacial character. Don't use for filename.
     *
     * @param currentTimeInMilliSec     Current System time in millisecond.
     * @return                          A string that contains date and time with spacial character.
     */
    @JvmStatic
    fun getTimeStamp(currentTimeInMilliSec: Long): String {
        val date = Date(currentTimeInMilliSec)
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        return format.format(date)
    }

    /**
     * Supports for filename. Doesn't contains any spacial character that doesn't support in filename
     *
     * @param currentTimeInMilliSec     Current System time in millisecond.
     * @return                          A string that contains date and time for filename.
     */
    @JvmStatic
    fun getTimeStampFileName(currentTimeInMilliSec: Long): String {
        val date = Date(currentTimeInMilliSec)
        val format = SimpleDateFormat("yyyy, MMM-dd 'Time-'HH mm ss", Locale.getDefault())
        return format.format(date)
    }

    /**
     * Supports for filename. Doesn't contains any spacial character that doesn't support in filename
     *
     * @param currentTimeInMilliSec     Current System time in millisecond.
     * @return                          A string that contains only date for filename.
     */
    fun getDateStampForFileName(currentTimeInMilliSec: Long): String {
        val date = Date(currentTimeInMilliSec)
        val format = SimpleDateFormat("yyyy-MMM-dd", Locale.getDefault())
        return format.format(date)
    }
}