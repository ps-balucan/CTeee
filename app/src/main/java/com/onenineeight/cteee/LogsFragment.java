package com.onenineeight.cteee;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

public class LogsFragment extends Fragment {
    private ListView mListView;
    private List<BluetoothLog> myLogList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_logs, container, false);
        View v = inflater.inflate(R.layout.fragment_logs, container, false);
        LogDbHelper dbHelper = new LogDbHelper(getActivity());

        String time1 = "2020-08-09 17:00";
        String time2 = "2020-08-09 18:00";
        myLogList = dbHelper.getAllLogs();
        //myLogList = dbHelper.getLogsByDate(time1, time2);

        mListView = (ListView) v.findViewById(R.id.listView);
        LogListAdapter adapter = new LogListAdapter(getActivity(), R.layout.adapter_view_layout, myLogList);
        mListView.setAdapter(adapter);

        dbHelper.close();

        return v;
    }
}
