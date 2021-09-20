package com.alumnus.zebra.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DateFormatterTest {

    @Test
    public void getTimeStamp() {
        long timeInMillisecond = 1615964706453L;
        String time_stamp = DateFormatter.getTimeStamp(timeInMillisecond);
        System.out.println(time_stamp);
        assertEquals("17/03/2021 12:35:06", time_stamp);
    }

    @Test
    public void getTimeStampFileName() {
        long timeInMillisecond = 1615964706453L;
        String time_stamp = DateFormatter.getTimeStampFileName(timeInMillisecond);
        System.out.println(time_stamp);
        assertEquals("2021, Mar-17 Time-12 35 06", time_stamp);
    }
}