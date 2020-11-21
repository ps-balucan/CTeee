package com.onenineeight.cteee;

import android.content.SharedPreferences;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;



import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {
    public static final String SHARED_PREFS = "sharedPrefs";

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


        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Long userExposure = sharedPreferences.getLong("durationExposed", 0);
        if ( userExposure> 0)
        {
            CardView card = view.findViewById(R.id.safeCard); //find safeCard properly
            card.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.NiceRed));
            TextView safe = view.findViewById(R.id.safeOrAtRisk);
            safe.setText("At Risk"); //add code to revert back to safe
            TextView durationtext = view.findViewById(R.id.homepage_overview_duration);
            userExposure = userExposure / 60;
            durationtext.setText(userExposure.toString());
        }
        else
        {
            CardView card = view.findViewById(R.id.safeCard); //find safeCard properly
            TextView safe = view.findViewById(R.id.safeOrAtRisk);
            safe.setText("Safe"); //add code to revert back to safe
        }
        return view;
    }
}
