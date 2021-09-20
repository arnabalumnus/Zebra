package com.alumnus.zebra.recoder.machineLearning.pojo

data class TensorFlowModelInput(

        // region ImpactEvent variables
        /* val impactEventCount: Int, */
        /* val impactEventDuration: Double, */
        val maxTSV: Double,
        val avgTSV: Double,
        val maxDTSV: Double,
        val avgDTSV: Double,
        val avgSeverity: Double, // Severity value calculated in a scale of ZERO to FIVE (0 to 5)
        //endregion

        // region FreeFallEvent variables
        /*val FreeFallEventCount: Int,*/
        val minTSV: Double,
        val avgMinTSV: Double,
        val avgSpin: Double //  ∑(isSpinDetected) / FreeFallEventCount
        //endregion
)