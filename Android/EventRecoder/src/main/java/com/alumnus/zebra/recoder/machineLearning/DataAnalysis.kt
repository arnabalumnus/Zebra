package com.alumnus.zebra.recoder.machineLearning

import android.content.Context
import android.util.Log
import com.alumnus.zebra.machineLearning.utils.Calculator
import com.alumnus.zebra.machineLearning.utils.Calculator.estimateDistance
import com.alumnus.zebra.machineLearning.utils.LogFileGenerator.appendLog
import com.alumnus.zebra.machineLearning.utils.SimpsonsRule
import com.alumnus.zebra.recoder.machineLearning.enums.EventType
import com.alumnus.zebra.recoder.machineLearning.pojo.DetectedEvent
import com.alumnus.zebra.recoder.machineLearning.pojo.EventNoisePair
import com.alumnus.zebra.recoder.machineLearning.pojo.NoiseZone
import com.alumnus.zebra.recoder.machineLearning.pojo.TensorFlowModelInput
import com.alumnus.zebra.recoder.pojo.AccelerationNumericData
import com.alumnus.zebra.recoder.utils.Constant
import com.alumnus.zebra.recoder.utils.CsvFileOperator
import com.alumnus.zebra.recoder.utils.DateFormatter
import com.alumnus.zebra.recoder.utils.FolderFiles


//var prefallData TODO convert from python code

/**
 * This class does whole data analysis part on top of accelerometer data
 * and Generate logs for respective data.
 *
 * @author Converted code from pre-existing Python script
 */
class DataAnalysis {

    //region Constants
    val EVENT_IMPACT = 1
    val EVENT_FREEFALL = 2
    val TYPE_UNKNOWN = 0
    val TYPE_FREEFALL_SIGNIFICANT = 1
    val TYPE_FREEFALL_INSIGNIFICANT = 2
    val TYPE_IMPACT_NEGLIGIBLE = 1
    val TYPE_IMPACT_SOFT = 2
    val TYPE_IMPACT_MEDIUM = 3
    val TYPE_IMPACT_HARD = 4
    val TYPE_IMPACT_FORCE = 5
    val TSV_IMPACT = 24.5
    val TSV_FREEFALL = 5.8
    val TSV_CALMZONE_HIGH = 11
    val TSV_CALMZONE_MEDIUM = 10
    val TSV_CALMZONE_LOW = 8
    val TSV_FREEFALL_SPIN = 4
    val DTSV_IMPACT_LOW = 10
    val DTSV_IMPACT_MEDIUM = 14.7
    val DTSV_IMPACT_HIGH = 20
    val interpolateFreq = 10
    val resampleFactor = 2
    val ZONE_PREFALL = 1
    val ZONE_FREEFALL = 2
    val ZONE_IMPACT = 3
    val ZONE_NOISE = 4
    val ZONE_CALM = 0
    val PREFALL_LENGTH = 200
    val PREIMPACT_LENGTH = 100
    val FREEFALL_SIGNIFICANT = 200  // UNIT in MILLISECOND
    val CALMZONE_DURATION = 30
    val IMPACT_LENGTH_MIN = 60
    val FORCE_AREA_MIN = 600
    val EVENT_GAP_MIN = 30

    //endregion

    private lateinit var mFileName: String
    private lateinit var context: Context
    private lateinit var xyzList: ArrayList<AccelerationNumericData>
    private lateinit var TSV: ArrayList<Double>
    private lateinit var DTSV: ArrayList<Double>

