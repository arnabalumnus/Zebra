package com.alumnus.zebra.machineLearning.pojo;

import org.jetbrains.annotations.NotNull;

public class NoiseZone {

    public int noiseStart;
    public int i;

    public NoiseZone(int noiseStart, int i) {
        this.noiseStart = noiseStart;
        this.i = i;
    }

    @NotNull
    @Override
    public String toString() {
        return "[" + noiseStart + "," + i + "]";
    }
}
