package com.alumnus.zebra.machineLearning.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class CalculatorTest {

    @Test
    fun calculateTSV() {
        assertEquals("Error in TSV calculation", 9.81, Calculator.calculateTSV(9.81, 0.0, 0.0), 0.0)
        assertEquals("Error in TSV calculation", 9.81, Calculator.calculateTSV(0.0, 9.81, 0.0), 0.0)
        assertEquals("Error in TSV calculation", 9.81, Calculator.calculateTSV(0.0, 0.0, 9.81), 0.0)
        assertEquals("Error in TSV calculation", 9.81, Calculator.calculateTSV(-0.703000009059906, 6.072000026702881, 7.658999919891357), 0.03)
        assertEquals("Error in TSV calculation", 12.38, Calculator.calculateTSV(-2.799999952316284, 6.138999938964844, 10.388999938964844), 0.03)
        assertEquals("Error in TSV calculation", 8.6, Calculator.calculateTSV(0.5220000147819519, 5.545000076293945, 6.558000087738037), 0.03)
    }

    @Test
    fun calculateDTSV() {
    }

    @Test
    fun estimateDistance() {
        assertEquals(20.0, Calculator.estimateDistance(1000.0),0.0)
        assertEquals(79.0, Calculator.estimateDistance(2000.0),0.0)
    }
}