package com.alumnus.zebra.machineLearning.utils

import java.util.*

/**
 * Simpson's rule is one of the numerical methods which is used to evaluate the definite integral.
 * Here it's calculating area under the curve.
 */
object SimpsonsRule {
    /**********************************************************************
     * Standard normal distribution density function.
     * Replace with any sufficiently smooth function.
     */
    fun f(x: Double): Double {
        return Math.exp(-x * x / 2) / Math.sqrt(2 * Math.PI)
    }

    /**********************************************************************
     * Integrate f from a to b using Simpson's rule.
     * Increase N for more precision.
     */
    fun integrate(tsvDataset: ArrayList<Double>, impactStart: Int, impactEnd: Int, step_size: Int): Double {
        val N = impactEnd - impactStart // precision parameter

        // 1/3 terms
        var sum = 1.0 / 3.0 * (tsvDataset[impactStart] + tsvDataset[impactEnd])

        // 4/3 terms
        run {
            var i = 1
            while (i < N - 1) {
                val x = impactStart + step_size * i
                sum += 4.0 / 3.0 * tsvDataset[x]
                i += 2
            }
        }

        // 2/3 terms
        var i = 2
        while (i < N - 1) {
            val x = impactStart + step_size * i
            sum += 2.0 / 3.0 * tsvDataset[x]
            i += 2
        }
        return sum * step_size
    }
}