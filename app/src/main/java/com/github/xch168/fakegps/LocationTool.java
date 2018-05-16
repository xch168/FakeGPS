package com.github.xch168.fakegps;

import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;
import android.util.Log;


/**
 * Created by wiw on 17-10-30.
 */

public class LocationTool {


    public static  void setLocation(LocationManager locationManager, String mockProviderName, double latitude, double longitude, float speed) {
        Location location = new Location(mockProviderName);
        location.setTime(System.currentTimeMillis());
        LatLng latLng=delta(latitude,longitude);
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        Log.i("asdf", "la:" + latLng.latitude + " lo:" + latLng.longitude);
        location.setSpeed(speed);
        location.setAltitude(2.0f);
        location.setAccuracy(3.0f);
        location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        locationManager.setTestProviderLocation(mockProviderName, location);
    }
    public static  void setLocation(LocationManager locationManager, String mockProviderName, double latitude, double longitude) {
        setLocation(locationManager,mockProviderName,latitude,longitude,0);
    }
    public static void setLocation(LocationManager locationManager, String mockProviderName, LatLng latLng, float speed){
        setLocation(locationManager,mockProviderName,latLng.latitude,latLng.longitude,speed);
    }
    public static void setLocation(LocationManager locationManager, String mockProviderName, LatLng latLng){
        setLocation(locationManager,mockProviderName,latLng.latitude,latLng.longitude);
    }
    public static LatLng transToLatlng(Location location){
        return new LatLng(location.getLatitude(),location.getLongitude());
    }
    static double  PI = 3.14159265358979324;
    /**
     * @author 作者:
     * 方法描述:方法可以将高德地图SDK获取到的GPS经纬度转换为真实的经纬度，可以用于解决安卓系统使用高德SDK获取经纬度的转换问题。
     * @param 需要转换的经纬度
     * @return 转换为真实GPS坐标后的经纬度
     * @throws <异常类型> {@inheritDoc} 异常描述
     */
    public static LatLng delta(double lat, double lon) {
        double a = 6378245.0;//克拉索夫斯基椭球参数长半轴a
        double ee = 0.00669342162296594323;//克拉索夫斯基椭球参数第一偏心率平方
        double dLat = transformLat(lon - 105.0, lat - 35.0);
        double dLon = transformLon(lon - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * PI;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * PI);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * PI);

//        HashMap<String, Double> hm = new HashMap<String, Double>();
//        hm.put("lat",lat - dLat);
//        hm.put("lon",lon - dLon);
        return new LatLng(lat-dLat,lon-dLon);
    }
    //转换经度
    public static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * PI) + 40.0 * Math.sin(x / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * PI) + 300.0 * Math.sin(x / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }
    //转换纬度
    public static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * PI) + 40.0 * Math.sin(y / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * PI) + 320 * Math.sin(y * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }
}
