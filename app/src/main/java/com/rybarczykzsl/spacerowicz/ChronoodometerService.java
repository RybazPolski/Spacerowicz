package com.rybarczykzsl.spacerowicz;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import android.app.Service;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.Timer;

public class ChronoodometerService extends Service {

    public ChronoodometerService(){
    }
    private LocationManager locationManager;
    public static final String PERMISSION_STRING = Manifest.permission.ACCESS_FINE_LOCATION;
    private static double distanceInMeters;
    private static Location lastLocation = null;
    private LocationListener listener;
    private static int seconds = 0;
    private boolean running = false;
    private Handler handler;
    private final IBinder binder = new ChronoodometerBinder();

    public class ChronoodometerBinder extends Binder {
        ChronoodometerService getChronoodometer() {return ChronoodometerService.this;}
    }

    public double getDistance() {
        return distanceInMeters / 1000.0;
    }
    public int getTime() { return seconds;}
    public IBinder onBind(Intent intent) { return binder; }


    @Override
    public void onCreate() {
        super.onCreate();

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if(running){
                    if(lastLocation==null){
                        lastLocation=location;
                    }
                    distanceInMeters += location.distanceTo(lastLocation);
                    lastLocation = location;
                }
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
        };

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, PERMISSION_STRING) == PackageManager.PERMISSION_GRANTED) {
            String provider = locationManager.getBestProvider(new Criteria(), true);
            if(provider!=null){
                locationManager.requestLocationUpdates(provider, 1000, 1, listener);
            }
        }

        handler = new Handler();
        startUpdatingTime();
    }

    private void startUpdatingTime(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(running){
                    seconds++;
                }
                handler.postDelayed(this, 1000);
            }
        },1000);
    }

    public void toggle(){
        running=!running;
        if(!running){
            lastLocation = null;
        }
    }
    public void reset(){
        running=false;
        seconds=0;
        lastLocation = null;
    }

    public boolean isRunning(){
        return running;
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