    /**
     * Starts the long process of event analysis.
     * 1. Calculate TSV (Total Sum Vector)
     * @see Calculator.calculateTSV
     * 2. Calculate DTSV (Deference between constitutive two TSV value)
     * @see Calculator.calculateDTSV
     * 3. Performs data analysis with TSV and dTSV data sets for event detection
     * @see finalizeDetection
     *
     * @param xyzList       List of xyz axis value of accelerometer
     * @param context       Context contains android background information. Needed to create file.
     * @param fileName      File will generate with provided filename
     * @return              finalizeDetection()
     */
    fun startEventAnalysis(xyzList: ArrayList<AccelerationNumericData>, context: Context, fileName: String? = DateFormatter.getTimeStampFileName(System.currentTimeMillis())): TensorFlowModelInput {
        val tsList = ArrayList<Long>()
        val tsvList = ArrayList<Double>()
        val dtsvList = ArrayList<Double>()
        for (xyz in xyzList) {
            tsList.add(xyz.ts)
            tsvList.add(Calculator.calculateTSV(x = xyz.x.toDouble(), y = xyz.y.toDouble(), z = xyz.z.toDouble()))
        }
        dtsvList.add(0.0)
        for (i in 1 until tsvList.size) {
            dtsvList.add(Calculator.calculateDTSV(tsv = tsvList[i - 1], tsv1 = tsvList[i]))
        }
        this.context = context
        if (fileName == null)
            mFileName = DateFormatter.getTimeStampFileName(System.currentTimeMillis())
        else
            mFileName = fileName
        this.xyzList = xyzList
        this.TSV = tsvList
        this.DTSV = dtsvList
        return finalizeDetection(tsList, tsvList, dtsvList)
    }


