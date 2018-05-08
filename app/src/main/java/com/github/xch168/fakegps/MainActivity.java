package com.github.xch168.fakegps;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private EditText mLongitudeText;
    private EditText mLatitudeText;

    private LocationManager mLocationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLongitudeText = findViewById(R.id.longitude);
        mLatitudeText = findViewById(R.id.latitude);
    }

    public void start(View view) {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            String providerStr = LocationManager.GPS_PROVIDER;
            LocationProvider provider = mLocationManager.getProvider(providerStr);
            if (provider != null) {
                mLocationManager.addTestProvider(provider.getName(),
                        provider.requiresNetwork(),
                        provider.requiresSatellite(), provider.requiresCell(),
                        provider.hasMonetaryCost(),
                        provider.supportsAltitude(), provider.supportsSpeed(),
                        provider.supportsBearing(),
                        provider.getPowerRequirement(), provider.getAccuracy());
            } else {
                mLocationManager.addTestProvider(providerStr, true, true,
                        false, false, true, true, true, Criteria.POWER_HIGH,
                        Criteria.ACCURACY_FINE);
            }
            mLocationManager.setTestProviderEnabled(providerStr, true);
            mLocationManager.setTestProviderStatus(providerStr,
                    LocationProvider.AVAILABLE, null, System.currentTimeMillis());

            Location mockLocation = new Location(providerStr);
            mockLocation.setLatitude(Double.parseDouble(mLatitudeText.getText().toString())); // 维度（度）
            mockLocation.setLongitude(Double.parseDouble(mLongitudeText.getText().toString())); // 经度（度）
            mockLocation.setAltitude(30); // 高程（米）
            mockLocation.setBearing(180f); // 方向（度）
            mockLocation.setSpeed(5); // 速度（米/秒）
            mockLocation.setAccuracy(0.1f); // 精度（米）
            mockLocation.setTime(new Date().getTime()); // 本地时间
            mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
            mLocationManager.setTestProviderLocation(providerStr, mockLocation);
            Toast.makeText(this, "设置完成", Toast.LENGTH_LONG).show();

        } catch (SecurityException e) {
            Log.e("asdf", "error:" + e.getMessage());
        }
    }

    public void stop(View view) {
        try {
            mLocationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
            Toast.makeText(this, "停止成功", Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Log.e("asdf", "stop:" + ex.getMessage());
        }
    }
}
