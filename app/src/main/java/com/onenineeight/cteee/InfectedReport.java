package com.onenineeight.cteee;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class InfectedReport<i> {
    @SerializedName("date_tested")
    private String date_tested;

    @SerializedName("num_log")
    private int num_log;

    @SerializedName("logs")
    private HashMap<Integer, InfectedReportDetails> map;

    public InfectedReport(String date_tested, int num_log, HashMap<Integer, InfectedReportDetails> map) {
        this.map = map;
        this.date_tested = date_tested;
        this.num_log = num_log;
    }


    public HashMap<Integer, InfectedReportDetails> getMap() {
        return map;
    }

    public String getDate_tested() {
        return date_tested;
    }

    public int getNum_logs() {
        return num_log;
    }
}