    /**
     * Detect Events and Noise on provided data set and Returns combine object of Event & Noise
     *
     * @param ts            List of Timestamp
     * @param tsvDataSet    List of TSV data set
     * @param dtsvDataSet   List of DTSV data set
     * @return Event & Noise object
     */
    private fun detectEvents(ts: ArrayList<Long>, tsvDataSet: ArrayList<Double>, dtsvDataSet: ArrayList<Double>): EventNoisePair {
        val noiseZones: ArrayList<NoiseZone> = ArrayList()
        val detectedEvents = arrayListOf<DetectedEvent>()
        val numberOfSamples = ts.size

        var maxTsv = -1.0
        var minTsv = -1.0
        var freefallStart = -1
        var impactStart = -1
        var noiseStart = -1
        var calmZoneCount = 0
        var spinDetected: Boolean
        var impactType: Int
        var maxDtsv: Double
        var areaUnderCurve: Double
        appendLog(context, mFileName, "<html><head><title>Zebra event log</title><link rel=\"icon\" type=\"image/png\" href=\"YOUR_ICON_URL\"/></head><body>")
        println("-------------------------------------------------------------------------------------------------------------------------")
        for (i in 0 until numberOfSamples) {
            val currentTsv = tsvDataSet[i]
            // Update max / min TSV values if required
            if (maxTsv >= 0) {
                if (maxTsv < currentTsv) {
                    maxTsv = currentTsv
                }
            } else if (minTsv >= 0) {
                if (minTsv > currentTsv) {
                    minTsv = currentTsv
                }
            }
            if (currentTsv > TSV_IMPACT) {
                // Impact zone
                if (noiseStart >= 0) {
                    calmZoneCount = 0
                }
                // Finalize stuff if this marks the end of a freefall event
                if (freefallStart > 0) {
                    spinDetected = detectSpin(tsvDataSet, freefallStart, i)
                    detectedEvents.add(DetectedEvent(EVENT_FREEFALL, freefallStart, i, minTsv, spinDetected))
                    freefallStart = -1
                    minTsv = -1.0
                }
                // Initialize stuff if this is the start of impact
                if (impactStart < 0) {
                    impactStart = i
                    maxTsv = currentTsv
                }
            } else if (currentTsv < TSV_FREEFALL) {
                // Freefall zone
                if (noiseStart >= 0) {
                    calmZoneCount = 0
                }
                // Finalize stuff if this marks the end of an impact event
                if (impactStart > 0) {
                    // Look at DTSV to determine type
                    var maxDtsv = -1.0
                    for (j in impactStart until i) {
                        if (maxDtsv < dtsvDataSet[j]) {
                            maxDtsv = dtsvDataSet[j]
                        }
                    }
                    if (maxDtsv >= DTSV_IMPACT_HIGH) {
                        // If the DTSV is too highm it is an impact
                        impactType = TYPE_IMPACT_HARD
                    } else {
                        // Otherwise, it might be force impartion or impact
                        //areaUnderCurve = simps(tsvDataset[impactStart:i], dx = timeDiffInMs)
                        areaUnderCurve = SimpsonsRule.integrate(tsvDataSet, impactStart, i, 1)
                        appendLog(context, mFileName, "Area under curve:" + areaUnderCurve)
                        println("Area under curve:" + areaUnderCurve)
                        if (areaUnderCurve >= FORCE_AREA_MIN) {
                            // Not actually an impact, just external application of force
                            impactType = TYPE_IMPACT_FORCE
                        } else {
                            // Impact it is
                            if (maxDtsv >= DTSV_IMPACT_MEDIUM) {
                                impactType = TYPE_IMPACT_MEDIUM
                            } else if (maxDtsv >= DTSV_IMPACT_LOW) {
                                impactType = TYPE_IMPACT_SOFT
                            } else {
                                impactType = TYPE_IMPACT_NEGLIGIBLE
                            }
                        }
                    }
                    detectedEvents.add(DetectedEvent(EVENT_IMPACT, impactStart, i, maxTsv, maxDtsv, impactType))

                    // Noise filtering to be done only for high and medium impacts
                    if ((impactType == TYPE_IMPACT_HARD) or (impactType == TYPE_IMPACT_MEDIUM)) {
                        // End of impact, detect noise zone
                        if (noiseStart < 0) {
                            noiseStart = i
                        }
                        calmZoneCount = 0
                    }
                    impactStart = -1
                    maxTsv = -1.0
                }
                // Initialize stuff if this is the start of freefall
                if (freefallStart < 0) {
                    freefallStart = i
                    minTsv = currentTsv
                }
            } else {
                // Regular zone
                // Check for end of noise
                if (noiseStart >= 0) {
                    if ((currentTsv <= TSV_CALMZONE_HIGH) and (currentTsv >= TSV_CALMZONE_LOW)) {
                        calmZoneCount += 1
                        if (calmZoneCount >= CALMZONE_DURATION) {
                            // We have calmed down enough
                            noiseZones.add(NoiseZone(noiseStart, i))
                            noiseStart = -1
                            calmZoneCount = 0
                        }
                    } else {
                        // Still some noise
                        calmZoneCount = 0
                    }
                }
                // Finalize stuff if this marks the end of an impact event
                if (impactStart > 0) {
                    // Look at DTSV to determine type
                    maxDtsv = -1.0
                    for (j in impactStart until i) {
                        if (maxDtsv < dtsvDataSet[j]) {
                            maxDtsv = dtsvDataSet[j]
                        }
                    }
                    if (maxDtsv >= DTSV_IMPACT_HIGH) {
                        // If the DTSV is too high, it is an impact
                        impactType = TYPE_IMPACT_HARD
                    } else {
                        //areaUnderCurve = simps(tsvDataset[impactStart:i], dx = timeDiffInMs)
                        areaUnderCurve = SimpsonsRule.integrate(tsvDataSet, impactStart, i, 1)

                        appendLog(context, mFileName, "Area under curve: " + areaUnderCurve)
                        println("Area under curve: " + areaUnderCurve)
                        if (areaUnderCurve >= FORCE_AREA_MIN) {
                            // Not actually an impact, just external application of force
                            impactType = TYPE_IMPACT_FORCE
                        } else {
                            // Impact it is
                            if (maxDtsv >= DTSV_IMPACT_MEDIUM) {
                                impactType = TYPE_IMPACT_MEDIUM
                            } else if (maxDtsv >= DTSV_IMPACT_LOW) {
                                impactType = TYPE_IMPACT_SOFT
                            } else {
                                impactType = TYPE_IMPACT_NEGLIGIBLE
                            }
                        }
                    }
                    detectedEvents.add(DetectedEvent(EVENT_IMPACT, impactStart, i, maxTsv, maxDtsv, impactType))

                    // Noise filtering to be done only for high and medium impacts
                    if ((impactType == TYPE_IMPACT_HARD) or (impactType == TYPE_IMPACT_MEDIUM)) {
                        // End of impact, detect noise zone
                        if (noiseStart < 0) {
                            noiseStart = i
                        }
                        calmZoneCount = 0
                    }
                    impactStart = -1
                    maxTsv = -1.0
                }
                // Finalize stuff if this marks the end of a freefall event
                if (freefallStart > 0) {
                    spinDetected = detectSpin(tsvDataSet, freefallStart, i)
                    detectedEvents.add(DetectedEvent(EVENT_FREEFALL, freefallStart, i, minTsv, spinDetected))
                    freefallStart = -1
                    minTsv = -1.0
                }
            }
        }
        for (i in 0 until detectedEvents.size) {
            println(detectedEvents[i].toString())
        }
        return EventNoisePair(detectedEvents, noiseZones)
    }


