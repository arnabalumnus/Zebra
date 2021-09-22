# 1. Calculate timediffs
# 2. Interpolate if required.
# 3. Resample if required
# 4. Calculate TSV, DTSV
# 5. Identify number of abuses - based upon calm zones/xcorrelation
# 6. For each abuse case -
#   6.1. Identify Zones - Prefall, noise, freefall
#   6.2. Identify freefall events - based upon TSV
#   6.3. Analyse freefall zone for additional insights - duration, height, spin
#   6.4. Identify Impact events - based upon DTSV
#   6.5. Analyse impact events for additional insights - severity, surface type, type
#   6.6. Analyse prefall zone for abuse type detection
# 7. Analyse multi-abuse cases for additional insights - repeated banging, stair fall etc.
# 8. Notify UI (?)

#######################
#        FLAGS        #
#######################
debugMode = False
interpolateOn = False
resampleOn = False
filterOn = False

#######################
#       IMPORTS       #
#######################
import pandas as pd
import os
import shutil
import math
import copy
import re
import sys
import getopt
import matplotlib as mpl
mpl.use('Agg')
import matplotlib.pyplot as plt
import matplotlib.patches as patches
import numpy as np
import getpass

from scipy.interpolate import interp1d
from sklearn.svm import SVC
from sklearn.ensemble import RandomForestClassifier
import joblib
from shutil import copyfile
from scipy.integrate import simps

#######################
#       SCALARS       #
#######################
EVENT_IMPACT = 1
EVENT_FREEFALL = 2

TYPE_UNKNOWN = 0
TYPE_FREEFALL_SIGNIFICANT = 1
TYPE_FREEFALL_INSIGNIFICANT = 2
TYPE_IMPACT_NEGLIGIBLE = 1
TYPE_IMPACT_SOFT = 2
TYPE_IMPACT_MEDIUM = 3
TYPE_IMPACT_HARD = 4
TYPE_IMPACT_FORCE = 5

TSV_IMPACT = 24.5
TSV_FREEFALL = 5.8
TSV_CALMZONE_HIGH = 11
TSV_CALMZONE_MEDIUM = 10
TSV_CALMZONE_LOW = 8
TSV_FREEFALL_SPIN = 4

DTSV_IMPACT_LOW = 10
DTSV_IMPACT_MEDIUM = 14.7
DTSV_IMPACT_HIGH = 20

interpolateFreq = 10
resampleFactor = 2

ZONE_PREFALL = 1
ZONE_FREEFALL = 2
ZONE_IMPACT = 3
ZONE_NOISE = 4
ZONE_CALM = 0

PREFALL_LENGTH = 200
PREIMPACT_LENGTH = 100
FREEFALL_SIGNIFICANT = 200
CALMZONE_DURATION = 30
IMPACT_LENGTH_MIN = 60
FORCE_AREA_MIN = 600
EVENT_GAP_MIN = 30

prefallTypes = [
    ["FFXP","a freefall on its left side"],
    ["FFXM","a freefall on its right side"],
    ["FFYP","a freefall on its bottom"],
    ["FFYM","a freefall on its top"],
    ["FFZP","a freefall while facing down"],
    ["FFZM","a freefall while facing up"],
    ["HFXP","a freefall following a push on its left side"],
    ["HFXM","a freefall following a push on its right side"],
    ["HFYP","a freefall following a push on its bottom"],
    ["HFYM","a freefall following a push on its top"],
    ["HFZP","a freefall following a push on its face"],
    ["HFZM","a freefall following a push on its back"],
    ["OTXX","an overhand throw"],
    ["UTXX","an underhand throw"],
    ["PFXX","a slippage while being put into pocket"],
    ["PPXX","a slippage while being put into pocket"],
    ["TCXX","an upward throw"]
    ]

impactTypes = [
    ["DSXX","was slammed on the desk"],
    ["WSXX","hit a wall"],
    ["FSXX","was slammed on the floor"]
    ]

imageTypes = [
    ["FFXP","img/DAFall.png"],
    ["FFXM","img/DAFall.png"],
    ["FFYP","img/DAFall.png"],
    ["FFYM","img/DAFall.png"],
    ["FFZP","img/DAFall.png"],
    ["FFZM","img/DAFall.png"],
    ["HFXP","img/DATableFall.png"],
    ["HFXM","img/DATableFall.png"],
    ["HFYP","img/DATableFall.png"],
    ["HFYM","img/DATableFall.png"],
    ["HFZP","img/DATableFall.png"],
    ["HFZM","img/DATableFall.png"],
    ["OTXX","img/DAOThrow.png"],
    ["UTXX","img/DAUThrow.png"],
    ["PFXX","img/DAPocketSlip.png"],
    ["PPXX","img/DAPocketSlip.png"],
    ["TCXX","img/DAThrowCatch.png"],
    ["DSXX","img/DADeskSlam.png"],
    ["WSXX","img/DAWallSlam.png"],
    ["FSXX","img/DAFloorSlam.png"]
    ]

#######################
#       GLOBALS       #
#######################
# Raw data
numberOfSamples = 0
timeDiffInMs = 0
tsv = list()
dtsv = list()

# Interpolated data
numberOfInterpolatedSamples = 0;
tsInterpolated = list()
xInterpolated = list()
yInterpolated = list()
zInterpolated = list()
tsvInterpolated = list()
dtsvInterpolated = list()

# Resampled data
numberOfResampledSamples = 0
tsResampled = list()
xResampled = list()
yResampled = list()
zResampled = list()
tsvResampled = list()
dtsvResampled = list()

# Filtered data
xFiltered = list()
yFiltered = list()
zFiltered = list()
tsvFiltered = list()
dtsvFiltered = list()

# Event data
events = list()
processedEvent = list()
INDEX_EVENT_TYPE = 0
INDEX_EVENT_SIGNIFICANT = 1
INDEX_EVENT_SEVERITY = 2
INDEX_EVENT_DURATION = 3
INDEX_EVENT_START = 4
INDEX_EVENT_END = 5
INDEX_EVENT_HEIGHT = 6
INDEX_EVENT_SPIN = 7
INDEX_EVENT_ORIENTATION = 6
INDEX_EVENT_SURFACE = 7
INDEX_EVENT_CAUSE = 8
filteredEvents = list()

# Zone data
zones = list()

# Model path
modelPath = os.path.dirname(os.path.realpath(__file__))

