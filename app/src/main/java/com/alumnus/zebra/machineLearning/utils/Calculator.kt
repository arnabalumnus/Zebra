package com.alumnus.zebra.machineLearning.utils


/**
 * @author Arnab Kundu
 */
object Calculator {


    /**
     * Calculates TSV from X-Y-Z axis data of accelerometer
     *
     * @param x     X-Axis value of accelerometer's data
     * @param y     Y-Axis value of accelerometer's data
     * @param z     Z-Axis value of accelerometer's data
     * @return      TSV stands for SquareRootOf  _______________
     *                                         √( X² + Y² + Z² )
     */
    fun calculateTSV(x: Double, y: Double, z: Double): Double {
        return Math.sqrt(x * x + y * y + z * z)
    }


    /**
     * Calculates dTSV from two consecutive TSV value
     * @param tsv   A TSV value
     * @param tsv1  Next consecutive TSV value with respect to TimeStamp
     * @return dTSV stand for delta TSV
     */
    fun calculateDTSV(tsv: Double, tsv1: Double): Double {
        return tsv1 - tsv
    }


    /**
     * Estimate distance of a free fall event
     * s = ut + ½gt²
     * 1 meter = 3.28 ft
     * TODO information unknown for the fraction 1.225
     *  Copied from python code. Anyway this method's output is OK
     * @param duration      Free fall time in millisecond
     * @return              Distance(in ft) covered in the that duration of free fall
     */
    fun estimateDistance(duration: Double): Double {
        return Math.round((3.28 * (9.81 * (duration / 1000) * (duration / 1000)) / 2) * 1.225).toDouble()
    }
}