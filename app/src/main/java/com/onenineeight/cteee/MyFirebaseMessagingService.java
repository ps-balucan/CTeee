package com.onenineeight.cteee;

import android.app.Notification;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.onenineeight.cteee.BeaconScan.CHANNEL_1_ID;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private NotificationManagerCompat notificationManager;
    public static final String TAG = "FCM";
    public static final int EXPOSURE_JOB_ID = 123;
    private TokenUpdateApi tokenUpdateApi;

    public static final String SHARED_PREFS = "sharedPrefs";
    private boolean isCovidPositve = false;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = NotificationManagerCompat.from(this);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "onMessageReceived: " + remoteMessage.getData().toString());

        //Converting the String JSON type to JSON object
        NotificationConverterClass obj = new NotificationConverterClass();
        Gson gson = new Gson();
        obj = gson.fromJson(remoteMessage.getData().get("default"), NotificationConverterClass.class);
        Log.d(TAG, "onMessageReceived: " + obj.getNotificationType());

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        isCovidPositve = sharedPreferences.getBoolean("isCovidPositive", false);

        isCovidPositve = false;
        if (obj.getNotificationType().equals("InfectionReport"))
        {
            if (isCovidPositve)
            {
                Log.d(TAG, "onMessageReceived: Received exposure notif but rejected since user is already positive");
            }
            else{
                scheduleJob();
            }
        }
//        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID )
//                .setSmallIcon(R.drawable.ic_notif)
//                .setContentTitle(remoteMessage.getNotification().getTitle())
//                .setContentText(remoteMessage.getNotification().getBody())
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setCategory(NotificationCompat.CATEGORY_SERVICE)
//                .build();
//
//        notificationManager.notify(1, notification);

    }
    private void scheduleJob(){
        ComponentName componentName = new ComponentName(this, ExposureNotificationJobService.class);
        JobInfo info = new JobInfo.Builder(EXPOSURE_JOB_ID, componentName)
                .setRequiresCharging(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                //.setPeriodic(15 * 60 * 1000)
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

        int resultCode = scheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS){
            Log.d(TAG, "scheduleJob: Job scheduled");
        }
        else
        {
            Log.d(TAG, "scheduleJob: Job scheduling failed");
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "onNewToken: New token is " +  token);
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        tokenUpdateApi = ApiClient.getInstance().create(TokenUpdateApi.class);

        SubscribeToken subscribeToken = new SubscribeToken();
        subscribeToken.setToken(token);

        Call<Void> call = tokenUpdateApi.sendToken(subscribeToken);

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
