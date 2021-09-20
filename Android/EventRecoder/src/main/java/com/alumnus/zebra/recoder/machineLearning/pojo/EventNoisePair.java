package com.alumnus.zebra.recoder.machineLearning.pojo;

import java.util.ArrayList;

public class EventNoisePair {

    public ArrayList<DetectedEvent> detectedEvents;
    public ArrayList<NoiseZone> noiseZones;

    public EventNoisePair(ArrayList<DetectedEvent> detectedEvents, ArrayList<NoiseZone> noiseZones) {
        this.detectedEvents = detectedEvents;
        this.noiseZones = noiseZones;
    }
}
