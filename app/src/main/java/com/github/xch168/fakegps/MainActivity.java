package com.github.xch168.fakegps;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, AMap.OnMyLocationChangeListener,AMap.OnMarkerDragListener,AMap.OnMarkerClickListener,AMap.InfoWindowAdapter,AMap.OnMapClickListener {

    private MapView mMapView;
    private Marker mMarker;
    private FloatingActionButton mFab;

    private AMap mAMap;

    private Intent mServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMapView = findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        initMap();

        checkPermissionsGranted();

        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mServiceIntent == null) {
                    start();
                    mFab.setImageResource(R.drawable.ic_action_stop);
                } else {
                    stop();
                    mFab.setImageResource(R.drawable.ic_action_stop);
                    mServiceIntent = null;
                }
            }
        });
    }

    private void initMap() {
        mAMap = mMapView.getMap();
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker));
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW);
        //myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        mAMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        mAMap.setOnMapClickListener(this);
        mAMap.setOnMarkerClickListener(this);
        mAMap.setOnMarkerDragListener(this);
        mAMap.setOnMyLocationChangeListener(this);

        UiSettings uiSettings= mAMap.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        mAMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点
        mAMap.setInfoWindowAdapter(this);
        mAMap.moveCamera(CameraUpdateFactory.zoomTo((float) 17.6));
    }

    public void start() {
        if (checkPermissionsGranted()) {
            mServiceIntent = new Intent(this, MockGpsService.class);
            startService(mServiceIntent);
        }
    }

    public void stop() {
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


    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (mMarker == null) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title(latLng.toString())
                    .position(latLng)
                    .setGps(false)
                    .draggable(true);
            mMarker = mAMap.addMarker(markerOptions);
        } else {
            mMarker.setPosition(latLng);
            mMarker.setTitle(latLng.toString());
            mMarker.showInfoWindow();
        }
        MockGpsService.updateLocation(this, latLng);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public void onMyLocationChange(Location location) {

    }
}
