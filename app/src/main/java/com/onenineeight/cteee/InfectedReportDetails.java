package com.onenineeight.cteee;

import com.google.gson.annotations.SerializedName;

public class InfectedReportDetails
{
    @SerializedName("location")
    private int location;

    @SerializedName("time")
    private String time;

    @SerializedName("duration")
    private int duration;

    public InfectedReportDetails(int location, String time, int duration) {
        this.location = location;
        this.time = time;
        this.duration = duration;
    }

    public int getLocation() {
        return location;
    }

    public String getTime() {
        return time;
    }

    public int getDuration() {
        return duration;
    }
}
