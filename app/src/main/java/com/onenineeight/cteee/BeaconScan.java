package com.onenineeight.cteee;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.RemoteException;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

public class BeaconScan extends Application implements BootstrapNotifier {
    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";
    private NotificationManagerCompat notificationManager;

    private static final String TAG = "BeaconReferenceApp";
    private RegionBootstrap regionBootstrap;
    private BackgroundPowerSaver backgroundPowerSaver;
    private boolean haveDetectedBeaconsSinceBoot = true;
    private boolean safeMark = false;
    private HomeActivity monitoringActivity = null;
    private String cumulativeLog = "";
    //private JsonPlaceHolderApi jsonPlaceHolderApi;
    private BeaconManager beaconManager;

    private int primeBeaconUuid;
    private String startTime = "";
    private String endTime = "";

    //private HashSet<Beacon> primeBeacon = new HashSet<Beacon>();





    public void onCreate() {
        super.onCreate();
        beaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().clear();
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));




        //PART 4. Background Scanning
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        builder.setContentTitle("Scanning for Beacons");
        Intent intent = new Intent(this, AuthenticationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );
        builder.setContentIntent(pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("My Notification Channel ID",
                    "My Notification Name", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("My Notification Channel Description");
            NotificationManager notificationManager = (NotificationManager) getSystemService(
                    Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channel.getId());
        }
        beaconManager.enableForegroundServiceScanning(builder.build(), 456);
        // For the above foreground scanning service to be useful, you need to disable
        // JobScheduler-based scans (used on Android 8+) and set a fast background scan
        // cycle that would otherwise be disallowed by the operating system.
        //
        beaconManager.setEnableScheduledScanJobs(false);
        beaconManager.setBackgroundBetweenScanPeriod(0);
        beaconManager.setBackgroundScanPeriod(1100);

        { //Retrofit stuff
//        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
//        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(loggingInterceptor)
//                .build();
//
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://192.168.1.2:5000")
//                .addConverterFactory(GsonConverterFactory.create())
//                .client(okHttpClient)
//                .build();
//
//        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        }

        Log.d(TAG, "setting up background monitoring for beacons and power saving");
        // wake up the app when a beacon is seen
        Region region = new Region("backgroundRegion",
                null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);

        backgroundPowerSaver = new BackgroundPowerSaver(this);

        createNotificationChannel();
        notificationManager = NotificationManagerCompat.from(this);
    }

    public void disableMonitoring() {
        Log.d(TAG, "disabling monitoring..");
        if (regionBootstrap != null) {
            regionBootstrap.disable();
            regionBootstrap = null;
        }
    }
    public void enableMonitoring() {
        Region region = new Region("backgroundRegion",
                null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);
    }

    @Override
    public void didEnterRegion(Region arg0) {
        // In this example, this class sends a notification to the user whenever a Beacon
        // matching a Region (defined above) are first seen.
        Log.i(TAG, "I entered the region");
        customNotification("Entered Beacon Range.");

        RangeNotifier rangeNotifier = new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    Log.i(TAG, "Ranging through beacons in the region");
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                    java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

                    //Date currentTime = Calendar.getInstance().getTime();
                    //new SimpleDateFormat("yyyy-MM-dd HH:mm")
                    // Log.i(TAG, "Time is: " + startTime);

                    for (Beacon beacon : beacons) {
                        Log.i(TAG, "The beacon " + beacon.getServiceUuid() + " is about " + beacon.getDistance() + " meters away.");
                        //Log.i(TAG, "Comparing " + beacon.getServiceUuid() + " vs " + primeBeaconUuid);
                        if ((beacon.getDistance() < 3) && (beacon.getServiceUuid() != primeBeaconUuid))
                        {
                            //this assumes only one beacon will ever be <5 meters at all times
                            startTime = format.format(new Date());
                            primeBeaconUuid = beacon.getServiceUuid();

                            Log.i(TAG, "Timing new thing");
                        }
                        else if ((beacon.getDistance() > 3 ) && (beacon.getServiceUuid() == primeBeaconUuid))
                        {
                            Date d1 = new Date();
                            Date d2 = new Date();
                            Long duration = 0L;
                            Log.i(TAG, "Stopping timing of new thing");
                            //checks if you are the prime beacon and u are greater than 5 meters
                            try{
                                endTime = format.format(new Date());
                                d1 = df.parse(startTime);
                                d2 = df.parse(endTime);
                                duration = d2.getTime() - d1.getTime();
                                duration = duration/1000;

                                Log.i(TAG, "Duration of beacon: " + primeBeaconUuid + " is " + duration.toString());
                            } catch (java.text.ParseException e){
                                e.printStackTrace();
                            }


                            //Save to log here
                            //

                            insertEntry(String.valueOf(primeBeaconUuid), persistDate(d1), duration);
                            customNotification("Notification Logged: " + primeBeaconUuid + " " + persistDate(d1) + " " + duration);
                            primeBeaconUuid = 0;


                        }


                    }



                }

            }

        };


        try {
            // start ranging for beacons.  This will provide an update once per second with the estimated
            // distance to the beacon in the didRAngeBeaconsInRegion method.
            beaconManager.removeAllRangeNotifiers();
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            beaconManager.addRangeNotifier(rangeNotifier);
            Log.d(TAG, "trying something xd");
        } catch (RemoteException e) {   }


    }
    private void insertEntry(String name, Long time, Long duration){
        LogDbHelper dbHelper = new LogDbHelper(this);
        BluetoothLog bluetoothLog = new BluetoothLog(name, time, duration);
        Log.i(TAG, "ADDING ENTRY: Name-> " + bluetoothLog.getBeacon() + " time-> " + time + " duration->" + duration);
        dbHelper.insertLog(bluetoothLog);
    }

    public static Long persistDate(Date date) {
        if (date != null) {
            return date.getTime()/1000;
        }
        return null;
    }
    @Override
    public void didExitRegion(Region region) {
        //logToDisplay("I no longer see a beacon.");
        Log.i(TAG, "I no longer see a beacon." );
        customNotification("I no longer see a beacon");

    }

    @Override
    public void didDetermineStateForRegion(int state, Region region) {
        Log.i(TAG, "Current region state is: " + (state == 1 ? "INSIDE" : "OUTSIDE ("+state+")"));
        //logToDisplay("Current region state is: " + (state == 1 ? "INSIDE" : "OUTSIDE ("+state+")"));
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("Beacon detected nearby.2");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel1);
        }
    }

    private void sendNotification(String ActualLocation) {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("SATs tracker")
                .setContentText(ActualLocation + " Detected Nearby.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager.notify(1, notification);
    }

    private void customNotification(String message) {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("CTeee")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager.notify(1, notification);
    }

    public void setMonitoringActivity(HomeActivity activity) {
        this.monitoringActivity = activity;
    }



    //create Post function
//    private void createPost(String ActualLocation){
//        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.US);
//        String hour = format.format(new Date());
//
//        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
//        String date = format2.format(new Date());
//
//        Post post = new Post(1,hour, date,ActualLocation);
//
//        Call<Post> call = jsonPlaceHolderApi.createPost(post);
//        call.enqueue(new Callback<Post>() {
//            @Override
//            public void onResponse(Call<Post> call, Response<Post> response) {
//                if (!response.isSuccessful()){
//                    Log.d(TAG , "Post failed");
//                    return;
//                }
//                Post postResponse = response.body();
//                //Log.d(TAG, postResponse.getTitle());
//                Log.d(TAG, Integer.toString(response.code()));
//            }
//
//            @Override
//            public void onFailure(Call<Post> call, Throwable t) {
//
//            }
//        });
//    }


}
