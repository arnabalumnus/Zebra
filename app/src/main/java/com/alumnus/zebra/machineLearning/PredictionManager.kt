package com.alumnus.zebra.machineLearning

import android.content.Context
import android.util.Log
import com.alumnus.zebra.machineLearning.pojo.TensorFlowModelInput
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
        Log.i(TAG, "HeavyFreeFall: ${output[0][0]}")
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