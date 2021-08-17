package com.alumnus.zebra.machineLearning

import android.content.Context
import com.alumnus.zebra.machineLearning.pojo.TensorFlowModelInput

/**
 *  Is a wrapper class in PredictionManager for classify in between Impact and FreeFall event
 */
object ClassifiedPredictionManager {

    /**
     * Predict Significant Impact Events here
     *
     * @param context
     * @param dataFrame
     */
    fun predictImpactEvent(context: Context, dataFrame: TensorFlowModelInput): String {

        var predictedConfidence = 0F
        var resultOutput: String = ""

        if (predictedConfidence < PredictionManager.isDsEvent(context, dataFrame)) {
            predictedConfidence = PredictionManager.isDsEvent(context, dataFrame)
            resultOutput = "Desk Slam"
        }
        if (predictedConfidence < PredictionManager.isFsEvent(context, dataFrame)) {
            predictedConfidence = PredictionManager.isFsEvent(context, dataFrame)
            resultOutput = "Floor Slam"
        }
        if (predictedConfidence < PredictionManager.isWsEvent(context, dataFrame)) {
            predictedConfidence = PredictionManager.isWsEvent(context, dataFrame)
            resultOutput = "Wall Slam"
        }
        val predictedOutputForLog: String = "Predicted a <b>$resultOutput</b> with Confidence:<b>${predictedConfidence}</b></br></br>"
        return predictedOutputForLog
    }


    /**
     * Predict Significant FreeFall Events here
     *
     * @param context
     * @param dataFrame
     */
    fun predictFreeFallEvent(context: Context, dataFrame: TensorFlowModelInput): String {
        var predictedConfidence = 0F
        var resultOutput: String = ""

        if (predictedConfidence < PredictionManager.isPpEvent(context, dataFrame)) {
            predictedConfidence = PredictionManager.isPpEvent(context, dataFrame)
            resultOutput = "Desk Slam"
        }
        if (predictedConfidence < PredictionManager.isPfEvent(context, dataFrame)) {
            predictedConfidence = PredictionManager.isPfEvent(context, dataFrame)
            resultOutput = "Floor Slam"
        }
        if (predictedConfidence < PredictionManager.isFfEvent(context, dataFrame)) {
            predictedConfidence = PredictionManager.isFfEvent(context, dataFrame)
            resultOutput = "Wall Slam"
        }
        val predictedOutputForLog: String = "Predicted a <b>$resultOutput</b> with Confidence:<b>${predictedConfidence}</b></br></br>"
        return predictedOutputForLog
    }

}