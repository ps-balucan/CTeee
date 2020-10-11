package com.onenineeight.cteee;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReportFragment extends Fragment {
    private List<BluetoothLog> results;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    public static final String TAG = "ReportFragment";
    private LogDbHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_report, container, false);
        dbHelper = new LogDbHelper(getActivity());




        //rest functionality
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        final OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(45, TimeUnit.SECONDS)
                .readTimeout(45, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                //.baseUrl("https://5n8g4h5f3m.execute-api.ap-southeast-1.amazonaws.com/v2/")
                .baseUrl("https://1cgw622rr4.execute-api.ap-southeast-1.amazonaws.com/test2/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        final Button reportBtn = v.findViewById(R.id.submit_report);
        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(), "Report created", Toast.LENGTH_LONG).show();
                //ReportMaker.generateReport(dbHelper, 3, "2020-09-01");
                postReport();
            }
        });
        return v;
    }

    private void postReport() {

        AggregateReport aggregateReport = ReportMaker.generateReport(dbHelper, 3, "2020-09-01");


        Call<Void> call = jsonPlaceHolderApi.postDPReport(aggregateReport);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()){

                    return;
                }
                Log.d(TAG, "onResponse: Code->" + response.code());
                Log.d(TAG, "onResponse: Body->" + response.body());

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
}

}
