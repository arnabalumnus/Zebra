package com.alumnus.zebra.utils

import org.junit.Test


object TestHelper {

    @Test
    fun delay(timeInMilliSecond: Int = 300) {
        try {
            Thread.sleep(timeInMilliSecond.toLong())
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}