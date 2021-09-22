package com.alumnus.zebra.recoder.machineLearning

import android.content.Context
import android.util.Log
import com.alumnus.zebra.recoder.machineLearning.pojo.TensorFlowModelInput
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

object PredictionManager {

    private const val TAG = "PredictionManager"

    fun isDsEvent(context: Context, dataFrame: TensorFlowModelInput): Float {
        val tfLite: Interpreter = Interpreter(loadModelFile(context = context, modelFileName = "zebra_ds_model.tflite"))
        val inputVal = FloatArray(8)

        inputVal[0] = dataFrame.maxTSV.toFloat()
        inputVal[1] = dataFrame.maxDTSV.toFloat()
        inputVal[2] = dataFrame.avgTSV.toFloat()
        inputVal[3] = dataFrame.avgDTSV.toFloat()
        inputVal[4] = dataFrame.avgSeverity.toFloat()
        inputVal[5] = dataFrame.minTSV.toFloat()
        inputVal[6] = dataFrame.avgMinTSV.toFloat()
        inputVal[7] = dataFrame.avgSpin.toFloat()

        val output = Array(1) { FloatArray(1) }
        tfLite.run(inputVal, output)
        Log.i(TAG, "Desk Slam: ${output[0][0]}")
        return output[0][0]
    }

    fun isFsEvent(context: Context, dataFrame: TensorFlowModelInput): Float {
        val tfLite: Interpreter = Interpreter(loadModelFile(context = context, modelFileName = "zebra_fs_model.tflite"))
        val inputVal = FloatArray(8)

        inputVal[0] = dataFrame.maxTSV.toFloat()
        inputVal[1] = dataFrame.maxDTSV.toFloat()
        inputVal[2] = dataFrame.avgTSV.toFloat()
        inputVal[3] = dataFrame.avgDTSV.toFloat()
        inputVal[4] = dataFrame.avgSeverity.toFloat()
        inputVal[5] = dataFrame.minTSV.toFloat()
        inputVal[6] = dataFrame.avgMinTSV.toFloat()
        inputVal[7] = dataFrame.avgSpin.toFloat()

        val output = Array(1) { FloatArray(1) }
        tfLite.run(inputVal, output)
        Log.i(TAG, "Floor Slam: ${output[0][0]}")
        return output[0][0]
    }

    fun isWsEvent(context: Context, dataFrame: TensorFlowModelInput): Float {
        val tfLite: Interpreter = Interpreter(loadModelFile(context = context, modelFileName = "zebra_ws_model.tflite"))
        val inputVal = FloatArray(8)

        inputVal[0] = dataFrame.maxTSV.toFloat()
        inputVal[1] = dataFrame.maxDTSV.toFloat()
        inputVal[2] = dataFrame.avgTSV.toFloat()
        inputVal[3] = dataFrame.avgDTSV.toFloat()
        inputVal[4] = dataFrame.avgSeverity.toFloat()
        inputVal[5] = dataFrame.minTSV.toFloat()
        inputVal[6] = dataFrame.avgMinTSV.toFloat()
        inputVal[7] = dataFrame.avgSpin.toFloat()

        val output = Array(1) { FloatArray(1) }
        tfLite.run(inputVal, output)
        Log.i(TAG, "Wall Slam: ${output[0][0]}")
        return output[0][0]
    }

    fun isPfEvent(context: Context, dataFrame: TensorFlowModelInput): Float {
        val tfLite: Interpreter = Interpreter(loadModelFile(context = context, modelFileName = "zebra_pf_model.tflite"))
        val inputVal = FloatArray(8)

        inputVal[0] = dataFrame.maxTSV.toFloat()
        inputVal[1] = dataFrame.maxDTSV.toFloat()
        inputVal[2] = dataFrame.avgTSV.toFloat()
        inputVal[3] = dataFrame.avgDTSV.toFloat()
        inputVal[4] = dataFrame.avgSeverity.toFloat()
        inputVal[5] = dataFrame.minTSV.toFloat()
        inputVal[6] = dataFrame.avgMinTSV.toFloat()
        inputVal[7] = dataFrame.avgSpin.toFloat()

        val output = Array(1) { FloatArray(1) }
        tfLite.run(inputVal, output)
        Log.i(TAG, "Fall while put into Pocket: ${output[0][0]}")
        return output[0][0]
    }