#######################
#       HELPERS       #
#######################
def addEvent (eventType, eventStart, eventEnd):
    # Create new event
    processedEvent.append([eventType, False, 0, 0, eventStart, eventEnd, 0, 0, ""])
    
    currentIndex = len(processedEvent) - 1

    # Detect duration

    # Detect significance

    # 
    return currentIndex

def findEvent (eventType, eventStart):
    eventIndex = 0

    return int(eventIndex)

def updateEventValue (eventIndex, updateIndex, newValue):

    return

def updateEventCause (eventIndex, newCause):
    return updateEventValue(eventIndex, INDEX_EVENT_CAUSE, newCause)

"""
This function fills in missing data for prefall and preimpact analysis
    Input:
        (1: list) Actual dataset
        (2: int) Start of data
        (3: int) End of data
        (4: int) Required length
    Output:
        (1: list) Filled-in dataset
"""
def fillData (origDataset, start, end, length):
    finalDataset = list()
    if (length > (end - start)):
        fillNeed = length - (end - start)
    else:
        fillNeed = 0
    for i in range(fillNeed):
        finalDataset.append(origDataset[start])
    for i in range(start, end):
        finalDataset.append(origDataset[i])

    return finalDataset

"""
This function estimates fall height from fall duration.
    Input:
        (1: float) Fall duration in ms
    Output:
        (1: float) Estimated fall height in feet
"""
def estimateDistance (durarion = 0):
    return round((3.28*(9.81*(durarion/1000)*(durarion/1000))/2) * 1.225, 2)

"""
This function detects spin in a freefall.
    Input:
        (1: list) TSV dataset
        (2: int) Freefall start index
        (3: int) Freefall end index
    Output:
        (1: Bool) True if spin detected, False otherwise
"""
def detectSpin (tsvDataset, start, end):
    detected = False

    for i in range(start, end):
        if (tsvDataset[i] >= TSV_FREEFALL_SPIN):
            detected = True
            break

    return detected

"""
This function detects impact orientation.
    Input:
        (1: list) TSV dataset
        (2: list) X dataset
        (3: list) Y dataset
        (4: list) Z dataset
        (5: int) Impact start index
        (6: int) Impact end index
    Output:
        (1: list) Relative components (in %) of TSV in each 3 direction
"""
def detectImpactDirection (tsvDataset, xDataset, yDataset, zDataset, start, end):
    xComponent = 0
    yComponent = 0
    zComponent = 0

    maxTsv = -1
    maxI = -1

    for i in range(start, end):
        if (tsvDataset[i] > maxTsv):
            maxTsv = tsvDataset[i]
            maxI = i

    if (maxI >= 0):

        # Values
        xComponent = round((xDataset[maxI] * xDataset[maxI] * 100) / (tsvDataset[maxI] * tsvDataset[maxI]), 2)
        yComponent = round((yDataset[maxI] * yDataset[maxI] * 100) / (tsvDataset[maxI] * tsvDataset[maxI]), 2)
        zComponent = round((zDataset[maxI] * zDataset[maxI] * 100) / (tsvDataset[maxI] * tsvDataset[maxI]), 2)

        # Signs
        if (xDataset[maxI] < 0):
            xComponent = -xComponent

        if (yDataset[maxI] < 0):
            yComponent = -yComponent

        if (zDataset[maxI] < 0):
            zComponent = -zComponent
        
    return [xComponent, yComponent, zComponent]

"""
This function merges closely spaced similar events to handle in-event noises.
    Input:
        (1: list) Events' list
    Output:
        (1: list) Merged events' list
"""
def mergeEvents(events):
    mergedEvents = list()

    if (len(events) <= 1):
        return events

    i = 0
    continued = False
    while (i < len(events)):
        if (i == (len(events) - 1)):
            # We're at last event
            if not (continued):
                currentEvent = events[i]
            else:
                i -= 1
                continued = False
        else:
            if (continued):
                nextEvent = events[i]
            else:
                currentEvent = events[i]
                nextEvent = events[i + 1]
                i += 1

            if (nextEvent[0] == currentEvent[0]):
                # The two events are same
                if ((nextEvent[1] - currentEvent[2]) < EVENT_GAP_MIN):
                    # Too closely spaced, merge
                    currentEvent[2] = nextEvent[2]
                    if (currentEvent[0] == EVENT_IMPACT):
                        currentEvent[3] = max(currentEvent[3], nextEvent[3])
                        currentEvent[4] = max(currentEvent[4], nextEvent[4])
                        currentEvent[5] = max(currentEvent[5], nextEvent[5])
                    elif (currentEvent[0] == EVENT_FREEFALL):
                        currentEvent[3] = min(currentEvent[3], nextEvent[3])
                        currentEvent[4] = currentEvent[4] or nextEvent[4]
                    continued = True
                else:
                    continued = False
                    i -= 1
            else:
                continued = False
                i -= 1
        i += 1
        if not (continued):
            mergedEvents.append(currentEvent)
    return mergedEvents

