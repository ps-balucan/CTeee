package com.onenineeight.cteee;


import java.util.HashMap;
import java.util.List;

public class AggregateReport {

    //@SerializedName("date")
    private String date;

    //@SerializedName("logs")
    private HashMap<Integer, List<Integer>> map;


    public AggregateReport(String date, HashMap<Integer, List<Integer> > map) {
        this.map = map;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public HashMap<Integer, List<Integer>> getMap() {
        return map;
    }
}