package com.onenineeight.cteee;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

//import com.amazonaws.mobile.client.AWSMobileClient;
//import com.amazonaws.mobile.client.results.Tokens;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.results.Tokens;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
//import com.onenineeight.cteee.ApiClient;
public class SettingsFragment extends Fragment {
    private List<BluetoothLog> results;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    public static final String TAG = "SettingsFragment";
    private LogDbHelper dbHelper;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final boolean isCovidPositve = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        dbHelper = new LogDbHelper(getActivity());




        //rest functionality
        //rest functionality{
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        //OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
//        final OkHttpClient client = new OkHttpClient.Builder()
//                .addInterceptor(interceptor)
//                .connectTimeout(20, TimeUnit.SECONDS)
//                .writeTimeout(45, TimeUnit.SECONDS)
//                .readTimeout(45, TimeUnit.SECONDS)
//                .build();
//
//        Retrofit retrofit = new Retrofit.Builder()
//                //.baseUrl("https://5n8g4h5f3m.execute-api.ap-southeast-1.amazonaws.com/v2/")
//                .baseUrl("https://1cgw622rr4.execute-api.ap-southeast-1.amazonaws.com/test2/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .client(client)
//                .build();
        //}

        jsonPlaceHolderApi = ApiClient.getInstance().create(JsonPlaceHolderApi.class);