"""
This function detects freefall and impact events in TSV, DTSV datasets.
    Input:
        (1: list) Timestamp dataset
        (2: list) TSV dataset
        (3: list) DTSV dataset
    Output:
        (1: list) Detected events
                    Freefall: [[EVENT_FREEFALL, eventStart, eventEnd, minTsv, spinFlag]]
                    Impact:   [[EVENT_IMPACT, eventStart, eventEnd, maxTsv, maxDtsv, impactType]]
        (2: list) Noise zones
                    [[noiseStart, noiseEnd]]
    
"""
def detectEvents (tsDataset, tsvDataset, dtsvDataset):
    noiseZones = list()
    detectedEvents = list()
    numberOfSamples = len(tsDataset)

    maxTsv = -1.0
    minTsv = -1.0
    freefallStart = -1
    impactStart = -1
    noiseStart = -1
    calmZoneCount = 0

    for i in range(numberOfSamples):
        currentTsv = tsvDataset[i]
        # Update max/min TSV values if required
        if (maxTsv >= 0):
            if (maxTsv < currentTsv):
                maxTsv = currentTsv
        elif (minTsv >= 0):
            if (minTsv > currentTsv):
                minTsv = currentTsv

        if (currentTsv > TSV_IMPACT):
            # Impact zone
            if (noiseStart >= 0):
                calmZoneCount = 0
            # Finalize stuff if this marks the end of a freefall event
            if (freefallStart > 0):
                spinDetected = detectSpin(tsvResampled, freefallStart, i)
                detectedEvents.append([EVENT_FREEFALL, freefallStart, i, minTsv, spinDetected])
                freefallStart = -1
                minTsv = -1.0
            # Initialize stuff if this is the start of impact
            if (impactStart < 0):
                impactStart = i
                maxTsv = currentTsv
        elif (currentTsv < TSV_FREEFALL):
            # Freefall zone
            if (noiseStart >= 0):
                calmZoneCount = 0
            # Finalize stuff if this marks the end of an impact event
            if (impactStart > 0):
                # Look at DTSV to determine type
                maxDtsv = -1.0
                for j in range(impactStart, i):
                    if (maxDtsv < dtsvDataset[j]):
                        maxDtsv = dtsvDataset[j]
                if (maxDtsv >= DTSV_IMPACT_HIGH):
                    # If the DTSV is too highm it is an impact
                    impactType = TYPE_IMPACT_HARD
                else:
                    # Otherwise, it might be force impartion or impact
                    areaUnderCurve = simps(tsvDataset[impactStart:i], dx=timeDiffInMs)
                    print("Area under curve:", areaUnderCurve)
                    if (areaUnderCurve >= FORCE_AREA_MIN):
                        # Not actually an impact, just external application of force
                        impactType = TYPE_IMPACT_FORCE
                    else:
                        # Impact it is
                        if (maxDtsv >= DTSV_IMPACT_MEDIUM):
                            impactType = TYPE_IMPACT_MEDIUM
                        elif (maxDtsv >= DTSV_IMPACT_LOW):
                            impactType = TYPE_IMPACT_SOFT
                        else:
                            impactType = TYPE_IMPACT_NEGLIGIBLE
                detectedEvents.append([EVENT_IMPACT, impactStart, i, maxTsv, maxDtsv, impactType])

                # Noise filtering to be done only for high and medium impacts
                if ((impactType == TYPE_IMPACT_HARD) or (impactType == TYPE_IMPACT_MEDIUM)):
                    # End of impact, detect noise zone
                    if (noiseStart < 0):
                        noiseStart = i
                    calmZoneCount = 0
                impactStart = -1
                maxTsv = -1.0
            # Initialize stuff if this is the start of freefall
            if (freefallStart < 0):
                freefallStart = i
                minTsv = currentTsv
        else:
            # Regular zone
            # Check for end of noise
            if (noiseStart >= 0):
                if ((currentTsv <= TSV_CALMZONE_HIGH) and (currentTsv >= TSV_CALMZONE_LOW)):
                    calmZoneCount += 1
                    if (calmZoneCount >= CALMZONE_DURATION):
                        # We have calmed down enough
                        noiseZones.append([noiseStart, i])
                        noiseStart = -1
                        calmZoneCount = 0
                else:
                    # Still some noise
                    calmZoneCount = 0
            # Finalize stuff if this marks the end of an impact event
            if (impactStart > 0):
                # Look at DTSV to determine type
                maxDtsv = -1.0
                for j in range(impactStart, i):
                    if (maxDtsv < dtsvDataset[j]):
                        maxDtsv = dtsvDataset[j]
                if (maxDtsv >= DTSV_IMPACT_HIGH):
                    # If the DTSV is too high, it is an impact
                    impactType = TYPE_IMPACT_HARD
                else:
                    areaUnderCurve = simps(tsvDataset[impactStart:i], dx=timeDiffInMs)
                    print("Area under curve:", areaUnderCurve)
                    if (areaUnderCurve >= FORCE_AREA_MIN):
                        # Not actually an impact, just external application of force
                        impactType = TYPE_IMPACT_FORCE
                    else:
                        # Impact it is
                        if (maxDtsv >= DTSV_IMPACT_MEDIUM):
                            impactType = TYPE_IMPACT_MEDIUM
                        elif (maxDtsv >= DTSV_IMPACT_LOW):
                            impactType = TYPE_IMPACT_SOFT
                        else:
                            impactType = TYPE_IMPACT_NEGLIGIBLE
                detectedEvents.append([EVENT_IMPACT, impactStart, i, maxTsv, maxDtsv, impactType])

                # Noise filtering to be done only for high and medium impacts
                if ((impactType == TYPE_IMPACT_HARD) or (impactType == TYPE_IMPACT_MEDIUM)):
                    # End of impact, detect noise zone
                    if (noiseStart < 0):
                        noiseStart = i
                    calmZoneCount = 0
                impactStart = -1
                maxTsv = -1
            # Finalize stuff if this marks the end of a freefall event
            if (freefallStart > 0):
                spinDetected = detectSpin(tsvResampled, freefallStart, i)
                detectedEvents.append([EVENT_FREEFALL, freefallStart, i, minTsv, spinDetected])
                freefallStart = -1
                minTsv = -1
    print("Detect Event Count: "+str(len(detectedEvents)))
    print("Noise Count: " +str(len(noiseZones)))
    return detectedEvents, noiseZones

"""
This function parses a list of events and prints the results
    Input:
        (1: list) Event list
        (2: list) Timestamp dataset
    Output:
        Nothing
"""
def parseEvents (eventList, tsDataset):
    if (debugMode):
        for event in eventList:
            print(event)

    lastEvent = 0
    for event in eventList:
        if (event[0] == EVENT_FREEFALL):
            if (event[4]):
                spinResult = "Yes"
            else:
                spinResult = "No"
            print("After", format(event[1] - lastEvent, '.2f'), "ms:", "Freefall of duration", format(tsDataset[event[2]] - tsDataset[event[1]], '.2f'), "ms, minimum TSV:", format(event[3], '.2f'), "m/s2, estimated fall:", estimateDistance(tsDataset[event[2]] - tsDataset[event[1]]), "feet, spin detected:", spinResult)
        elif(event[0] == EVENT_IMPACT):
            if (event[5] == TYPE_IMPACT_HARD):
                impactType = "Severe"
            elif (event[5] == TYPE_IMPACT_MEDIUM):
                impactType = "Medium"
            elif (event[5] == TYPE_IMPACT_SOFT):
                impactType = "Low"
            elif (event[5] == TYPE_IMPACT_FORCE):
                impactType = "Force"
            else:
                impactType = "Negligible"
            print("After", format(event[1] - lastEvent, '.2f'), "ms:", "Impact of duration", format(tsDataset[event[2]] - tsDataset[event[1]], '.2f'), "ms, maximum TSV:", format(event[3], '.2f'), "m/s2, maximum DTSV:", event[4], ", type:", impactType)
            print("Impact direction =", detectImpactDirection (tsvResampled, xResampled, yResampled, zResampled, event[1], event[2]))
        else:
            print("After", format(event[1] - lastEvent, '.2f'), "ms:", "Unknown event of duration", format(tsDataset[event[2]] - tsDataset[event[1]], '.2f'), "ms")
        lastEvent = event[2]
    return

