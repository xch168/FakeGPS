package com.github.xch168.fakegps;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

/**
 * Created by XuCanHui on 2018/5/9.
 */
public class MockGpsService extends Service {
    private static final String TAG = "MockGpsService";

    private static final int NOTIFICATION_ID = 9999;

    private LocationManager mLocationManager;

    private volatile boolean running = true;

    @Override
    public void onCreate() {
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        startForeground(NOTIFICATION_ID, getNotification("Mock Location"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "onStartCommand() called");

        startMockLocation();
        return START_STICKY;
    }


    private boolean startMockLocation() {
        Log.v(TAG, "startMockLocation");
        newLocation();
        return true;
    }

    private void stopMockLocation() {
        Log.v(TAG, "stopMockLocation");
        running = false;
        try {
            mLocationManager.removeTestProvider(MockGpsProvider.GPS_MOCK_PROVIDER);
        } catch (Exception e){
            Log.w(TAG, e);
        }
    }

    private void newLocation() {
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.addTestProvider(LocationManager.GPS_PROVIDER, false, true, false, false, true, true, true, 0, 5);
        mLocationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);

        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setTime(System.currentTimeMillis());
        location.setLatitude(24.883320);
        location.setLongitude(118.840340);
        location.setSpeed(0);
        location.setAltitude(2.0f);
        location.setAccuracy(3.0f);
        location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    LocationTool.setLocation(mLocationManager, LocationManager.GPS_PROVIDER, 24.883320, 118.840340);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");
        stopForeground(true);
        stopMockLocation();
        super.onDestroy();
    }

    private Notification getNotification(String contentText) {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(contentText)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent)
                .setAutoCancel(false)
                .build();

        return notification;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
