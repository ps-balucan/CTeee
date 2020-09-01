package com.onenineeight.cteee;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LogListAdapter extends ArrayAdapter<BluetoothLog> {
    private Context mContext;
    int mResource;

    public LogListAdapter(@NonNull Context context, int resource, @NonNull List<BluetoothLog> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = getItem(position).getBeacon();
        Long time = getItem(position).getTime();
        Long duration = getItem(position).getDuration();

        BluetoothLog bluetoothLog = new BluetoothLog(name, time, duration);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvbeacon = (TextView) convertView.findViewById(R.id.textview1);
        TextView tvtime = (TextView) convertView.findViewById(R.id.textview2);
        TextView tvduration = (TextView) convertView.findViewById(R.id.textview3);


        tvbeacon.setText(name);
        tvtime.setText(getDateTimeFromTimeStamp(time, "h:mm a dd MMMM yyyy"));
        //tvtime.setText(String.valueOf(time));
        tvduration.setText(duration.toString());
        return convertView;
    }

    public static String getDateTimeFromTimeStamp(Long time, String mDateFormat) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(mDateFormat, Locale.US);
        //dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date dateTime = new Date(time*1000);
        return dateFormat.format(dateTime);
    }
}
