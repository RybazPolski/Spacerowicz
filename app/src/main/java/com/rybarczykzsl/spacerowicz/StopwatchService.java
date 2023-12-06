package com.rybarczykzsl.spacerowicz;

import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import android.app.Service;

import java.util.Timer;

public class StopwatchService extends Service {

    public StopwatchService(){
        super();
    }
    private boolean running = false;
    private static int seconds = 0;
    private Handler handler;
    private final IBinder binder = new StopwatchBinder();

    public class StopwatchBinder extends Binder {
        StopwatchService getStopwatch() {return StopwatchService.this;}
    }

    public int getTime() { return seconds;}
    public IBinder onBind(Intent intent) { return binder; }


    @Override
    public void onCreate() {
        super.onCreate();
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
    }
    public void reset(){
        running=false;
        seconds=0;
    }

    public boolean isRunning(){
        return running;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
