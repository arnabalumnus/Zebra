package com.alumnus.zebra.pojo


/**
 * Data class for Acceleration
 * Contains acceleration Numeric values
 * @author Arnab Kundu
 */
class AccelerationNumericData {
    var ts: Long = 0
    var x = 0f
    var y = 0f
    var z = 0f

    constructor() {}

    constructor(ts: Long, x: Float, y: Float, z: Float) {
        this.ts = ts
        this.x = x
        this.y = y
        this.z = z
    }
}