package com.github.xch168.fakegps;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Created by XuCanHui on 2018/5/10.
 */
public final class LatLng implements Parcelable, Cloneable {
    public final double latitude;
    public final double longitude;
    private static DecimalFormat a;

    public LatLng(double var1, double var3) {
        if (-180.0D <= var3 && var3 < 180.0D) {
            this.longitude = a(var3);
        } else {
            this.longitude = a(((var3 - 180.0D) % 360.0D + 360.0D) % 360.0D - 180.0D);
        }

        this.latitude = a(Math.max(-90.0D, Math.min(90.0D, var1)));
    }

    protected LatLng(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<LatLng> CREATOR = new Creator<LatLng>() {
        @Override
        public LatLng createFromParcel(Parcel in) {
            return new LatLng(in);
        }

        @Override
        public LatLng[] newArray(int size) {
            return new LatLng[size];
        }
    };

    private static double a(double var0) {
        double var2 = var0;

        try {
            var2 = Double.parseDouble(a.format(var0));
        } catch (Throwable var5) {
            ;
        }

        return var2;
    }

    public LatLng clone() {
        return new LatLng(this.latitude, this.longitude);
    }

    public int hashCode() {
        byte var1 = 31;
        byte var2 = 1;
        long var3 = Double.doubleToLongBits(this.latitude);
        int var5 = var1 * var2 + (int)(var3 ^ var3 >>> 32);
        var3 = Double.doubleToLongBits(this.longitude);
        var5 = var1 * var5 + (int)(var3 ^ var3 >>> 32);
        return var5;
    }

    public boolean equals(Object var1) {
        if (this == var1) {
            return true;
        } else if (!(var1 instanceof LatLng)) {
            return false;
        } else {
            LatLng var2 = (LatLng)var1;
            return Double.doubleToLongBits(this.latitude) == Double.doubleToLongBits(var2.latitude) && Double.doubleToLongBits(this.longitude) == Double.doubleToLongBits(var2.longitude);
        }
    }

    public String toString() {
        return "lat/lng: (" + this.latitude + "," + this.longitude + ")";
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel var1, int var2) {
        var1.writeDouble(this.longitude);
        var1.writeDouble(this.latitude);
    }

    static {
        a = new DecimalFormat("0.000000", new DecimalFormatSymbols(Locale.US));
    }
}
