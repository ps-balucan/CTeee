package com.onenineeight.cteee;


import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;

public class AggregateReport {

    @SerializedName("date")
    private String date;

    @SerializedName("logs")
    private List<List<Integer>> list;


    public AggregateReport(String date, List<List<Integer>> list) {
        this.list = list;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public List<List<Integer>> getList() {
        return list;
    }
}