    /**
     *  Detect whether device spinning at the point of impact event
     *
     *  @param tsvDataSet   List of TSV data set
     *  @param start        Free fall start index
     *  @param end          Free fall end index
     *  @return             True if spin detected, False otherwise
     */
    private fun detectSpin(tsvDataSet: ArrayList<Double>, start: Int, end: Int): Boolean {
        var detected = false
        for (i in start until end) {
            if (tsvDataSet[i] >= TSV_FREEFALL_SPIN) {
                detected = true
                break
            }
        }
        return detected
    }


    /**
     * Collate all events and noise zones
     *
     * @param ts            List of Timestamp
     * @param tsvDataSet    List of TSV data set
     * @param dtsvDataSet   List of dTSV data set
     *
     * @return              A string message that contains
     *                      1. Significant free fall events:
     *                      2. Significant impact events:
     *                      3. Force impartions: "
     */
    private fun finalizeDetection(ts: ArrayList<Long>, tsvDataSet: ArrayList<Double>, dtsvDataSet: ArrayList<Double>): TensorFlowModelInput {

        val eventNoisePair: EventNoisePair = detectEvents(ts, tsvDataSet, dtsvDataSet)
        val events: ArrayList<DetectedEvent> = eventNoisePair.detectedEvents
        //Log.e("DataAnalysis", "TensorFlowInputs: \n${prepareTFLiteModelInput(events).toString()}")
        val noises: ArrayList<NoiseZone> = eventNoisePair.noiseZones
        appendLog(context, mFileName, "Detected events:")
        println("Detected events:")
        parseEvents(eventNoisePair.detectedEvents, ts)

        appendLog(context, mFileName, "Noise zones:")
        println("Noise zones:")
        for (noiseZone in noises) {
            appendLog(context, mFileName, noiseZone.toString())
            println(noiseZone)
        }

        //else:
        //filteredEvents = events

        // 6.Find type of abuse
        // 6.1.Find first significant fall event

        // Only use significant events
        var numberOfSignificantFalls = 0
        var numberOfSignificantImpacts = 0
        var numberOfForces = 0
        var firstFall = 1
        var prefallFound = false
        var preimpactFound = false

        var preimpactData = arrayListOf<Int>()
        var prefallDataTs = arrayListOf<Long>()
        var preimpactDataTs = arrayListOf<Int>()
        var lastEventEnded = 0

        var freefallData = IntArray(ts.size)//arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0) //[0] * numberOfResampledSamples
        var impactData = IntArray(ts.size)//arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0) //[0] * numberOfResampledSamples

        for (event in events) {
            if ((event.event_type == EVENT_FREEFALL) and ((ts[event.eventEnd] - ts[event.eventStart]) >= FREEFALL_SIGNIFICANT)) {
                if (firstFall > 0) {
                    prefallFound = true
                    // prefallData = [Math.max((event.eventStart - PREFALL_LENGTH), 0), event.eventStart]
                    //for (i in range(max([event.eventStart - PREFALL_LENGTH, 0]), event.eventStart)) {
                    //    prefallDataTs.add(ts[i])
                    //}
                    firstFall = 0
                }
                numberOfSignificantFalls += 1
                freefallData[event.eventStart] = 100
                lastEventEnded = event.eventEnd
            } else if ((event.event_type == EVENT_IMPACT) and (event.impactType == TYPE_IMPACT_FORCE)) {
                numberOfForces += 1
            } else if ((event.event_type == EVENT_IMPACT) and (event.impactType >= TYPE_IMPACT_MEDIUM)) {
                numberOfSignificantImpacts += 1
                impactData[event.eventStart] = 100
                if ((event.eventStart - lastEventEnded) >= PREIMPACT_LENGTH) {
                    // preimpactData.add(max([event.eventStart - PREIMPACT_LENGTH, lastEventEnded + 1]), event.eventStart)
                    //preimpactDataTs.add([ts[max(event.eventStart - PREIMPACT_LENGTH, lastEventEnded + 1)], ts[event.eventStart])
                    preimpactFound = true
                }
                lastEventEnded = event.eventEnd
            }
        }
        appendLog(context, mFileName, "<br/><b>Significant freefall events:</b> $numberOfSignificantFalls")
        println("Significant freefall events: $numberOfSignificantFalls")
        appendLog(context, mFileName, "<br/><b>Significant impact events:</b> $numberOfSignificantImpacts")
        println("Significant impact events: $numberOfSignificantImpacts")
        appendLog(context, mFileName, "<br/><b>Force impartions:</b> $numberOfForces")
        println("Force impartions: $numberOfForces")
        appendLog(context, mFileName, "</body></html>")

        //return "Significant freefall events: $numberOfSignificantFalls, \nSignificant impact events: $numberOfSignificantImpacts, \nForce impartions: $numberOfForces"
        return prepareTFLiteModelInput(events)
    }

