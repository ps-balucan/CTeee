package com.onenineeight.cteee;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //add code for detecting bluetooth

        FloatingActionButton button = view.findViewById(R.id.homeTestedPositive);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // do something
                /*
                BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
                BottomNavigationView bottomNavigationView;
                bottomNavigationView = view.findViewById()
                bottomNavigationView.setSelectedItemId(R.id.item_id);
                */
                Fragment selectedFragment = null;
                selectedFragment = new ReportFragment();
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

            }
        });

        return view;
    }
}
