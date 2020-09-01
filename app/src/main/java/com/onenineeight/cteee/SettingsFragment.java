package com.onenineeight.cteee;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

public class SettingsFragment extends Fragment {
    private List<BluetoothLog> results;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        final LogDbHelper dbHelper = new LogDbHelper(getActivity());


        final Button reportBtn = v.findViewById(R.id.create_report_btn);
        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(), "Report created", Toast.LENGTH_LONG).show();
                ReportMaker.generateReport(dbHelper, 3, "2020-09-01");

            }
        });

        return v;
    }
}