def prefallProcess(xData, yData, zData, zoneStart, zoneEnd):
    finalDF = pd.DataFrame()
    
    rowCount = zoneEnd - zoneStart
    count = 0
       
    if (rowCount >= PREFALL_LENGTH):
        start = 0
        end = rowCount
       
        for i in range(start, end):
            temp = pd.DataFrame(columns=['X'+str(i), 'Y'+str(i), 'Z'+str(i)])
            temp.loc[0] = [xData[i], yData[i], zData[i]]
            temp = temp.reset_index(drop=True)
           
            if count == 0:
                tempDF = temp
                count = 1
            else:
                tempDF = pd.concat([tempDF.reset_index(drop=True), temp], axis=1)
               
        finalDF = finalDF.append(tempDF)
    elif (rowCount >= PREFALL_LENGTH / 2):
        # Not enough data, but still try with padding
        diff = PREFALL_LENGTH - rowCount
        start = 0
        end = PREFALL_LENGTH
        countDown = PREFALL_LENGTH - rowCount
        
        for i in range(start, end):
            temp = pd.DataFrame(columns=['X'+str(i), 'Y'+str(i), 'Z'+str(i)])
            if (countDown > 0):
                temp.loc[0] = [xData[0], yData[0], zData[0]]
                countDown -= 1
            else:
                temp.loc[0] = [xData[i - diff], yData[i - diff], zData[i - diff]]
            temp = temp.reset_index(drop=True)
           
            if count == 0:
                tempDF = temp
                count = 1
            else:
                tempDF = pd.concat([tempDF.reset_index(drop=True), temp], axis=1)
               
        finalDF = finalDF.append(tempDF)
    else:
        # Not nearly enough data
        print("Cannot do prefall analysis. Number of samples is less than", PREFALL_LENGTH)
    return finalDF

def preimpactProcess(xData, yData, zData, zoneStart, zoneEnd):
    finalDF = pd.DataFrame()
    
    rowCount = zoneEnd - zoneStart
    count = 0
       
    if (rowCount >= PREIMPACT_LENGTH):
        start = 0
        end = rowCount
       
        for i in range(start, end):
            temp = pd.DataFrame(columns=['X'+str(i), 'Y'+str(i), 'Z'+str(i)])
            temp.loc[0] = [xData[i], yData[i], zData[i]]
            temp = temp.reset_index(drop=True)
           
            if count == 0:
                tempDF = temp
                count = 1
            else:
                tempDF = pd.concat([tempDF.reset_index(drop=True), temp], axis=1)
               
        finalDF = finalDF.append(tempDF)
    elif (rowCount >= PREIMPACT_LENGTH / 2):
        # Not enough data, but still try with padding
        diff = PREIMPACT_LENGTH - rowCount
        start = 0
        end = PREIMPACT_LENGTH
        countDown = PREIMPACT_LENGTH - rowCount
        
        for i in range(start, end):
            temp = pd.DataFrame(columns=['X'+str(i), 'Y'+str(i), 'Z'+str(i)])
            if (countDown > 0):
                temp.loc[0] = [xData[0], yData[0], zData[0]]
                countDown -= 1
            else:
                temp.loc[0] = [xData[i - diff], yData[i - diff], zData[i - diff]]
            temp = temp.reset_index(drop=True)
           
            if count == 0:
                tempDF = temp
                count = 1
            else:
                tempDF = pd.concat([tempDF.reset_index(drop=True), temp], axis=1)
               
        finalDF = finalDF.append(tempDF)
    else:
        # Not nearly enough data
        print("Cannot do prefall analysis. Number of samples is less than", PREIMPACT_LENGTH)
    return finalDF

def convertEventCode(eventCode):
    for event in prefallTypes:
        if (event[0] == eventCode):
            return event[1]

    for event in impactTypes:
        if (event[0] == eventCode):
            return event[1]

    return "Unknown event"

def getImageName(eventCode):
    for event in imageTypes:
        if (event[0] == eventCode):
            return event[1]

    return "Unknown.png"

def detectType(eventList, fallNum, impactNum, fallType, impactTypes):
    impactResult = ""
    freefallResult = ""
    result = "The device"
    firstFall = True
    firstImpact = True
    imageFileName = "img/Unknown.png"

    if (fallNum + impactNum == 0):
        result = result + " did not suffer any major abuse"
        impactResult = "No significant impacts detected"
        freefallResult = "No significant freefalls detected"
        return freefallResult, impactResult, result, imageFileName
    
    if (fallNum == 0):
        result = result + " did not have a significant fall"
    elif (fallNum == 1):
        if (len(fallType) == 0):
            result = result + " fell down once"
        else:
            result = result + " suffered " + convertEventCode(fallType)
            imageFileName = getImageName(fallType)
    else:
        if (len(fallType) == 0):
            result = result + " fell down " + str(fallNum) + " time(s)"
        else:
            result = result + " suffered " + convertEventCode(fallType)
            imageFileName = getImageName(fallType)

    if (impactNum == 0):
        result = result + ", but did not hit anything hard."
    else:
        if (fallNum == 0):
            result = result + ", but "
            if(len(impactTypes[0]) > 0):
                result = result + convertEventCode(impactTypes[0])
                imageFileName = getImageName(impactTypes[0])
        else:
            result = result + ", and "
            if (impactNum == 1):
                result = result + "hit a surface."
            elif (impactNum == 2):
                result = result + "there were a couple of impacts."
            elif (impactNum > 2):
                result = result + "there were multiple impacts (" + str(impactNum) + ")"       

    for event in eventList:
        if (event[0] == EVENT_FREEFALL):
            if (event[4]):
                spinResult = "Yes"
            else:
                spinResult = "No"
            if (firstFall):
                firstFall = False
            else:
                freefallResult = freefallResult + "<br><br>"
            freefallResult = freefallResult + "<b>Freefall</b> of duration " + str(round(tsResampled[event[2]] - tsResampled[event[1]], 2)) + " ms<br>Minimum TSV: " + str(round(event[3], 2))  + " m/s2<br>Estimated fall height: " + str(round(estimateDistance(tsResampled[event[2]] - tsResampled[event[1]]), 2)) + " feet<br>Spin detected: " + spinResult
        elif (event[0] == EVENT_IMPACT):
            if (firstImpact):
                firstImpact = False
            else:
                impactResult = impactResult + "<br><br>"
            if (event[5] == TYPE_IMPACT_FORCE):
                impactResult = impactResult + "<b>Force</b> impartion of duration " + str(round(tsResampled[event[2]] - tsResampled[event[1]], 2)) + " ms<br>Maximum TSV: " + str(round(event[3], 2)) + " m/s2"
            else:
                if (event[5] == TYPE_IMPACT_HARD):
                    impactType = "Severe"
                elif (event[5] == TYPE_IMPACT_MEDIUM):
                    impactType = "Medium"
                elif (event[5] == TYPE_IMPACT_SOFT):
                    impactType = "Low"
                else:
                    impactType = "Negligible"
                impactResult = impactResult + "<b>Impact</b> of duration " + str(round(tsResampled[event[2]] - tsResampled[event[1]], 2)) + " ms<br>Maximum TSV: " + str(round(event[3], 2)) + " m/s2<br>Type: " + impactType

    if (len(impactResult) == 0):
        impactResult = "No significant impacts detected"

    if (len(freefallResult) == 0):
        freefallResult = "No significant freefalls detected"

    #print(freefallResult)
    #print(impactResult)
    #print(result)

    return freefallResult, impactResult, result, imageFileName

