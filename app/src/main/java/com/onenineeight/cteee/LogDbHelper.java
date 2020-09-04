package com.onenineeight.cteee;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.onenineeight.cteee.LogContract.*;

public class LogDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "MyLogs.db";
    public static final int DATABASE_VERSION = 1;
    private SQLiteDatabase db;

    public LogDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        final String SQL_CREATE_LOG_TABLE = "CREATE TABLE " +
                LogTable.TABLE_NAME + " ( " +
                LogTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LogTable.COLUMN_LOG + " TEXT, " +
                LogTable.COLUMN_1 + " LONG," +
                LogTable.COLUMN_2 + " LONG" +
                ")";

        db.execSQL(SQL_CREATE_LOG_TABLE);
        fillSampleLogs();
    }
    public static Long persistDate(Date date) {
        if (date != null) {
            return date.getTime()/1000;
        }
        return null;
    }

    private void fillSampleLogs() {
        Log.i("SQLite", "filling sample logs");

        Long j = 12345L;
        String testDateString = "2020-08-24 09:00";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Date createdTime = new Date();
//        try {
//            createdTime = df.parse(testDateString);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        //Date createdTime = Calendar.getInstance().getTime();
        int diff = 180;
        Date newTime = new Date(createdTime.getTime() + diff*2*60000);
        Date newnewTime = new Date(createdTime.getTime() + diff*4*60000);
        //Log.d("LOG LIST HERE", String.valueOf(createdTime.getTime()));


        BluetoothLog b1 = new BluetoothLog("Palma",  persistDate(createdTime), 9000L);
        addLog(b1);
        BluetoothLog b2 = new BluetoothLog("Math",  persistDate(newTime), j);
        addLog(b2);
        BluetoothLog b3 = new BluetoothLog("EEE", persistDate(newnewTime), j);
        addLog(b3);

    }

    private void addLog(BluetoothLog bluetoothLog){
        ContentValues cv = new ContentValues();
        cv.put(LogTable.COLUMN_LOG, bluetoothLog.getBeacon());
        cv.put(LogTable.COLUMN_1, bluetoothLog.getTime());
        cv.put(LogTable.COLUMN_2, bluetoothLog.getDuration());
        db.insert(LogTable.TABLE_NAME, null, cv);

        Log.i("SQLite", "Entry created.");
    }

    public void insertLog(BluetoothLog bluetoothLog){
        ContentValues cv = new ContentValues();
        cv.put(LogTable.COLUMN_LOG, bluetoothLog.getBeacon());
        cv.put(LogTable.COLUMN_1, bluetoothLog.getTime());
        cv.put(LogTable.COLUMN_2, bluetoothLog.getDuration());

        db = getWritableDatabase();
        db.insert(LogTable.TABLE_NAME, null, cv);

        Log.i("SQLite", "Entry inserted.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + LogTable.TABLE_NAME);
        onCreate(db);
    }

    public List<BluetoothLog> getAllLogs(){
        List<BluetoothLog> bluetoothLogList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + LogTable.TABLE_NAME, null);
        if (c.moveToFirst()){
            do {
                BluetoothLog bluetoothLog = new BluetoothLog();
                bluetoothLog.setBeacon(c.getString(c.getColumnIndex(LogTable.COLUMN_LOG)));
                bluetoothLog.setTime(c.getLong(c.getColumnIndex(LogTable.COLUMN_1)));
                bluetoothLog.setDuration(c.getLong(c.getColumnIndex(LogTable.COLUMN_2)));
                bluetoothLogList.add(bluetoothLog);
            } while(c.moveToNext());
        }
        c.close();
        return bluetoothLogList;
    }

    public List<BluetoothLog> getLogsByDate(Long timeA, Long timeB){//(String time1, String time2){
        List<BluetoothLog> bluetoothLogList = new ArrayList<>();
        db = getReadableDatabase();

//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

//        Date timeA = null;
//        Date timeB = null;
//        try {
//            timeA = df.parse(time1);
//            timeB = df.parse(time2);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        Log.i("TIME" , timeA.toString());

        //SELECT * FROM log_table WHERE datetime(time/1000, 'unixepoch', 'localtime')  BETWEEN "2020-08-25 05:00" AND "2020-08-25 22:00" OR datetime(time/1000 + duration, 'unixepoch', 'localtime') BETWEEN "2020-08-25 05:00" AND "2020-08-25 22:00";
        //String myQuery = "SELECT * FROM " + LogTable.TABLE_NAME + " WHERE datetime(time/1000, 'unixepoch', 'localtime') BETWEEN " + time1 + " AND " +time2;
        String myQuery = "SELECT * FROM " + LogTable.TABLE_NAME + " WHERE time <= " + timeB + " AND (time+duration) >= " + timeA;
        Log.i("QUERY" , myQuery);
        Cursor c = db.rawQuery(myQuery , null);
        if (c.moveToFirst()){
            do {
                BluetoothLog bluetoothLog = new BluetoothLog();
                bluetoothLog.setBeacon(c.getString(c.getColumnIndex(LogTable.COLUMN_LOG)));
                bluetoothLog.setTime(c.getLong(c.getColumnIndex(LogTable.COLUMN_1)));
                bluetoothLog.setDuration(c.getLong(c.getColumnIndex(LogTable.COLUMN_2)));
                bluetoothLogList.add(bluetoothLog);
            } while(c.moveToNext());
        }
        c.close();
        return bluetoothLogList;
    }

    public List<BluetoothLog> getLogsByDateWithBeacon(Long timeA, Long timeB, String Beacon ){//(String time1, String time2){
        List<BluetoothLog> bluetoothLogList = new ArrayList<>();
        db = getReadableDatabase();

//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

//        Date timeA = null;
//        Date timeB = null;
//        try {
//            timeA = df.parse(time1);
//            timeB = df.parse(time2);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        Log.i("TIME" , timeA.toString());

        //SELECT * FROM log_table WHERE datetime(time/1000, 'unixepoch', 'localtime')  BETWEEN "2020-08-25 05:00" AND "2020-08-25 22:00" OR datetime(time/1000 + duration, 'unixepoch', 'localtime') BETWEEN "2020-08-25 05:00" AND "2020-08-25 22:00";
        //String myQuery = "SELECT * FROM " + LogTable.TABLE_NAME + " WHERE datetime(time/1000, 'unixepoch', 'localtime') BETWEEN " + time1 + " AND " +time2;
        String myQuery = "SELECT * FROM " + LogTable.TABLE_NAME + " WHERE time <= " + timeB + " AND (time+duration) >= " + timeA + " AND beacon==" + Beacon;
        Log.i("QUERY" , myQuery);
        Cursor c = db.rawQuery(myQuery , null);
        if (c.moveToFirst()){
            do {
                BluetoothLog bluetoothLog = new BluetoothLog();
                bluetoothLog.setBeacon(c.getString(c.getColumnIndex(LogTable.COLUMN_LOG)));
                bluetoothLog.setTime(c.getLong(c.getColumnIndex(LogTable.COLUMN_1)));
                bluetoothLog.setDuration(c.getLong(c.getColumnIndex(LogTable.COLUMN_2)));
                bluetoothLogList.add(bluetoothLog);
            } while(c.moveToNext());
        }
        c.close();
        return bluetoothLogList;
    }

}
