package com.luihin903.setstimer;

import java.io.Serializable;

public class Time implements Serializable {

    private int hour;
    private int minute;
    private int second;

    public Time(int hour, int minute, int second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public long toMillisecond() {
        return (hour * 3600 + minute * 60 + second) * 1000;
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

}
