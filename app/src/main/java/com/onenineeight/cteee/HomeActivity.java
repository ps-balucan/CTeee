package com.onenineeight.cteee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private List<BluetoothLog> bluetoothLogList;
    //private List<BluetoothLog> myLogList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //PART 1. mobile db setup
        LogDbHelper dbHelper = new LogDbHelper(this);
        bluetoothLogList = dbHelper.getAllLogs();
        Log.i("BluetoothLogList", Boolean.toString(bluetoothLogList.isEmpty()));
        //ListView mListView = (ListView) findViewById(R.id.listView);

        //"2020-08-25 05:00" AND "2020-08-25 22:00"
//        String time1 = "2020-08-24 10:00";
//        String time2 = "2020-08-24 11:00";
        //myLogList = dbHelper.getLogsByDate(time1, time2);

//        LogListAdapter adapter = new LogListAdapter(this, R.layout.adapter_view_layout, myLogList);
//        mListView.setAdapter(adapter);


        //PART 2. bottom nav bar setup
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()){
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_logs:
                            selectedFragment = new LogsFragment();
                            break;
                        case R.id.nav_settings:
                            selectedFragment = new SettingsFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                    return true;
                }
            };
}