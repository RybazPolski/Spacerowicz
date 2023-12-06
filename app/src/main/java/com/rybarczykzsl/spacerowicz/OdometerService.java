package com.rybarczykzsl.spacerowicz;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.Random;

public class OdometerService extends Service {
    public OdometerService() {
    }

    private final IBinder binder = new OdometerBinder();
    private final Random random = new Random();

    private LocationManager locationManager;
    public static final String PERMISSION_STRING = Manifest.permission.ACCESS_FINE_LOCATION;
    private static double distanceInMeters;
    private static Location lastLocation = null;
    private boolean running;

    public class OdometerBinder extends Binder {
        OdometerService getOdometer() {
            return OdometerService.this;
        }
    }

    public double getDistance() {
        return distanceInMeters / 1000.0;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private LocationListener listener;

    @Override
    public void onCreate() {
        super.onCreate();
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if(lastLocation==null){
                    lastLocation=location;
                }
                distanceInMeters += location.distanceTo(lastLocation);
                lastLocation = location;
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
//                LocationListener.super.onProviderDisabled(provider);
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
//                LocationListener.super.onProviderEnabled(provider);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
//                LocationListener.super.onStatusChanged(provider, status, extras);
            }
        };

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, PERMISSION_STRING) == PackageManager.PERMISSION_GRANTED) {
            String provider = locationManager.getBestProvider(new Criteria(), true);
            if(provider!=null){
                locationManager.requestLocationUpdates(provider, 1000, 1, listener);
            }
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationManager!=null && listener!=null){
            if(ContextCompat.checkSelfPermission(this, PERMISSION_STRING)==PackageManager.PERMISSION_GRANTED){
                locationManager.removeUpdates(listener);
            }
            locationManager = null;
            listener = null;
        }
    }
}