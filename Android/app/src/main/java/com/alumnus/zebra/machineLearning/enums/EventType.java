package com.alumnus.zebra.machineLearning.enums;

public enum EventType {
    EVENT_IMPACT(1), EVENT_FREEFALL(2);

    private final int value;

    EventType(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }
}
