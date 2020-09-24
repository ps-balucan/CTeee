package com.onenineeight.cteee;

import android.app.Notification;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.onenineeight.cteee.BeaconScan.CHANNEL_1_ID;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private NotificationManagerCompat notificationManager;
    public static final String TAG = "FCM";
    private TokenUpdateApi tokenUpdateApi;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = NotificationManagerCompat.from(this);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "onMessageReceived: " + remoteMessage.getData().toString());

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