    private fun getEventDataFrame(event: DetectedEvent, ts: ArrayList<Long>, tsvDataSet: ArrayList<Double>, dtsvDataSet: ArrayList<Double>): TensorFlowModelInput {

        var impactEventCount: Int = 0
        var freeFallEventCount: Int = 0
        var maxTSV: Double = 0.0
        var totalTSV: Double = 0.0
        var avgTSV: Double = 0.0
        var maxDTSV: Double = 0.0
        var totalDTSV: Double = 0.0
        var avgDTSV: Double = 0.0
        var totalSeverity: Double = 0.0
        var avgSeverity: Double = 0.0
        var minTSV = 1000.0
        var totalMinTSV: Double = 0.0
        var avgMinTSV = 0.0
        var totalSpinDetectedEventCount: Int = 0
        var avgSpin: Double = 0.0


        if (event.event_type == EVENT_FREEFALL) {
            for (i in event.eventStart..event.eventEnd) {
                Log.d("msg EVENT_FREEFALL:", "TSV: ${tsvDataSet[i]}  DTSV: ${dtsvDataSet[i]}")

                freeFallEventCount++

                totalMinTSV += tsvDataSet[i]
                if (tsvDataSet[i] < minTSV) {
                    minTSV = tsvDataSet[i]
                }


                if (event.spinDetected) {
                    totalSpinDetectedEventCount++
                }
            }

        } else if (event.event_type == EVENT_IMPACT) {
            for (i in event.eventStart..event.eventEnd) {
                Log.d("msg EVENT_IMPACT:", "TSV: ${tsvDataSet[i]}  DTSV: ${dtsvDataSet[i]}")

                impactEventCount++

                totalTSV += tsvDataSet[i]
                if (tsvDataSet[i] > maxTSV)
                    maxTSV = event.maxTsv

                totalDTSV += dtsvDataSet[i]
                if (dtsvDataSet[i] > maxDTSV)
                    maxDTSV = dtsvDataSet[i]

                totalSeverity += event.impactType / 5.0
            }
        }
        if (impactEventCount == 0) impactEventCount = 1
        avgTSV = totalTSV / impactEventCount
        avgDTSV = totalDTSV / impactEventCount
        avgSeverity = totalSeverity / impactEventCount

        if (freeFallEventCount == 0) freeFallEventCount = 1
        avgMinTSV = totalMinTSV / freeFallEventCount
        avgSpin = (totalSpinDetectedEventCount / freeFallEventCount).toDouble()

        if (minTSV == 1000.0) minTSV = 0.0
        return TensorFlowModelInput(
                maxTSV = maxTSV,
                maxDTSV = maxDTSV,
                avgTSV = avgTSV,
                avgDTSV = avgDTSV,
                avgSeverity = avgSeverity,

                minTSV = minTSV,
                avgMinTSV = avgMinTSV,
                avgSpin = avgSpin
        )

    }