#######################
#        MAIN         #
#######################

# 0. Initializations
fullFilename = ''

# 1. Input filename
try:
    opts, args = getopt.getopt(sys.argv[1:], "dirnf:")
except getopt.GetoptError:
    print ("ZebraDeviceAbuse.py -d -i -r -n <filename>")
    print ("d: debug ON")
    print ("i: interpolation ON")
    print ("r: resampling ON")
    print ("n: noise removal ON")
    print ("<filename>: input filename")
    sys.exit(2)
for opt, arg in opts:
    if (opt == '-d'):
        debugMode = True
    elif (opt == '-i'):
        interpolateOn = True
    elif (opt == '-r'):
        resampleOn = True
    elif (opt == '-n'):
        filterOn = True

if (len(args) == 0):
    print ("Please provide filename")
    sys.exit()
else:
    fullFilename = args[0]
    print ("Processing file:", fullFilename)
    filename = fullFilename[:-4]
    extension = fullFilename[-3:]

    if (debugMode):
        print('Filename:', filename)
        print('Extension:', extension)

# 2. Load raw data
csv_path = os.path.join(os.getcwd(), fullFilename)
fileData = pd.read_csv(csv_path)
fileData["TS"] = fileData["TS"] - fileData["TS"][0]
fileData["TS"] = fileData["TS"] / 1000000
numberOfSamples = fileData["TS"].count()
timeDiffInMs = round(np.median(np.diff(fileData["TS"])))
print("Timediff median:", timeDiffInMs)

if (debugMode):
    print(str(fileData.head()))
    fileData.info()
    print('Number of samples:', numberOfSamples)

# 3. Initialize algorithm parameters
# 3.1. TSV
for i in range(numberOfSamples):
    tsv.append(np.sqrt(np.square(fileData["X"][i]) + np.square(fileData["Y"][i]) + np.square(fileData["Z"][i])))

# 3.2. DTSV
dtsv.append(0)
for i in range(1, numberOfSamples):
    dtsv.append(tsv[i] - tsv[i - 1])

# 4. Process data
# 4.1. Interpolate, if required
if (interpolateOn):
    print('Interpolating data to frequency', interpolateFreq, 'ms')
    numberOfInterpolatedSamples = math.floor(((fileData["TS"][numberOfSamples - 1] - fileData["TS"][0])/interpolateFreq)+1)
    tsInterpolated = np.linspace(fileData["TS"][0], fileData["TS"][numberOfSamples - 1], numberOfInterpolatedSamples)
    f = interp1d(fileData["TS"], fileData["X"])
    xInterpolated = f(tsInterpolated)
    f = interp1d(fileData["TS"], fileData["Y"])
    yInterpolated = f(tsInterpolated)
    f = interp1d(fileData["TS"], fileData["Z"])
    zInterpolated = f(tsInterpolated)
    for i in range(numberOfInterpolatedSamples):
        tsvInterpolated.append(np.sqrt(np.square(xInterpolated[i]) + np.square(yInterpolated[i]) + np.square(zInterpolated[i])))
    dtsvInterpolated.append(0)
    for i in range(1, numberOfInterpolatedSamples):
        dtsvInterpolated.append(tsvInterpolated[i] - tsvInterpolated[i - 1])
else:
    print('Interpolation off')
    numberOfInterpolatedSamples = numberOfSamples
    tsInterpolated = copy.deepcopy(fileData["TS"][:])
    xInterpolated = copy.deepcopy(fileData["X"][:])
    yInterpolated = copy.deepcopy(fileData["Y"][:])
    zInterpolated = copy.deepcopy(fileData["Z"][:])
    tsvInterpolated = copy.deepcopy(tsv[:])
    dtsvInterpolated = copy.deepcopy(dtsv[:])

# 4.2. Resample, if required
if (resampleOn):
    print('Resampling data by factor', resampleFactor)
    for i in range(0, numberOfInterpolatedSamples, resampleFactor):
        tsResampled.append(tsInterpolated[i])
        xResampled.append(xInterpolated[i])
        yResampled.append(yInterpolated[i])
        zResampled.append(zInterpolated[i])
        tsvResampled.append(tsvInterpolated[i])
        dtsvResampled.append(dtsvInterpolated[i])
        numberOfResampledSamples += 1
else:
    print('Resampling off')
    numberOfResampledSamples = numberOfInterpolatedSamples
    tsResampled = tsInterpolated[:]
    xResampled = xInterpolated[:]
    yResampled = yInterpolated[:]
    zResampled = zInterpolated[:]
    tsvResampled = tsvInterpolated[:]
    dtsvResampled = dtsvInterpolated[:]

# 5. Detect events
events, noises = detectEvents(tsResampled, tsvResampled, dtsvResampled)
print("Detected events:")
parseEvents(events, tsResampled)
print("Noise zones:")
for noise in noises:
    print(noise)

# Filter, if required
xFiltered = xResampled[:]
yFiltered = yResampled[:]
zFiltered = zResampled[:]
tsvFiltered = tsvResampled[:]
dtsvFiltered = dtsvResampled[:]

if (filterOn):
    for noise in noises:
        for i in range(noise[0], noise[1]):
            xFiltered[i] = TSV_CALMZONE_MEDIUM
            yFiltered[i] = TSV_CALMZONE_MEDIUM
            zFiltered[i] = TSV_CALMZONE_MEDIUM
            tsvFiltered[i] = TSV_CALMZONE_MEDIUM
            dtsvFiltered[i] = TSV_CALMZONE_MEDIUM
    filteredEvents, filteredNoises = detectEvents(tsResampled, tsvFiltered, dtsvFiltered)
    print("Filtered events:")
    parseEvents(filteredEvents, tsResampled)
    for event in filteredEvents:
        print(event)
    filteredEvents = mergeEvents(filteredEvents)
    print("Merged events:")
    parseEvents(filteredEvents, tsResampled)
    for event in filteredEvents:
        print(event)
