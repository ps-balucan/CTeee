package com.onenineeight.cteee;

public class BluetoothLog
{
    private String beacon;
    private Long time;
    private Long duration;


    public BluetoothLog(){}
    public BluetoothLog(String beacon, Long time, Long duration) {
        this.beacon = beacon;
        this.time = time;
        this.duration = duration;
    }

    public String getBeacon() {
        return beacon;
    }

    public void setBeacon(String beacon) {
        this.beacon = beacon;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }
}