    /**
     * Prepare DataFrame for tfLite model input
     *
     * @param events is a  ArrayList of DetectedEvent
     */
    private fun prepareTFLiteModelInput(events: ArrayList<DetectedEvent>): TensorFlowModelInput {

        var impactEventCount: Int = 0
        var freeFallEventCount: Int = 0
        var maxTSV: Double = 0.0
        var totalTSV: Double = 0.0
        var avgTSV: Double = 0.0
        var maxDTSV: Double = 0.0
        var totalDTSV: Double = 0.0
        var avgDTSV: Double = 0.0
        var totalSeverity: Double = 0.0
        var avgSeverity: Double = 0.0
        var minTSV = 1000.0
        var totalMinTSV: Double = 0.0
        var avgMinTSV = 0.0
        var totalSpinDetectedEventCount: Int = 0
        var avgSpin: Double = 0.0

        for (event in events) {
            if (event.event_type == EventType.EVENT_IMPACT.value) {

                impactEventCount++

                totalTSV += event.maxTsv
                if (event.maxTsv > maxTSV)
                    maxTSV = event.maxTsv

                totalDTSV += event.dTsv
                if (event.dTsv > maxDTSV)
                    maxDTSV = event.dTsv

                totalSeverity += event.impactType / 5.0


            } else if (event.event_type == EventType.EVENT_FREEFALL.value) {
                freeFallEventCount++

                totalMinTSV += event.minTsv
                if (event.minTsv < minTSV) {
                    minTSV = event.minTsv
                }


                if (event.spinDetected) {
                    totalSpinDetectedEventCount++
                }


            }

            if (impactEventCount == 0) impactEventCount = 1
            avgTSV = totalTSV / impactEventCount
            avgDTSV = totalDTSV / impactEventCount
            avgSeverity = totalSeverity / impactEventCount

            if (freeFallEventCount == 0) freeFallEventCount = 1
            avgMinTSV = totalMinTSV / freeFallEventCount
            avgSpin = (totalSpinDetectedEventCount / freeFallEventCount).toDouble()
        }
        if (minTSV == 1000.0) minTSV = 0.0

        return TensorFlowModelInput(
                maxTSV = maxTSV,
                maxDTSV = maxDTSV,
                avgTSV = avgTSV,
                avgDTSV = avgDTSV,
                avgSeverity = avgSeverity,

                minTSV = minTSV,
                avgMinTSV = avgMinTSV,
                avgSpin = avgSpin
        )

    }