        final Button reportBtn = v.findViewById(R.id.create_report_btn);
        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(), "Report created", Toast.LENGTH_LONG).show();
                //ReportMaker.generateReport(dbHelper, 3, "2020-09-01");
                postReport();
            }
        });

        final Button getReportBtn = v.findViewById(R.id.download_reports_btn);
        getReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(), "Report created", Toast.LENGTH_LONG).show();
                //ReportMaker.generateReport(dbHelper, 3, "2020-09-01");
                getLocData();
            }
        });

        final Button getAccessToken = v.findViewById(R.id.check_reports_btn);
        getAccessToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(), "Report created", Toast.LENGTH_LONG).show();
                //ReportMaker.generateReport(dbHelper, 3, "2020-09-01");
                //getAccessToken();
                check_username();
            }
        });

        final Button reportCovidBtn = v.findViewById(R.id.report_covid_status_btn);
        reportCovidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(), "Report created", Toast.LENGTH_LONG).show();
                //ReportMaker.generateReport(dbHelper, 3, "2020-09-01");
                reportCovid();
            }
        });
        return v;
    }

    private void check_username() {
        final String username = AWSMobileClient.getInstance().getUsername();
        String token = "";
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                final String token = instanceIdResult.getToken();
                Log.d(TAG, "getLocData: " + username );
                Log.d(TAG, "onSuccess: " + token);

                AWSMobileClient.getInstance().getTokens(new com.amazonaws.mobile.client.Callback<Tokens>() {
                    @Override
                    public void onResult(Tokens result) {
                        String AccessToken = result.getAccessToken().getTokenString();
                        Log.d(TAG, "Access Token: " + AccessToken);
                        DeleteUserRequest deleteUserRequest = new DeleteUserRequest();
                        deleteUserRequest.setUsername(username);
                        deleteUserRequest.setToken(token);

                        Call<Void> call = jsonPlaceHolderApi.deleteUser(AccessToken, deleteUserRequest);

                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (!response.isSuccessful()){

                                    return;
                                }
                                Log.d(TAG, "onResponse: Code->" + response.code());
                                Log.d(TAG, "onResponse: Body->" + response.body());
                                if (AWSMobileClient.getInstance().isSignedIn())
                                {
                                    AWSMobileClient.getInstance().signOut();
                                    Log.d(TAG, "check_username: you are signed in and now signed out");
                                }
                                Intent i = new Intent(getActivity(), AuthenticationActivity.class);
                                startActivity(i);
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Log.d(TAG, "onFailure: " + t.getMessage());
                                Toast.makeText(getActivity(), "Opt-out has failed." , Toast.LENGTH_LONG);
                            }
                        });


                    }

                    @Override
                    public void onError(Exception e) {
                        if(e.getMessage() != null)
                        {
                            Log.e("Err", e.getMessage());
                        }
                    }
                });
            }
        });



    }

    private void postReport() {
        long startTime = System.nanoTime();
        AggregateReport aggregateReport = ReportMaker.generateReport(dbHelper, 3, "2020-08-10");
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
        Log.d(TAG, "postReport: EXECUTION TIME IS " + duration);

//        AWSMobileClient.getInstance().getTokens(new com.amazonaws.mobile.client.Callback<Tokens>() {
//            @Override
//            public void onResult(Tokens result) {
//                String AccessToken = result.getAccessToken().getTokenString();
//                Log.d(TAG, "Access Token: " + AccessToken);
//                AggregateReport aggregateReport = ReportMaker.generateReport(dbHelper, 3, "2020-08-10");
//
//
//                Call<Void> call = jsonPlaceHolderApi.postDPReport(AccessToken, aggregateReport);
//
//                call.enqueue(new Callback<Void>() {
//                    @Override
//                    public void onResponse(Call<Void> call, Response<Void> response) {
//                        if (!response.isSuccessful()){
//
//                            return;
//                        }
//                        Log.d(TAG, "onResponse: Code->" + response.code());
//                        Log.d(TAG, "onResponse: Body->" + response.body());
//
//                    }
//
//                    @Override
//                    public void onFailure(Call<Void> call, Throwable t) {
//                        Log.d(TAG, "onFailure: " + t.getMessage());
//                    }
//                });
//
//
//            }
//
//            @Override
//            public void onError(Exception e) {
//                if(e.getMessage() != null)
//                {
//                    Log.e("Err", e.getMessage());
//                }
//            }
//        });

    }
    private void getLocData(){


        //threshold
        AWSMobileClient.getInstance().getTokens(new com.amazonaws.mobile.client.Callback<Tokens>() {
            @Override
            public void onResult(Tokens result) {
                String AccessToken = result.getAccessToken().getTokenString();
                Log.d(TAG, "Access Token: " + AccessToken);

                Call <List<InfectedHistory>> call = jsonPlaceHolderApi.getLocData(AccessToken,"1");

                call.enqueue(new Callback<List<InfectedHistory>>() {
                    @Override
                    public void onResponse(Call<List<InfectedHistory>> call, Response<List<InfectedHistory>> response) {
                        if (!response.isSuccessful()){
                            Toast.makeText(getActivity(), "Not successful: Code->" + response.code(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //Toast.makeText(getActivity(), "Is Successful: Body->" + response.body(), Toast.LENGTH_SHORT).show();
                        //InfectedHistory infectedHistory = response.body();
                        List<InfectedHistory> infectedHistories = response.body();
//                Log.d(TAG, "Location-> " + response.body().get(1).getLocation());
//                Log.d(TAG, "Duration-> " + response.body().get(1).getDuration());
//                Log.d(TAG, "Time-> " + response.body().get(1).getTime());
                        Log.d(TAG, "onResponse: size of list->" + infectedHistories.size() );
                        
                        Long total_duration = 0L;
                        total_duration = ReportMaker.checkExposure(dbHelper, infectedHistories);
                        if (total_duration > 0)
                        {
                            Log.d(TAG, "onResponse: exposure detected.");
                        }
                        else{
                            Log.d(TAG, "onResponse: no exposure. ur ok");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<InfectedHistory>> call, Throwable t) {
                        Toast.makeText(getActivity(), "OnFailure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                if(e.getMessage() != null)
                {
                    Log.e("Err", e.getMessage());
                }
            }
        });
    }
    /*
    private void getAccessToken(){
        AWSMobileClient.getInstance().getTokens(new com.amazonaws.mobile.client.Callback<Tokens>() {
            @Override
            public void onResult(Tokens result) {
                String AccessToken = result.getAccessToken().getTokenString();
                Log.d(TAG, "Access Token: " + AccessToken);

            }

            @Override
            public void onError(Exception e) {
                if(e.getMessage() != null)
                {
                    Log.e("Err", e.getMessage());
                }
            }
        });
    }*/

    private void reportCovid(){
        //Toggle Shared preference variable
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isCovidPositive" , true);

        Log.d(TAG, "reportCovid: I've saved new value.");
        editor.apply();

        //Do REST command
        AWSMobileClient.getInstance().getTokens(new com.amazonaws.mobile.client.Callback<Tokens>() {
            @Override
            public void onResult(Tokens result) {
                String AccessToken = result.getAccessToken().getTokenString();
                Log.d(TAG, "Access Token: " + AccessToken);


                List<BluetoothLog> patientHistory;
                patientHistory = ReportMaker.collectCovidHistory(dbHelper ,"2020-09-29");


                Call<Void> call = jsonPlaceHolderApi.postHistory(AccessToken,patientHistory);

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

            @Override
            public void onError(Exception e) {
                if(e.getMessage() != null)
                {
                    Log.e("Err", e.getMessage());
                }
            }
        });
    }
}
