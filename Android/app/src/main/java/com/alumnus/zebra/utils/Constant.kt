package com.alumnus.zebra.utils

/**
 * @author Arnab Kundu
 */
object Constant {
    /**
     * G_calibration_factor value need to decrease gradually to be more accurate about a free fall
     */
    const val G_CALIBRATION_FACTOR = 5.89f //value need to decrease gradually to be more accurate about a free fall

    /**
     * throw_calibration_factor value need to increase gradually to be more accurate about a throw
     */
    const val THROW_CALIBRATION_FACTOR = 18.81f //value need to decrease gradually to be more accurate about a free fall
    const val SP = "ZebraSp"
    const val isAutoStartPermissionGranted = "isAutoStartPermissionGranted"

    const val DATABASE_NAME = "ZebraDB"


    /**
     * @DATA_CHUNK_SIZE of 1,30,000 approximately makes 5MB size of .csv file
     * &
     * 43 mints of data @50Hz(50 records/sec)
     *
     * @DATA_CHUNK_SIZE of 20,000 approximately makes 790KB size of .csv file. ** TESTED **
     *
     * 2^17 = 131072
     * for FFT(Fast Fourier Transform) number of row should be power of 2
     */
    const val DATA_CHUNK_SIZE = 131072
    const val RETAIN_NUMBER_OF_CSV_FILE = 100
}