    fun isFfEvent(context: Context, dataFrame: TensorFlowModelInput): Float {
        val tfLite: Interpreter = Interpreter(loadModelFile(context = context, modelFileName = "zebra_ff_model.tflite"))
        val inputVal = FloatArray(8)

        inputVal[0] = dataFrame.maxTSV.toFloat()
        inputVal[1] = dataFrame.maxDTSV.toFloat()
        inputVal[2] = dataFrame.avgTSV.toFloat()
        inputVal[3] = dataFrame.avgDTSV.toFloat()
        inputVal[4] = dataFrame.avgSeverity.toFloat()
        inputVal[5] = dataFrame.minTSV.toFloat()
        inputVal[6] = dataFrame.avgMinTSV.toFloat()
        inputVal[7] = dataFrame.avgSpin.toFloat()

        val output = Array(1) { FloatArray(1) }
        tfLite.run(inputVal, output)
        Log.i(TAG, "FreeFall from 3ft: ${output[0][0]}")
        return output[0][0]
    }

    fun isHfEvent(context: Context, dataFrame: TensorFlowModelInput): Float {
        val tfLite: Interpreter = Interpreter(loadModelFile(context = context, modelFileName = "zebra_hf_model.tflite"))
        val inputVal = FloatArray(8)

        inputVal[0] = dataFrame.maxTSV.toFloat()
        inputVal[1] = dataFrame.maxDTSV.toFloat()
        inputVal[2] = dataFrame.avgTSV.toFloat()
        inputVal[3] = dataFrame.avgDTSV.toFloat()
        inputVal[4] = dataFrame.avgSeverity.toFloat()
        inputVal[5] = dataFrame.minTSV.toFloat()
        inputVal[6] = dataFrame.avgMinTSV.toFloat()
        inputVal[7] = dataFrame.avgSpin.toFloat()

        val output = Array(1) { FloatArray(1) }
        tfLite.run(inputVal, output)
        Log.i(TAG, "HandFall: ${output[0][0]}")
        return output[0][0]
    }

    fun isOtEvent(context: Context, dataFrame: TensorFlowModelInput): Float {
        val tfLite: Interpreter = Interpreter(loadModelFile(context = context, modelFileName = "zebra_ot_model.tflite"))
        val inputVal = FloatArray(8)

        inputVal[0] = dataFrame.maxTSV.toFloat()
        inputVal[1] = dataFrame.maxDTSV.toFloat()
        inputVal[2] = dataFrame.avgTSV.toFloat()
        inputVal[3] = dataFrame.avgDTSV.toFloat()
        inputVal[4] = dataFrame.avgSeverity.toFloat()
        inputVal[5] = dataFrame.minTSV.toFloat()
        inputVal[6] = dataFrame.avgMinTSV.toFloat()
        inputVal[7] = dataFrame.avgSpin.toFloat()

        val output = Array(1) { FloatArray(1) }
        tfLite.run(inputVal, output)
        Log.i(TAG, "Forcibly thrown onto the floor: ${output[0][0]}")
        return output[0][0]
    }

    fun isUtEvent(context: Context, dataFrame: TensorFlowModelInput): Float {
        val tfLite: Interpreter = Interpreter(loadModelFile(context = context, modelFileName = "zebra_ut_model.tflite"))
        val inputVal = FloatArray(8)

        inputVal[0] = dataFrame.maxTSV.toFloat()
        inputVal[1] = dataFrame.maxDTSV.toFloat()
        inputVal[2] = dataFrame.avgTSV.toFloat()
        inputVal[3] = dataFrame.avgDTSV.toFloat()
        inputVal[4] = dataFrame.avgSeverity.toFloat()
        inputVal[5] = dataFrame.minTSV.toFloat()
        inputVal[6] = dataFrame.avgMinTSV.toFloat()
        inputVal[7] = dataFrame.avgSpin.toFloat()

        val output = Array(1) { FloatArray(1) }
        tfLite.run(inputVal, output)
        Log.i(TAG, "Thrown underarm from dist 5ft: ${output[0][0]}")
        return output[0][0]
    }


