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


        HashMap<Integer, List<Integer>> map = new HashMap<> ();


        for (int i = 0; i < 24 ; i++)
        {
            results = dbHelper.getLogsByDate((startTime+i*HOUR), (startTime+i*HOUR+ HOUR));
            List<Integer> loc_counts = new ArrayList<Integer>(Collections.nCopies(300, 0));
            if (results.isEmpty())
            {
                Log.d(TAG, "generateReport: result is empty for " +  (startTime+i*HOUR) + " and " + (startTime+i*HOUR+ HOUR));
            }
            else
            {
                Log.d(TAG, "generateReport: result is not empty for " +  (startTime+i*HOUR) + " and " + (startTime+i*HOUR+ HOUR));
                for (BluetoothLog log :results)
                {
                    Log.d(TAG, "Result: " + log.getBeacon());
                    loc_counts.set((Integer.valueOf(log.getBeacon()) - 1), 1);

                }
            }

            map.put(i, loc_counts);
            //Log.d(TAG, "generateReport: Querying for time-> " + (startTime+i*HOUR) + " and " + (startTime+i*HOUR+ HOUR));
        }


        AggregateReport aggregateReport = new AggregateReport("2020-08-10", map);
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
}
