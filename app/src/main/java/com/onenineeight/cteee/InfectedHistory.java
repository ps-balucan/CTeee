package com.onenineeight.cteee;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class InfectedHistory {
    @SerializedName("duration")
    @Expose
    private Double duration;
    @SerializedName("location")
    @Expose
    private Integer location;
    @SerializedName("time")
    @Expose
    private Integer time;

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public Integer getLocation() {
        return location;
    }

    public void setLocation(Integer location) {
        this.location = location;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }
}
