package com.onenineeight.cteee;

import android.app.Notification;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.results.Tokens;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.onenineeight.cteee.BeaconScan.CHANNEL_1_ID;

public class ExposureNotificationJobService extends JobService {
    public static final String TAG = "ExposureNotifJobService";
    private boolean jobCancelled = false;
    private LogDbHelper dbHelper;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private Long exposureResult = 0L;
    private NotificationManagerCompat notificationManager;
    public static final String SHARED_PREFS = "sharedPrefs";


    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "onStartJob: Job Started");
        GetExposureHistory(jobParameters);
        return true;
    }

    private void GetExposureHistory(final JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                for (int i = 0; i < 10 ; i ++)
//                {
//                    if (jobCancelled){
//                        return;
//                    }
//                    Log.d(TAG, "run: " + i);
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
                dbHelper = new LogDbHelper(ExposureNotificationJobService.this);
                getLocData();
                dbHelper.close();
                Log.d(TAG, "run: Job finished.");
                jobFinished(params, false);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "onStopJob: Job cancelled before completion");
        jobCancelled = true;
        return false;
    }

    private void getLocData(){
        //threshold
        jsonPlaceHolderApi = ApiClient.getInstance().create(JsonPlaceHolderApi.class);

        AWSMobileClient.getInstance().getTokens(new com.amazonaws.mobile.client.Callback<Tokens>() {
            @Override
            public void onResult(Tokens result) {
                String AccessToken = result.getAccessToken().getTokenString();
                Log.d(TAG, "Access Token: " + AccessToken);

                Call<List<InfectedHistory>> call = jsonPlaceHolderApi.getLocData(AccessToken,"106");

                call.enqueue(new Callback<List<InfectedHistory>>() {
                    @Override
                    public void onResponse(Call<List<InfectedHistory>> call, Response<List<InfectedHistory>> response) {
                        if (!response.isSuccessful()){
                            //Toast.makeText(getActivity(), "Not successful: Code->" + response.code(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //Toast.makeText(getActivity(), "Is Successful: Body->" + response.body(), Toast.LENGTH_SHORT).show();
                        //InfectedHistory infectedHistory = response.body();
                        List<InfectedHistory> infectedHistories = response.body();
//                Log.d(TAG, "Location-> " + response.body().get(1).getLocation());
//                Log.d(TAG, "Duration-> " + response.body().get(1).getDuration());
//                Log.d(TAG, "Time-> " + response.body().get(1).getTime());
                        Log.d(TAG, "onResponse: size of list->" + infectedHistories.size() );
                        exposureResult =ReportMaker.checkExposure(dbHelper, infectedHistories);
                        if (exposureResult > 0)
                        {
                            Log.d(TAG, "Exposure Detected! carepul");
                            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putLong("durationExposed" , exposureResult);

                            /*
                            CardView card = view.findViewById(R.id.safeCard); //find safeCard properly
                            card.setCardBackgroundColor(Color.RED);
                            TextView safe = view.findViewById(R.id.safeOrAtRisk);
                            safe.setText("At Risk"); //add code to revert back to safe
                               */
                            notificationManager = NotificationManagerCompat.from(ExposureNotificationJobService.this);
                            Notification notification = new NotificationCompat.Builder(ExposureNotificationJobService.this, CHANNEL_1_ID )
                                    .setSmallIcon(R.drawable.ic_notif)
                                    .setContentTitle("PrivaTrace")
                                    .setContentText("New exposure report checked. You're at risk!")
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                                    .build();
                            notificationManager.notify(1, notification);

                            Log.d(TAG, "reportCovid: I've saved new value. for duration it's " + exposureResult );
                            editor.apply();
                        }
                        else
                        {
                            notificationManager = NotificationManagerCompat.from(ExposureNotificationJobService.this);

                            Log.d(TAG, "No exposure. u safe");
                            Notification notification = new NotificationCompat.Builder(ExposureNotificationJobService.this, CHANNEL_1_ID )
                                .setSmallIcon(R.drawable.ic_notif)
                                .setContentTitle("PrivaTrace")
                                .setContentText("New exposure report checked. You're safe!")
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                                .build();
                            notificationManager.notify(1, notification);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<InfectedHistory>> call, Throwable t) {
                        //Toast.makeText(getActivity(), "OnFailure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