else:
    filteredEvents = events

# 6. Find type of abuse
# 6.1. Find first significant fall event

# Only use significant events
numberOfSignificantFalls = 0
numberOfSignificantImpacts = 0
numberOfForces = 0
firstFall = 1
prefallFound = False
preimpactFound = False
prefallData = []
preimpactData = list()
prefallDataTs = list()
preimpactDataTs = list()
lastEventEnded = 0

freefallData = [0] * numberOfResampledSamples
impactData = [0] * numberOfResampledSamples

for event in filteredEvents:
    if ((event[0] == EVENT_FREEFALL) and ((tsResampled[event[2]] - tsResampled[event[1]]) >= FREEFALL_SIGNIFICANT)):
        if (firstFall > 0):
            prefallFound = True
            prefallData = [max([event[1] - PREFALL_LENGTH, 0]), event[1]]
            for i in range(max([event[1] - PREFALL_LENGTH, 0]), event[1]):
                prefallDataTs.append(tsResampled[i])
            firstFall = 0
        numberOfSignificantFalls += 1
        freefallData[event[1]] = 100
        lastEventEnded = event[2]
    elif ((event[0] == EVENT_IMPACT) and (event[5] == TYPE_IMPACT_FORCE)):
        numberOfForces += 1
    elif ((event[0] == EVENT_IMPACT) and (event[5] >= TYPE_IMPACT_MEDIUM)):
        numberOfSignificantImpacts += 1
        impactData[event[1]] = 100
        if ((event[1] - lastEventEnded) >= PREIMPACT_LENGTH):
            preimpactData.append([max([event[1] - PREIMPACT_LENGTH, lastEventEnded + 1]), event[1]])
            preimpactDataTs.append([tsResampled[max(event[1] - PREIMPACT_LENGTH, lastEventEnded + 1)], tsResampled[event[1]]])
            preimpactFound = True
        lastEventEnded = event[2]

print("Significant freefall events:", numberOfSignificantFalls)
print("Significant impact events:", numberOfSignificantImpacts)
print("Force impartions:", numberOfForces)

# Analyze prefall data
fallPred = []
if (prefallFound):
    print("Prefall region:", format(prefallDataTs[0], '.2f'), "ms to", format(prefallDataTs[len(prefallDataTs) - 1], '.2f'), "ms, size =", prefallData[1] - prefallData[0])
    formattedPrefallDataset = prefallProcess(xFiltered, yFiltered, zFiltered, prefallData[0], prefallData[1])
    if ((formattedPrefallDataset.empty == False) and (formattedPrefallDataset.size > 0)):
        #svm_classifier = joblib.load('//home//romit//Workspace//Zebra//DeviceAbuse//POC2//prefallClassifier_svm.pkl')
        #rf_classifier = joblib.load('//home//romit//Workspace//Zebra//DeviceAbuse//POC2//prefallClassifier_rf.pkl')
        try:
            svm_classifier = joblib.load('./prefallClassifier_svm.pkl')
            rf_classifier = joblib.load('./prefallClassifier_rf.pkl')
        except IOError:
            print ("File error! Could not load prefall models!")
            print ("CWD: ", os.getcwd());
        except:
            print ("Caught error!")

            svm_pred = svm_classifier.predict(formattedPrefallDataset.iloc[:].values)
            rf_pred = rf_classifier.predict(formattedPrefallDataset.iloc[:].values)

            print ("SVM classification:", svm_pred, "---", convertEventCode(svm_pred[0]))
            print ("RF classification:", rf_pred, "---", convertEventCode(rf_pred[0]))
            fallPred = rf_pred
    else:
        print("Prefall analysis error!")
else:
    print("No significant fall event found. Cannot do prefall analysis")

# Analyse preimpact data
# TODO: Fit preimpact data to ML
impactPred = []
if (preimpactFound):
    i = 0
    for impact in preimpactDataTs:
        print("Preimpact region:", format(impact[0], '.2f'), "ms to", format(impact[1], '.2f'), "ms, size =", preimpactData[i][1] - preimpactData[i][0])
        formattedPreimpactDataset = preimpactProcess(xFiltered, yFiltered, zFiltered, preimpactData[i][0], preimpactData[i][1])
        i += 1
        if ((formattedPreimpactDataset.empty == False) and (formattedPreimpactDataset.size > 0)):
            #svm_classifier = joblib.load('//home//romit//Workspace//Zebra//DeviceAbuse//POC2//preimpactClassifier_svm.pkl')
            #rf_classifier = joblib.load('//home//romit//Workspace//Zebra//DeviceAbuse//POC2//preimpactClassifier_rf.pkl')
            try:
                svm_classifier = joblib.load('./preimpactClassifier_svm.pkl')
                rf_classifier = joblib.load('./preimpactClassifier_rf.pkl')
            except IOError:
                print ("File error! Could not load prefall models!")
                print ("CWD: ", os.getcwd());
            except:
                print ("Caught error!")

            svm_pred = svm_classifier.predict(formattedPreimpactDataset.iloc[:].values)
            rf_pred = rf_classifier.predict(formattedPreimpactDataset.iloc[:].values)

            print ("SVM classification:", svm_pred, "---", convertEventCode(svm_pred[0]))
            print ("RF classification:", rf_pred, "---", convertEventCode(rf_pred[0]))
            impactPred.append(rf_pred)
else:
    if (numberOfSignificantImpacts > 0):
        print ("Not enough preimpact data available. Cannot do preimpact analysis")
    else:
        print ("No significant impact event found. Cannot do preimpact analysis")

# Final analysis. Bring everything together
freefallSummary, impactSummary, finalSummary, imageFileName = detectType(filteredEvents, numberOfSignificantFalls, numberOfSignificantImpacts, fallPred, impactPred)
print("Final summary:")
print("|__  | |\ |  /\  |       /__` |  |  |\/|  |\/|  /\  |__) \ / .");
print("|    | | \| /~~\ |___    .__/ \__/  |  |  |  | /~~\ |  \  |  .");
print(finalSummary)
print("Fall summary:")
print(freefallSummary)
print("Impact summary:")
print(impactSummary)

print(getpass.getuser())

