package com.github.xch168.fakegps;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.EditText;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private EditText mLongitudeText;
    private EditText mLatitudeText;

    private Intent mServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLongitudeText = findViewById(R.id.longitude);
        mLatitudeText = findViewById(R.id.latitude);

        checkPermissionsGranted();
    }

    public void start(View view) {
        if (checkPermissionsGranted()) {
            mServiceIntent = new Intent(this, MockGpsService.class);
            startService(mServiceIntent);
        }
    }

    public void stop(View view) {
        if (mServiceIntent != null) {
            stopService(mServiceIntent);
        }
    }

    private boolean checkPermissionsGranted() {
        if (!hasMockLocationPermission()) {
            String info="请在<font color='#00008B'>设置->开发人员选项->选择模拟位置信息</font>中选择<font color='#0000cd'>FakeGPS</font>";
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("设置模拟位置权限");
            builder.setMessage(Html.fromHtml(info));
            builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
                }
            });
            builder.show();

            return false;
        }
        if (!hasLocationPermission()) {
            EasyPermissions.requestPermissions(this, "hhhhh", 1, Manifest.permission.ACCESS_FINE_LOCATION);
            return false;
        }
        return true;
    }

    private boolean hasLocationPermission() {
        return EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private boolean hasMockLocationPermission() {
        if (Build.VERSION.SDK_INT > 22) {
            try {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationManager.addTestProvider(LocationManager.GPS_PROVIDER,
                        true, true, false, false,
                        true, true, true, Criteria.POWER_HIGH, Criteria.ACCURACY_FINE);
                return true;
            } catch (Exception e) {
                return false;
            }
        } else {
            return (Settings.Secure.getInt(getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION, 0) != 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }


}