    /**
     * Predict FreeFall event using Neural Network algorithm
     * @param context
     * @param modelInputArray Array of size 600 = (x * 200 + y * 200 + z * 200)
     */
    fun predictFallEventUsingNeuralNetwork(context: Context, modelInputArray: FloatArray): String {
        val tfLite: Interpreter = Interpreter(loadModelFile(context = context, modelFileName = "neural_network_fall_data_model.tflite"))


        val output = Array(1) { FloatArray(8) }
        tfLite.run(modelInputArray, output)
        Log.i(TAG, "HFYP: ${output[0][0]}")
        Log.i(TAG, "PFXX: ${output[0][1]}")
        Log.i(TAG, "FFXP: ${output[0][2]}")
        Log.i(TAG, "FFZM: ${output[0][3]}")
        Log.i(TAG, "HFXP: ${output[0][4]}")
        Log.i(TAG, "FFYP: ${output[0][5]}")
        Log.i(TAG, "OTXX: ${output[0][6]}")
        Log.i(TAG, "UTXX: ${output[0][7]}")
        Log.i(TAG, "=========================================")
        var predictedOutput: String = ""
        var confidence = 0F
        if (output[0][0] > confidence) {
            predictedOutput = "Fall from hand on y axis"
            confidence = output[0][0]
        }
        if (output[0][1] > confidence) {
            predictedOutput = "Fall during put into pocket"
            confidence = output[0][1]
        }
        if (output[0][2] > confidence) {
            predictedOutput = "FreeFall in X axis"
            confidence = output[0][2]
        }
        if (output[0][3] > confidence) {
            predictedOutput = "FreeFall in Z axis"
            confidence = output[0][3]
        }
        if (output[0][4] > confidence) {
            predictedOutput = "Fall from hand on x axis"
            confidence = output[0][4]
        }
        if (output[0][5] > confidence) {
            predictedOutput = "FreeFall in Y axis"
            confidence = output[0][5]
        }
        if (output[0][6] > confidence) {
            predictedOutput = "Over hand throw"
            confidence = output[0][6]
        }
        if (output[0][7] > confidence) {
            predictedOutput = "Under hand throw"
            confidence = output[0][7]
        }
        return "</br>Predicted as <b>$predictedOutput</b> event with confidence: <b>$confidence</b> by Neural Network</br>"
    }


    /**
     * Predict Impact event using Neural Network algorithm
     *
     * @param context
     * @param modelInputArray Array of size 300 = (x * 100 + y * 100 + z * 100)
     */
    fun predictImpactEventUsingNeuralNetwork(context: Context, modelInputArray: FloatArray): String {
        val tfLite: Interpreter = Interpreter(loadModelFile(context = context, modelFileName = "neural_network_impact_data_model.tflite"))

        val output = Array(1) { FloatArray(3) }
        tfLite.run(modelInputArray, output)
        Log.i(TAG, "DS: ${output[0][0]}")
        Log.i(TAG, "FS: ${output[0][1]}")
        Log.i(TAG, "WS: ${output[0][2]}")
        Log.i(TAG, "=========================================")
        var predictedOutput: String = ""
        var confidence = 0F
        if (output[0][0] > confidence) {
            predictedOutput = "Desk Slam"
            confidence = output[0][0]
        }
        if (output[0][1] > confidence) {
            predictedOutput = "Floor Slam"
            confidence = output[0][1]
        }
        if (output[0][2] > confidence) {
            predictedOutput = "Wall Slam"
            confidence = output[0][2]
        }
        return "</br>Predicted as <b>$predictedOutput</b> event with confidence: <b>$confidence</b> by Neural Network</br>"
    }


    /**
     * Load model file from assets folder or ml folder
     *
     * @return MappedByteBuffer
     * @throws IOException If model file does not exist
     */
    @Throws(IOException::class)
    private fun loadModelFile(context: Context, modelFileName: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelFileName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declareLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declareLength)
    }
}