# TEMP: Remove
#text_file = open('/tmp/username.txt', "w")
#text_file.write(getpass.getuser())
#text_file.close()

# 5.0 Create output/folder
try: 
    if not os.path.exists(os.path.join("./","output")):
        os.mkdir(os.path.join("./","output"))
except OSError as error: 
    print(error)  
# Directory 
directory = "output/"+filename
# Parent Directory path 
parent_dir = "./"
# Path 
path = os.path.join(parent_dir, directory) 
try:
    if os.path.exists(path):
	    # Delete folder and containing files
	    shutil.rmtree(path)
    os.mkdir(path) 
except OSError as error: 
    print(error)      
#print("Directory '% s' created" % directory) 


# 5. Generate graphs
# 5.1. Generate the graph image files
plt.figure(num=1, figsize=(8,6), dpi=100)
plt.plot(fileData["TS"], fileData["X"])
plt.plot(fileData["TS"], fileData["Y"])
plt.plot(fileData["TS"], fileData["Z"])
plt.title('X-Y-Z')
plt.legend(['X', 'Y', 'Z'], loc='lower right')
plt.savefig('output/'+filename+'/'+filename+'-xyz.png')

plt.figure(num=2, figsize=(8,6), dpi=100)
plt.plot(fileData["TS"], tsv)
for event in events:
    if (event[0] == EVENT_FREEFALL):
        eventText = "Freefall"
        faceColor = 'blue'
    elif (event[0] == EVENT_IMPACT):
        if (event[5] == TYPE_IMPACT_FORCE):
            eventText = "Force"
            faceColor = 'orange'
        else:
            eventText = "Impact"
            faceColor = 'red'
    else:
        eventText = "Unknown"
        faceColor = 'black'
    x = tsResampled[event[1]]
    y = tsvFiltered[event[1]]
    xtext = x - 1000
    ytext = y - 7
    plt.annotate(eventText, xy=(x,y), xycoords='data', xytext=(xtext, ytext), textcoords='data', arrowprops=dict(facecolor=faceColor, shrink=0.05), horizontalalignment='left', verticalalignment='bottom',) 
plt.title('TSV')
plt.savefig('output/'+filename+'/'+filename+'-tsv.png')

plt.figure(num=3, figsize=(8,6), dpi=100)
plt.plot(fileData["TS"], dtsv)
plt.title('DTSV')
plt.savefig('output/'+filename+'/'+filename+'-dtsv.png')

if (interpolateOn):
    plt.figure(num=4, figsize=(8,6), dpi=100)
    plt.plot(tsInterpolated, xInterpolated)
    plt.plot(tsInterpolated, yInterpolated)
    plt.plot(tsInterpolated, zInterpolated)
    plt.title('X-Y-Z Interpolated')
    plt.legend(['X', 'Y', 'Z'], loc='lower right')
    plt.savefig('output/'+filename+'/'+filename+'-xyz-intp.png')

    plt.figure(num=5, figsize=(8,6), dpi=100)
    plt.plot(tsInterpolated, tsvInterpolated)
    plt.title('TSV Interpolated')
    plt.savefig('output/'+filename+'/'+filename+'-tsv-intp.png')

    plt.figure(num=6, figsize=(8,6), dpi=100)
    plt.plot(tsInterpolated, dtsvInterpolated)
    plt.title('DTSV Interpolated')
    plt.savefig('output/'+filename+'/'+filename+'-dtsv-intp.png')

if (resampleOn):
    plt.figure(num=7, figsize=(8,6), dpi=100)
    plt.plot(tsResampled, xResampled)
    plt.plot(tsResampled, yResampled)
    plt.plot(tsResampled, zResampled)
    plt.title('X-Y-Z Resampled')
    plt.legend(['X', 'Y', 'Z'], loc='lower right')
    plt.savefig('output/'+filename+'/'+filename+'-xyz-resmp.png')

    plt.figure(num=8, figsize=(8,6), dpi=100)
    plt.plot(tsResampled, tsvResampled)
    plt.title('TSV Resampled')
    plt.savefig('output/'+filename+'/'+filename+'-tsv-resmp.png')

    plt.figure(num=9, figsize=(8,6), dpi=100)
    plt.plot(tsResampled, dtsvResampled)
    plt.title('DTSV Resampled')
    plt.savefig('output/'+filename+'/'+filename+'-dtsv-resmp.png')

if (filterOn):
    plt.figure(num=10, figsize=(8,6), dpi=100)
    plt.plot(tsResampled, xFiltered)
    plt.plot(tsResampled, yFiltered)
    plt.plot(tsResampled, zFiltered)
    plt.title('X-Y-Z Filtered')
    plt.legend(['X', 'Y', 'Z'], loc='lower right')
    plt.savefig('output/'+filename+'/'+filename+'-xyz-filt.png')

    plt.figure(num=11, figsize=(8,6), dpi=100)
    plt.plot(tsResampled, tsvFiltered)
    plt.title('TSV Filtered')
    for event in filteredEvents:
        if (event[0] == EVENT_FREEFALL):
            eventText = "Freefall"
            faceColor = 'blue'
        elif (event[0] == EVENT_IMPACT):
            if (event[5] == TYPE_IMPACT_FORCE):
                eventText = "Force"
                faceColor = 'orange'
            else:
                eventText = "Impact"
                faceColor = 'red'
        else:
            eventText = "Unknown"
            faceColor = 'black'
        x = tsResampled[event[1]]
        y = tsvFiltered[event[1]]
        xtext = x - 1000
        ytext = y - 7
        plt.annotate(eventText, xy=(x,y), xycoords='data', xytext=(xtext, ytext), textcoords='data', arrowprops=dict(facecolor=faceColor, shrink=0.05), horizontalalignment='left', verticalalignment='bottom',) 
    plt.savefig('output/'+filename+'/'+filename+'-tsv-filt.png')

    plt.figure(num=12, figsize=(8,6), dpi=100)
    plt.plot(tsResampled, dtsvFiltered)
    plt.title('DTSV Filtered')
    plt.savefig('output/'+filename+'/'+filename+'-dtsv-filt.png')

plt.figure(num=13, figsize=(8, 3), dpi=100)
legendHandles = list()
plt.plot(tsResampled, tsv, 'grey')
currentAxis = plt.gca()
if (prefallFound):
    rect = patches.Rectangle((prefallDataTs[0], 5), (prefallDataTs[len(prefallDataTs) - 1] - prefallDataTs[0]), 15, linewidth=1, facecolor='blue', alpha=0.5)
    currentAxis.add_patch(rect)
