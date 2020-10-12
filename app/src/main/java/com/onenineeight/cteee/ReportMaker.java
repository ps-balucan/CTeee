package com.onenineeight.cteee;

import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.math3.distribution.BinomialDistribution;

public final class ReportMaker {
    public static final String TAG = "ReportMaker";

    public static final int HOUR = 60 * 60;
    public static final int MINUTE = 60;
    public static final int DAY = 86400;
    private ReportMaker() {}

    public static AggregateReport generateReport(LogDbHelper dbHelper, int days , String startDate) {
        //format should be "yyyy-MM-dd"

        List<BluetoothLog> results;


        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        Date timeA = null;
        Date timeB = null;
        Date timeStart = null;

        try {
            timeStart = df.parse(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Long startTime = timeStart.getTime()/1000 - DAY;
        Log.d(TAG, "generateReport: We will check starting " + startDate.toString());
        Log.d(TAG, "generateReport: Yesterday, In seconds since Epoch that is " + startTime);


        //HashMap<Integer, List<Integer>> map = new HashMap<> ();
        List<List<Integer>> list = new ArrayList<>();

        //initializing values for DP Noising
        int trials = 1;
        double e = 11; //Epsilon; privacy budget
        double p = 1/(java.lang.Math.exp(e/2)+1);
        int loc = 300; //number of locations in the system

        for (int i = 0; i < 24 ; i++)
        {
            results = dbHelper.getLogsByDate((startTime+i*HOUR), (startTime+i*HOUR+ HOUR));
            List<Integer> loc_counts = new ArrayList<Integer>(Collections.nCopies(loc, 0));
            if (results.isEmpty()) {
                Log.d(TAG, "generateReport: result is empty for " + (startTime + i * HOUR) + " and " + (startTime + i * HOUR + HOUR));
            }
            else
            {
                Log.d(TAG, "generateReport: result is not empty for " +  (startTime+i*HOUR) + " and " + (startTime+i*HOUR+ HOUR));
                for (BluetoothLog log :results)
                {
                    Log.d(TAG, "Result: " + log.getBeacon());
                    loc_counts.set(Integer.valueOf(log.getBeacon()) - 1, 1);
                }

                //DP implementation, initiated after data collected from BluetoothLog
                for(int j = 0; j<loc; j++){
                    BinomialDistribution out = new BinomialDistribution(trials, p);
                    if(out.sample()==1) { //probability of adding noise
                        BinomialDistribution noise = new BinomialDistribution(trials, 0.5);
                        if(noise.sample()==1){  //50/50 chance of adding +/- noise
                            loc_counts.set(j,loc_counts.get(j)+1);
                        } else {
                            loc_counts.set(j,loc_counts.get(j)-1);
                        }
                    }
                }
                //End of DP implementation

            }

            //map.put(i, loc_counts);
            list.add(loc_counts);
            //Log.d(TAG, "generateReport: Querying for time-> " + (startTime+i*HOUR) + " and " + (startTime+i*HOUR+ HOUR));
        }


        AggregateReport aggregateReport = new AggregateReport("2020-08-10", list);
        Log.d(TAG, "generateReport: " + aggregateReport);

        return aggregateReport;
//
//        results = dbHelper.getLogsByDate(timeA, timeB);
//        if (results.isEmpty())
//        {
//            Log.d(TAG, "generateReport: result is empty. days is " + days );
//        }
//        else
//        {
//            Log.d(TAG, "generateReport: result is not empty. days is " + days);
//        }
//
//        String time1 = "2020-08-09 17:00";
//        String time2 = "2020-08-09 18:00";
//        for () {
//
//        }
    }

    public static boolean checkExposure(LogDbHelper dbHelper, List<InfectedHistory> infectedHistories){
        List<BluetoothLog> results;
        for (InfectedHistory infectedHistory: infectedHistories)
        {
            Long timeA = infectedHistory.getTime().longValue();
            Long timeB = infectedHistory.getTime().longValue() + infectedHistory.getDuration().longValue();
            results = dbHelper.getLogsByDateWithBeacon(timeA, timeB, infectedHistory.getLocation().toString());

            if (!results.isEmpty())
            {
                Log.d(TAG, "checkExposure: Exposure Detected!");
                return true;
            }
            else
            {
                Log.d(TAG, "checkExposure: clear. checking next");
            }

        }
        Log.d(TAG, "checkExposure: No Exposure detected.");
        return false;
    }

    public static List<BluetoothLog> collectCovidHistory(LogDbHelper logDbHelper, String startDate){
        //List<InfectedHistory> infectedHistories;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        Date timeA = null;
        Date timeB = null;
        Date timeStart = null;

        try {
            timeStart = df.parse(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Long endTime = timeStart.getTime()/1000;
        Long startTime = endTime  - 14*DAY;

        Log.d(TAG, "collectCovid: We will check starting " + startTime);
        Log.d(TAG, "collectCovid: EndTime, In seconds since Epoch that is " + endTime);

        List<BluetoothLog> results;
        results = logDbHelper.getLogsByDate((startTime), (endTime));
        if (results.isEmpty())
        {
            Log.d(TAG, "collectCovidHistory: Result is empty");
        }
        return results;
    }
}
