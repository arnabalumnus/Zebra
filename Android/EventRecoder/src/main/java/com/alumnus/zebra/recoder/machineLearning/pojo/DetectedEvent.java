package com.alumnus.zebra.recoder.machineLearning.pojo;

public class DetectedEvent {
    public int event_type; //EVENT (FREE FALL OR EVENT IMPACT) enum
    public int eventStart;
    public int eventEnd;

    public double minTsv;
    public boolean spinDetected;

    public double maxTsv;
    public double dTsv;
    public int impactType;

    //EVENT FREE FALL
    public DetectedEvent(int event_type, int eventStart, int eventEnd, double minTsv, boolean spinDetected) {
        this.event_type = event_type;
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
        this.minTsv = minTsv;
        this.spinDetected = spinDetected;
    }

    //EVENT IMPACT
    public DetectedEvent(int event_type, int eventStart, int eventEnd, double maxTsv, double dTsv, int impactType) {
        this.event_type = event_type;
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
        this.maxTsv = maxTsv;
        this.dTsv = dTsv;
        this.impactType = impactType;
    }

    @Override
    public String toString() {
        return "DetectedEvent{" +
                "event_type=" + event_type +
                ", eventStart=" + eventStart +
                ", eventEnd=" + eventEnd +
                ", minTsv=" + minTsv +
                ", spinDetected=" + spinDetected +
                ", maxTsv=" + maxTsv +
                ", dTsv=" + dTsv +
                ", impactType=" + impactType +
                '}';
    }
}
