package com.github.xch168.fakegps;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.amap.api.maps2d.model.LatLng;

/**
 * Created by XuCanHui on 2018/5/9.
 */
public class MockGpsService extends Service {
    private static final String TAG = "MockGpsService";

    private static final int NOTIFICATION_ID = 9999;

    public static final String CMD = "cmd";
    public static final String PARM = "parm";
    public static final String LATLNG = "latLng";

    public static final int CMD_START = 1;
    public static final int CMD_UPDATE = 2;


    private LocationManager mLocationManager;

    private volatile boolean running = true;

    private volatile LatLng mLatLng = new LatLng(24.883320, 118.840340);

    private volatile double mLat = 224.883320;
    private volatile double mLng = 118.840340;

    @Override
    public void onCreate() {
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        startForeground(NOTIFICATION_ID, getNotification("Mock Location"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "onStartCommand() called");
        if (intent != null) {
            int cmd = intent.getIntExtra(CMD, CMD_START);
            switch (cmd) {
                case CMD_START:
                    startMockLocation();
                    break;
                case CMD_UPDATE:
                    Bundle parm = intent.getBundleExtra(PARM);
                    mLatLng = (LatLng) parm.get(LATLNG);
                    mLat = mLatLng.latitude;
                    mLng = mLatLng.longitude;
                    Log.i("asdf", "la:" + mLat + " lo:" + mLng);
            }
        } else {
            startMockLocation();
        }

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
                    //LocationTool.setLocation(mLocationManager, LocationManager.GPS_PROVIDER, 24.883320, 118.840340);
                    Log.i("asdf", "xla:" + mLat + " xlo:" + mLng);
                    LocationTool.setLocation(mLocationManager, LocationManager.GPS_PROVIDER, mLat, mLng);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    public static void updateLocation(Context context, LatLng latLng) {
        Intent intent = new Intent(context, MockGpsService.class);
        Bundle parm = new Bundle();
        parm.putParcelable(LATLNG, latLng);
        intent.putExtra(PARM, parm);
        intent.putExtra(CMD, CMD_UPDATE);
        context.startService(intent);
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
