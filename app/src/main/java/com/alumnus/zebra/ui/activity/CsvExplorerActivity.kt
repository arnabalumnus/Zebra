package com.alumnus.zebra.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alumnus.zebra.BuildConfig
import com.alumnus.zebra.R
import com.alumnus.zebra.machineLearning.DataAnalysis
import com.alumnus.zebra.machineLearning.PredictionManager
import com.alumnus.zebra.machineLearning.pojo.TensorFlowModelInput
import com.alumnus.zebra.machineLearning.utils.Calculator
import com.alumnus.zebra.pojo.AccelerationNumericData
import com.alumnus.zebra.pojo.AccelerationStringData
import com.alumnus.zebra.ui.adapter.AccelerationDataAdapter
import com.alumnus.zebra.utils.CsvFileOperator.readCsvFile
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_csv_explorer.*
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

/**
 * This activity responsible for opening .csv files exported by Zebra app
 *
 * @author Arnab Kundu
 */
class CsvExplorerActivity : AppCompatActivity() {

    private val TAG = javaClass.simpleName
    private var tflite: Interpreter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_csv_explorer)
        val linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rv_acceleration_data.layoutManager = linearLayoutManager
    }

    override fun onResume() {
        super.onResume()
        val uri = intent.data // Get File data from Intent
        val file = File(uri!!.path!!)
        val fileName = file.name // Get File name from uri
        Log.i(TAG, "FileName: $fileName")
        try {
            val inputStream = contentResolver.openInputStream(uri) // Convert received intent data into InputStream.

            // Convert inputStream to ArrayList
            val accelerations: ArrayList<AccelerationStringData> = readCsvFile(inputStream!!)
            //Toast.makeText(this, "Row count: " + accelerations.size, Toast.LENGTH_SHORT).show()

            // Feed adapter with data
            val accelerationDataAdapter = AccelerationDataAdapter(accelerations)
            rv_acceleration_data!!.adapter = accelerationDataAdapter
            if (BuildConfig.DEBUG) {
                //runMachineLearning(accelerations)
                generateLogFile(accelerations)
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    /**
     * Generate corresponding Log file on opening CSV data file
     *
     * @param dataList Array:ist of AccelerationStringData
     */
    private fun generateLogFile(dataList: ArrayList<AccelerationStringData>) {
        /* Acceleration Numeric data collection */
        val accNumericDataList = ArrayList<AccelerationNumericData>()

        //Skip header
        dataList.removeAt(0)
        for ((ts, x, y, z) in dataList) {
            val accNumericData = AccelerationNumericData()
            accNumericData.ts = ts.toLong()
            accNumericData.x = x.toFloat()
            accNumericData.y = y.toFloat()
            accNumericData.z = z.toFloat()
            accNumericDataList.add(accNumericData)
        }
        val result = DataAnalysis().startEventAnalysis(accNumericDataList, this, null)
        runMachineLearning(result)
        Log.i(TAG, "LogFile: $result")
        Toast.makeText(this, "LogFile:\n$result", Toast.LENGTH_LONG).show()
        val data = DoubleArray(accNumericDataList.size * 3)
        var count = 0
        while (count < data.size) {
            data[count] = accNumericDataList[count++ / 3].x.toDouble()
            data[count] = accNumericDataList[count++ / 3].y.toDouble()
            data[count] = accNumericDataList[count++ / 3].z.toDouble()
        }
        /*
        val predictedFallResult = RandomForestClassifier.predict(data)
        val predictedImpactResult = RFClassifierForImpactData.predict(data)
        Log.i(TAG, "Predicted:")
        Log.i(TAG, "Fall Result: $predictedFallResult")
        Log.i(TAG, "Impact Result: $predictedImpactResult")
        Log.i(TAG, "=======================================================================================")
        Toast.makeText(this, "$result\nPredicted\nFall Result: $predictedFallResult\nImpact Result: $predictedImpactResult", Toast.LENGTH_SHORT).show()
        */
    }


    private fun runMachineLearning(dataFrame: TensorFlowModelInput) {
        try {
            var predictedConfidence = 0F
            var resultOutput = ""
            if (predictedConfidence < PredictionManager.isDsEvent(this, dataFrame)) {
                predictedConfidence = PredictionManager.isDsEvent(this, dataFrame)
                resultOutput = "Desk Slam"
            }
            if (predictedConfidence < PredictionManager.isFsEvent(this, dataFrame)) {
                predictedConfidence = PredictionManager.isFsEvent(this, dataFrame)
                resultOutput = "Floor Slam"
            }
            if (predictedConfidence < PredictionManager.isWsEvent(this, dataFrame)) {
                predictedConfidence = PredictionManager.isWsEvent(this, dataFrame)
                resultOutput = "Wall Slam"
            }

            Snackbar.make(findViewById(R.id.rv_acceleration_data), resultOutput, Snackbar.LENGTH_INDEFINITE).setAction("OK", {}).show()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }


    /**
     * Load model file from assets folder or ml folder
     *
     * @return MappedByteBuffer
     * @throws IOException If model file does not exist
     */
    @Throws(IOException::class)
    private fun loadModelFile(): MappedByteBuffer {
        val fileDescriptor = this.assets.openFd("zebra_ws_model.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declareLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declareLength)
    }

    /**
     * Predict Y value for corresponding given X values
     *
     * @return Y value
     */
    private fun predictDataSet(dataFrame: TensorFlowModelInput): String {
        val inputVal = FloatArray(8)

        inputVal[0] = dataFrame.maxTSV.toFloat()
        inputVal[1] = dataFrame.maxDTSV.toFloat()
        inputVal[2] = dataFrame.avgTSV.toFloat()
        inputVal[3] = dataFrame.avgDTSV.toFloat()
        inputVal[4] = dataFrame.avgSeverity.toFloat()
        inputVal[5] = dataFrame.minTSV.toFloat()
        inputVal[6] = dataFrame.avgMinTSV.toFloat()
        inputVal[7] = dataFrame.avgSpin.toFloat()

        Log.d(TAG, "DataSet: [${inputVal[0]}, ${inputVal[1]}, ${inputVal[2]}, ${inputVal[3]}, ${inputVal[4]}, ${inputVal[5]}, ${inputVal[6]}, ${inputVal[7]}}] ")

        val output = Array(1) { FloatArray(1) }
        tflite!!.run(inputVal, output)
        Log.e(TAG, "Predicted Output: ${output[0][0]}\n\n")
        if (output[0][0] > 0.5F)
            return "Throw!! Model Output:${output[0][0]} \nDataSet: [${inputVal[0]}, Force:${inputVal[1]}]"
        else if (output[0][0] < 0.5F)
            return "FreeFall!! Model Output:${output[0][0]} \nDataSet: [${inputVal[0]}, Force:${inputVal[1]}]"
        else
            return "No major event detected"
    }

    private fun calculation(dataList: ArrayList<AccelerationStringData>): Array<Int> {
        /* Acceleration Numeric data collection */
        val tsvList = ArrayList<Double>()
        val accNumericData = AccelerationNumericData()

        //Skip header
        dataList.removeAt(0)
        for ((ts, x, y, z) in dataList) {
            accNumericData.ts = ts.toLong()
            accNumericData.x = x.toFloat()
            accNumericData.y = y.toFloat()
            accNumericData.z = z.toFloat()
            tsvList.add(Calculator.calculateTSV(x = accNumericData.x.toDouble(), y = accNumericData.y.toDouble(), z = accNumericData.z.toDouble()))
        }
        Log.d(TAG, "Max G: ${tsvList.max()}")
        Log.d(TAG, "Min G: ${tsvList.min()}")
        var extram_force_applied = 0
        var free_fall_count = 0
        for (g in tsvList) {
            if (g < 1)
                free_fall_count += 1
            if (g > 25)
                extram_force_applied += 1
        }

        return arrayOf(tsvList.max()!!.toInt(), tsvList.min()!!.toInt())
    }
}