    /**
     * Parse Events and generate logs according to event data
     *
     * @param eventList     List of Detected events
     * @param tsDataSet     List of Timestamp data set
     */
    private fun parseEvents(eventList: ArrayList<DetectedEvent>, tsDataSet: ArrayList<Long>) {
        var spinResult: String
        var impactType: String

        for (event in eventList) {
            appendLog(context, mFileName, event.toString())
            println(event)
        }

        var lastEvent = 0
        for (event in eventList) {
            if (event.event_type == EVENT_FREEFALL) {
                if (event.spinDetected) {
                    spinResult = "Yes"
                } else {
                    spinResult = "No"
                }
                appendLog(context, mFileName, "<p><b>After ${(event.eventStart - lastEvent)} ms:</b> Freefall of duration ${(tsDataSet[event.eventEnd] - tsDataSet[event.eventStart])} ms, minimum TSV: ${(event.minTsv)} m/s2, estimated fall: ${estimateDistance((tsDataSet[event.eventEnd] - tsDataSet[event.eventStart]).toDouble())} feet, spin detected: $spinResult</p>")
                /** Is Significant FreeFall */
                val isSignificantFreeFall: Boolean = ((tsDataSet[event.eventEnd] - tsDataSet[event.eventStart]) >= FREEFALL_SIGNIFICANT) // TODO make it true to get all events in log
                if (isSignificantFreeFall) {

                    /** Logistic Regression prediction part */
                    val tensorFlowModelInput: TensorFlowModelInput = getEventDataFrame(event, tsDataSet, TSV, DTSV)
                    val predictedOutput: String = ClassifiedPredictionManager.predictFreeFallEvent(context, tensorFlowModelInput)
                    appendLog(context, mFileName, predictedOutput)

                    /** Neural Network prediction part */
                    /** Length of a FreeFall EVENT is 200 */
                    /** 210 length check is for 10 buffer size for FreeFall event. IndexOutOfOBound can occur without this check */
                    if (event.eventEnd >= 200 && xyzList.size > 210) {
                        val modelInputArray = FloatArray(600)
                        var count = 0
                        val tempArrayList: ArrayList<AccelerationNumericData> = ArrayList()
                        // Right shift by 10(as buffer) to sync with Model training Python code.
                        for (i in (event.eventStart - 189)..(event.eventStart + 10)) {
                            modelInputArray[3 * count + 0] = xyzList[i].x
                            modelInputArray[3 * count + 1] = xyzList[i].y
                            modelInputArray[3 * count + 2] = xyzList[i].z
                            count++
                            tempArrayList.add(AccelerationNumericData(count.toLong(), xyzList[i].x, xyzList[i].y, xyzList[i].z))
                        }
                        if (Constant.HAS_CAPTURED_EVENT_ONLY_FOLDER_IN) {
                            FolderFiles.createFolder(context, "events_only")
                            CsvFileOperator.writeCsvFile(context, tempArrayList, "events_only", "FreeFall" + System.currentTimeMillis())
                        }

                        val predictedOutput: String = PredictionManager.predictFallEventUsingNeuralNetwork(context, modelInputArray)
                        appendLog(context, mFileName, predictedOutput)
                    } else {
                        appendLog(context, mFileName, "Significant FreeFall event detected but don't have enough data to predict.</br>")
                    }
                }
                println("After ${(event.eventStart - lastEvent)} ms: Freefall of duration ${(tsDataSet[event.eventEnd] - tsDataSet[event.event_type])} ms, minimum TSV: ${(event.minTsv)} m/s2, estimated fall: ${estimateDistance((tsDataSet[event.eventEnd] - tsDataSet[event.eventStart]).toDouble())} feet, spin detected: $spinResult")
            } else if (event.event_type == EVENT_IMPACT) {
                if (event.impactType == TYPE_IMPACT_HARD) {
                    impactType = "Severe"
                } else if (event.impactType == TYPE_IMPACT_MEDIUM) {
                    impactType = "Medium"
                } else if (event.impactType == TYPE_IMPACT_SOFT) {
                    impactType = "Low"
                } else if (event.impactType == TYPE_IMPACT_FORCE) {
                    impactType = "Force"
                } else {
                    impactType = "Negligible"
                }
                appendLog(context, mFileName, "<p><b>After ${(event.eventStart - lastEvent)} ms:</b> Impact of duration ${(tsDataSet[event.eventEnd] - tsDataSet[event.eventStart])}, ms maximum TSV: ${(event.maxTsv)} m/s2, maximum DTSV: ${event.dTsv}, type: $impactType</p>")
                println("After ${(event.eventStart - lastEvent)} ms: Impact of duration ${(tsDataSet[event.eventEnd] - tsDataSet[event.eventStart])}, ms maximum TSV: ${(event.maxTsv)} m/s2, maximum DTSV: ${event.dTsv}, type: $impactType</br>")

                appendLog(context, mFileName, detectImpactDirection(TSV, event.eventStart, event.eventEnd - 1))
                /** Is Significant Impact */
                if (event.impactType == TYPE_IMPACT_FORCE) {                // TODO make it false to get all events in log
                    // KEEP THIS BLOCK EMPTY TO FILTER OUT FORCE FROM IMPACT EVENTS
                } else if (event.impactType >= TYPE_IMPACT_MEDIUM) {        // TODO make it true to get all events in log
                    val tensorFlowModelInput: TensorFlowModelInput = getEventDataFrame(event, tsDataSet, TSV, DTSV)
                    val outputString = ClassifiedPredictionManager.predictImpactEvent(context, tensorFlowModelInput)
                    appendLog(context, mFileName, outputString)

                    /** Neural Network prediction part */
                    /** Length of a IMPACT EVENT is 100 */
                    /** 120 length check for 20 buffer size for IMPACT event. IndexOutOfOBound can occur without this check */
                    if (event.eventEnd >= 100 && xyzList.size > 120) {
                        val modelInputArray = FloatArray(300)
                        var count = 0
                        val tempArrayList: ArrayList<AccelerationNumericData> = ArrayList()
                        /** Right shift by 20(as buffer) to sync with Model training Python code. */
                        for (i in (event.eventStart - 79)..(event.eventStart + 20)) {
                            modelInputArray[3 * count + 0] = xyzList[i].x
                            modelInputArray[3 * count + 1] = xyzList[i].y
                            modelInputArray[3 * count + 2] = xyzList[i].z
                            count++
                            tempArrayList.add(AccelerationNumericData(count.toLong(), xyzList[i].x, xyzList[i].y, xyzList[i].z))
                        }
                        if (Constant.HAS_CAPTURED_EVENT_ONLY_FOLDER_IN) {
                            FolderFiles.createFolder(context, "events_only")
                            CsvFileOperator.writeCsvFile(context, tempArrayList, "events_only", "Impact" + System.currentTimeMillis())
                        }
                        val predictedOutput: String = PredictionManager.predictImpactEventUsingNeuralNetwork(context, modelInputArray)
                        appendLog(context, mFileName, predictedOutput)
                    } else {
                        appendLog(context, mFileName, "Significant Impact event detected but don't have enough data to predict.</br>")
                    }
                }


                //println("${detectImpactDirection(TSV, event.eventStart, event.count - 1)}")
            } else {
                appendLog(context, mFileName, "<p><b>After ${(event.eventStart - lastEvent)} ms:</b> Unknown event of duration ${(tsDataSet[event.eventEnd] - tsDataSet[event.eventStart])} ms</p>")
                println("After ${(event.eventStart - lastEvent)} ms: Unknown event of duration ${(tsDataSet[event.eventEnd] - tsDataSet[event.eventStart])} ms")
            }
            lastEvent = event.eventEnd - 1
        }
        return
    }


