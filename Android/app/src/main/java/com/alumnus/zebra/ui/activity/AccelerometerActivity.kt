package com.alumnus.zebra.ui.activity

import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.alumnus.zebra.R
import com.alumnus.zebra.utils.Constant
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate

/**
 * Contains Graph x2
 * 1st graph contains current x,y & z axis values of device accelerometer
 * 2nd graph contains calculated TSV value from x,y & z axis value
 *
 * @author Arnab Kundu
 */
class AccelerometerActivity : AppCompatActivity(), SensorEventListener, OnChartValueSelectedListener {
    private val TAG = this.javaClass.simpleName
    private var mSensorManager: SensorManager? = null
    private var mAccelerometer: Sensor? = null
    private var xyzChart: LineChart? = null
    private var tsvChart: LineChart? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accelerometer)
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        xyzChart = findViewById(R.id.chart_xyz)
        tsvChart = findViewById(R.id.chart_tsv)
        setXYZGraph(xyzChart)
        setTSVGraph(tsvChart)
    }

    override fun onResume() {
        super.onResume()
        mSensorManager!!.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        mSensorManager!!.unregisterListener(this)
    }

    private fun setXYZGraph(chart: LineChart?) {
        chart!!.setOnChartValueSelectedListener(this)

        // enable description text
        chart.description.isEnabled = true
        chart.description.text = "Accelerometer data"

        // enable touch gestures
        chart.setTouchEnabled(true)

        // enable scaling and dragging
        chart.isDragEnabled = true
        chart.setScaleEnabled(true)
        chart.setDrawGridBackground(false)

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(true)

        // set an alternative background color
        chart.setBackgroundColor(Color.LTGRAY)
        val data = LineData()
        data.setValueTextColor(Color.WHITE)

        // add empty data
        chart.data = data

        // get the legend (only possible after setting data)
        val l = chart.legend

        // modify the legend ...
        l.form = Legend.LegendForm.LINE
        //l.setTypeface(tfLight);
        l.textColor = Color.WHITE
        val xl = chart.xAxis
        //xl.setTypeface(tfLight);
        xl.textColor = Color.WHITE
        xl.setDrawGridLines(false)
        xl.setAvoidFirstLastClipping(true)
        xl.isEnabled = true
        val leftAxis = chart.axisLeft
        //leftAxis.setTypeface(tfLight);
        leftAxis.textColor = Color.WHITE
        leftAxis.axisMaximum = 20f
        leftAxis.axisMinimum = -20f
        leftAxis.setDrawGridLines(true)
        val rightAxis = chart.axisRight
        rightAxis.isEnabled = false
    }

    private fun setTSVGraph(chart: LineChart?) {
        chart!!.setOnChartValueSelectedListener(this)

        // enable description text
        chart.description.isEnabled = true
        chart.description.text = "Accelerometer data"

        // enable touch gestures
        chart.setTouchEnabled(true)

        // enable scaling and dragging
        chart.isDragEnabled = true
        chart.setScaleEnabled(true)
        chart.setDrawGridBackground(false)

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(true)

        // set an alternative background color
        chart.setBackgroundColor(Color.LTGRAY)
        val data = LineData()
        data.setValueTextColor(Color.WHITE)

        // add empty data
        chart.data = data

        // get the legend (only possible after setting data)
        val l = chart.legend

        // modify the legend ...
        l.form = Legend.LegendForm.LINE
        //l.setTypeface(tfLight);
        l.textColor = Color.WHITE
        val xl = chart.xAxis
        //xl.setTypeface(tfLight);
        xl.textColor = Color.WHITE
        xl.setDrawGridLines(false)
        xl.setAvoidFirstLastClipping(true)
        xl.isEnabled = true
        val leftAxis = chart.axisLeft
        //leftAxis.setTypeface(tfLight);
        leftAxis.textColor = Color.WHITE
        leftAxis.axisMaximum = 100f
        leftAxis.axisMinimum = 0f
        leftAxis.setDrawGridLines(true)
        val rightAxis = chart.axisRight
        rightAxis.isEnabled = false
    }

    private fun addEntry(x: Float, y: Float, z: Float) {
        val data = xyzChart!!.data
        if (data != null) {
            var setx = data.getDataSetByIndex(0)
            var sety = data.getDataSetByIndex(1)
            var setz = data.getDataSetByIndex(2)
            // set.addEntry(...); // can be called as well
            if (setx == null) {
                setx = createSet("X")
                data.addDataSet(setx)
            }
            if (sety == null) {
                sety = createSet("Y")
                data.addDataSet(sety)
            }
            if (setz == null) {
                setz = createSet("Z")
                data.addDataSet(setz)
            }
            data.addEntry(Entry(setx!!.entryCount.toFloat(), x), 0)
            data.addEntry(Entry(sety!!.entryCount.toFloat(), y), 1)
            data.addEntry(Entry(setz!!.entryCount.toFloat(), z), 2)
            data.notifyDataChanged()

            // let the chart know it's data has changed
            xyzChart!!.notifyDataSetChanged()

            // limit the number of visible entries
            xyzChart!!.setVisibleXRangeMaximum(20f)
            // chart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            xyzChart!!.moveViewToX(data.entryCount.toFloat())

            // this automatically refreshes the chart (calls invalidate())
            // chart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }
    }

    private fun addTSVEntry(tsv: Float) {
        val data = tsvChart!!.data
        if (data != null) {
            var set = data.getDataSetByIndex(0)

            // set.addEntry(...); // can be called as well
            if (set == null) {
                set = createSet("TSV")
                data.addDataSet(set)
            }
            data.addEntry(Entry(set!!.entryCount.toFloat(), tsv), 0)
            data.notifyDataChanged()

            // let the chart know it's data has changed
            tsvChart!!.notifyDataSetChanged()

            // limit the number of visible entries
            tsvChart!!.setVisibleXRangeMaximum(20f)
            // chart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            tsvChart!!.moveViewToX(data.entryCount.toFloat())

            // this automatically refreshes the chart (calls invalidate())
            // chart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }
    }

    private fun createSet(axis: String): LineDataSet? {
        var set: LineDataSet? = null
        if (axis.equals("X", ignoreCase = true)) {
            set = LineDataSet(null, "X Data")
            set.color = ColorTemplate.MATERIAL_COLORS[0]
        }
        if (axis.equals("Y", ignoreCase = true)) {
            set = LineDataSet(null, "Y Data")
            set.color = ColorTemplate.MATERIAL_COLORS[2]
        }
        if (axis.equals("Z", ignoreCase = true)) {
            set = LineDataSet(null, "Z Data")
            set.color = ColorTemplate.MATERIAL_COLORS[3]
        }
        if (axis.equals("TSV", ignoreCase = true)) {
            set = LineDataSet(null, "TSV Data")
            set.color = ColorTemplate.getHoloBlue()
        }
        if (set != null) {
            set.axisDependency = YAxis.AxisDependency.LEFT
            set.setCircleColor(Color.WHITE)
            set.lineWidth = 2f
            set.circleRadius = 4f
            set.fillAlpha = 65
            set.fillColor = ColorTemplate.getHoloBlue()
            set.highLightColor = Color.rgb(244, 117, 117)
            set.valueTextColor = Color.WHITE
            set.valueTextSize = 9f
            set.setDrawValues(false)
        }
        return set
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    override fun onSensorChanged(event: SensorEvent) {
        //Log.d(TAG, "onSensorChanged: Name: "+event.sensor.getName());
        //Log.d(TAG, "onSensorChanged: StringType: "+event.sensor.getStringType());
        //Log.d(TAG, "onSensorChanged: Vendor: "+event.sensor.getVendor());
        //Log.d(TAG, "onSensorChanged: Power: "+event.sensor.getPower());

        //Log.d(TAG, "onSensorChanged: X: " + event.values[0]);
        //Log.d(TAG, "onSensorChanged: Y: " + event.values[1]);
        //Log.d(TAG, "onSensorChanged: Z: " + event.values[2]);
        val G_towards_X = event.values[0]
        val G_towards_Y = event.values[1]
        val G_towards_Z = event.values[2]
        addEntry(G_towards_X, G_towards_Y, G_towards_Z)
        val TSV = Math.sqrt((G_towards_X * G_towards_X + G_towards_Y * G_towards_Y + G_towards_Z * G_towards_Z).toDouble())
        addTSVEntry(TSV.toFloat())
        if (Math.abs(G_towards_X) < Constant.G_CALIBRATION_FACTOR && Math.abs(G_towards_Y) < Constant.G_CALIBRATION_FACTOR && Math.abs(G_towards_Z) < Constant.G_CALIBRATION_FACTOR) {
            Log.d(TAG, "It's a free fall")
        }
        if (Math.abs(G_towards_X) > Constant.THROW_CALIBRATION_FACTOR || Math.abs(G_towards_Y) > Constant.THROW_CALIBRATION_FACTOR || Math.abs(G_towards_Z) > Constant.THROW_CALIBRATION_FACTOR) {
            Log.d(TAG, "It's a throw")
        }
    }

    override fun onValueSelected(e: Entry, h: Highlight) {}
    override fun onNothingSelected() {}
}