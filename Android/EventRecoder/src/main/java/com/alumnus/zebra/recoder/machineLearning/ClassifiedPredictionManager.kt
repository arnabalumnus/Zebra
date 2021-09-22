package com.alumnus.zebra.recoder.machineLearning

import android.content.Context
import com.alumnus.zebra.recoder.machineLearning.pojo.TensorFlowModelInput

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
        val predictedOutputForLog: String =
            "</br>Predicted a <b>$resultOutput</b> with Confidence:<b>${predictedConfidence}</b> by Logistic Regression</br>"
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

        if (predictedConfidence < PredictionManager.isPfEvent(context, dataFrame)) {
            predictedConfidence = PredictionManager.isPfEvent(context, dataFrame)
            resultOutput = "Fall during Put into pocket"
        }
        if (predictedConfidence < PredictionManager.isFfEvent(context, dataFrame)) {
            predictedConfidence = PredictionManager.isFfEvent(context, dataFrame)
            resultOutput = "FreeFall"
        }
        if (predictedConfidence < PredictionManager.isHfEvent(context, dataFrame)) {
            predictedConfidence = PredictionManager.isHfEvent(context, dataFrame)
            resultOutput = "Dropped from hand"
        }
        if (predictedConfidence < PredictionManager.isOtEvent(context, dataFrame)) {
            predictedConfidence = PredictionManager.isOtEvent(context, dataFrame)
            resultOutput = "Forcibly thrown onto the floor"
        }
        if (predictedConfidence < PredictionManager.isUtEvent(context, dataFrame)) {
            predictedConfidence = PredictionManager.isUtEvent(context, dataFrame)
            resultOutput = "Thrown underarm from dist 5ft"
        }
        val predictedOutputForLog: String =
            "</br>Predicted a <b>$resultOutput</b> with Confidence:<b>${predictedConfidence}</b> by Logistic Regression</br>"
        return predictedOutputForLog
    }

}