    /**
     * Impact direction of the specified event between start and end point
     *
     * @param tsvDataSet    List of TSV data set calculated from x,y & z axis value
     * @param start         Event starting point
     * @param end           Event ending point
     * @return              Returns Impact direction of the event to log into logfile
     */

    private fun detectImpactDirection(tsvDataSet: ArrayList<Double>, start: Int, end: Int): String {
        var xComponent = 0L
        var yComponent = 0L
        var zComponent = 0L

        var maxTsv = -1.0
        var maxI = -1

        for (i in start..end) {
            if (tsvDataSet.get(i) > maxTsv) {
                maxTsv = tsvDataSet.get(i)
                maxI = i
            }
        }

        if (maxI >= 0) {
            // Values
            xComponent = Math.round((xyzList.get(maxI).x * xyzList.get(maxI).x * 100) / (tsvDataSet.get(maxI) * tsvDataSet.get(maxI)))
            yComponent = Math.round((xyzList.get(maxI).y * xyzList.get(maxI).y * 100) / (tsvDataSet.get(maxI) * tsvDataSet.get(maxI)))
            zComponent = Math.round((xyzList.get(maxI).z * xyzList.get(maxI).z * 100) / (tsvDataSet.get(maxI) * tsvDataSet.get(maxI)))

            //# Signs
            if (xyzList.get(maxI).x < 0) {
                xComponent = -xComponent
            }

            if (xyzList.get(maxI).y < 0) {
                yComponent = -yComponent
            }

            if (xyzList.get(maxI).z < 0) {
                zComponent = -zComponent
            }
        }
        return "Impact direction = [$xComponent, $yComponent, $zComponent]"
    }
}