for noise in noises:
    rect = patches.Rectangle((tsResampled[noise[0]], 5), (tsResampled[noise[1]] - tsResampled[noise[0]]), 15, linewidth=1, facecolor='orange', alpha=0.5)
    currentAxis.add_patch(rect)
for event in filteredEvents:
    if (event[0] == EVENT_FREEFALL):
        colorVal = 'green'
    elif (event[0] == EVENT_IMPACT):
        if (event[5] == TYPE_IMPACT_FORCE):
            colorVal = 'yellow'
        else:
            colorVal = 'red'
    else:
        colorVal = 'black'
    rect = patches.Rectangle((tsResampled[event[1]], 5), (tsResampled[event[2]] - tsResampled[event[1]]), 15, linewidth=1, facecolor=colorVal, alpha=0.5)
    currentAxis.add_patch(rect)
if (preimpactFound):
    for impact in preimpactDataTs:
        rect = patches.Rectangle((impact[0], 5), (impact[1] - impact[0]), 15, linewidth=1, facecolor='purple', alpha=0.5)
        currentAxis.add_patch(rect)
legendHandles.append(patches.Patch(color='blue', alpha=0.5, label='Prefall'))
legendHandles.append(patches.Patch(color='green', alpha=0.5, label='Freefall'))
legendHandles.append(patches.Patch(color='red', alpha=0.5, label='Impact'))
legendHandles.append(patches.Patch(color='yellow', alpha=0.5, label='Force'))
legendHandles.append(patches.Patch(color='orange', alpha=0.5, label='Noise'))
legendHandles.append(patches.Patch(color='purple', alpha=0.5, label='Preimpact'))
plt.legend(handles=legendHandles)
plt.title('Zones')
plt.savefig('output/'+filename+'/'+filename+'-zone.png')

plt.figure(num=14, figsize=(8, 3), dpi=100)
plt.plot(tsResampled, freefallData)
plt.title('Freefall events')
plt.savefig('output/'+filename+'/'+filename+'-ff.png')

plt.figure(num=15, figsize=(8, 3), dpi=100)
plt.plot(tsResampled, impactData)
plt.title('Impact events')
plt.savefig('output/'+filename+'/'+filename+'-im.png')

text_file = open('output/'+filename+'/'+filename+'-desc.txt', "w")
text_file.write(finalSummary)
text_file.close()

text_file = open('output/'+filename+'/'+filename+'-fin.txt', "w")
text_file.write(freefallSummary)
text_file.close()

text_file = open('output/'+filename+'/'+filename+'-iin.txt', "w")
text_file.write(impactSummary)
text_file.close()

#copyfile('//home//romit//Workspace//Zebra//DeviceAbuse//POC2//'+imageFileName,filename+'-type.png')
copyfile(imageFileName,'output/'+filename+'/'+filename+'-type.png')

if (debugMode):
    # In debug mode, show the graphs in addition to generating the image files
    f, axarr = plt.subplots(3, sharex=True, figsize=(8, 6), dpi=80)
    axarr[0].plot(fileData["TS"], fileData["X"])
    axarr[0].plot(fileData["TS"], fileData["Y"])
    axarr[0].plot(fileData["TS"], fileData["Z"])
    axarr[0].set_title('X-Y-Z')

    axarr[1].plot(fileData["TS"], tsv)
    axarr[1].set_title('TSV')

    axarr[2].plot(fileData["TS"], dtsv)
    axarr[2].set_title('DTSV')
    plt.show(block=False)
    
    if (interpolateOn):
        print('Number of interpolated samples:', numberOfInterpolatedSamples)
        f, axarr = plt.subplots(3, sharex=True, figsize=(8, 6), dpi=80)
        axarr[0].plot(tsInterpolated, xInterpolated)
        axarr[0].plot(tsInterpolated, yInterpolated)
        axarr[0].plot(tsInterpolated, zInterpolated)
        axarr[0].set_title('X-Y-Z Interpolated')
        axarr[1].plot(tsInterpolated, tsvInterpolated)
        axarr[1].set_title('TSV Interpolated')
        axarr[2].plot(tsInterpolated, dtsvInterpolated)
        axarr[2].set_title('DTSV Interpolated')
        plt.show(block=False)
        # Also dump the data in a file
        df = pd.DataFrame.from_records(np.column_stack([tsInterpolated, xInterpolated, yInterpolated, zInterpolated, tsvInterpolated, dtsvInterpolated]), columns=["TS", "X", "Y", "Z", "TSV", "DTSV"])
        df.to_csv(filename+' - intp.csv')
        
    if (resampleOn):
        print('Number of resampled samples:', numberOfResampledSamples)
        f, axarr = plt.subplots(3, sharex=True, figsize=(8, 6), dpi=80)
        axarr[0].plot(tsResampled, xResampled)
        axarr[0].plot(tsResampled, yResampled)
        axarr[0].plot(tsResampled, zResampled)
        axarr[0].set_title('X-Y-Z Resampled')
        axarr[1].plot(tsResampled, tsvResampled)
        axarr[1].set_title('TSV Resampled')
        axarr[2].plot(tsResampled, dtsvResampled)
        axarr[2].set_title('DTSV Resampled')
        plt.show(block=False)
        # Also dump the data in a file
        df = pd.DataFrame.from_records(np.column_stack([tsResampled, xResampled, yResampled, zResampled, tsvResampled, dtsvResampled]), columns=["TS", "X", "Y", "Z", "TSV", "DTSV"])
        df.to_csv(filename+' - resmpl.csv')

    if (filterOn):
        print('Number of filtered samples:', numberOfResampledSamples)
        f, axarr = plt.subplots(3, sharex=True, figsize=(8, 6), dpi=80)
        axarr[0].plot(tsResampled, xFiltered)
        axarr[0].plot(tsResampled, yFiltered)
        axarr[0].plot(tsResampled, zFiltered)
        axarr[0].set_title('X-Y-Z Filtered')
        axarr[1].plot(tsResampled, tsvFiltered)
        axarr[1].set_title('TSV Filtered')
        axarr[2].plot(tsResampled, dtsvFiltered)
        axarr[2].set_title('DTSV Filtered')
        plt.show(block=False)
        # Also dump the data in a file
        df = pd.DataFrame.from_records(np.column_stack([tsResampled, xFiltered, yFiltered, zFiltered, tsvFiltered, dtsvFiltered]), columns=["TS", "X", "Y", "Z", "TSV", "DTSV"])
        df.to_csv(filename+' - filt.csv')

    plt.show()

