package com.rybarczykzsl.spacerowicz;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;


public class WalkMeterFragment extends Fragment implements View.OnClickListener {


    private ChronoodometerService chronoodometer;
    private ChronoodometerService.ChronoodometerBinder chronoodometerBinder;
    private boolean bound;
    private final int PERMISSION_REQUEST_CODE = 698; // VALUE FROM E-BOOK
    private final int NOTIFICATION_ID = 423; // VALUE FROM E-BOOK
    private int seconds = 0;
    private Button toggleButton;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            if(chronoodometerBinder==null){
                chronoodometerBinder = (ChronoodometerService.ChronoodometerBinder) binder;
            }
            chronoodometer = chronoodometerBinder.getChronoodometer();
            bound = true;
            toggleButton.setText(getString(chronoodometer.isRunning() ? R.string.btn_stopwatch_toggle_stop : R.string.btn_stopwatch_toggle_start));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        // CHRONOODOMETER WILL START OR NOT, DEPENDING ON GRANTED PERMISSIONS
        if(ContextCompat.checkSelfPermission(getActivity(),ChronoodometerService.PERMISSION_STRING)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[]{ChronoodometerService.PERMISSION_STRING},PERMISSION_REQUEST_CODE);
        }else{
            Intent intent = new Intent(getActivity(), ChronoodometerService.class);
            getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            seconds = savedInstanceState.getInt("seconds");
            // TRIES TO GET OLD BINDERS IN ORDER TO RESTORE OLD SERVICES IF THEY EXIST
            chronoodometerBinder = (ChronoodometerService.ChronoodometerBinder) savedInstanceState.getBinder("chronoodometerhBinder");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_walk_meter, container, false);
        runTimer(layout);
        displayDistance(layout);
        toggleButton = (Button) layout.findViewById(R.id.btn_stopwatch_toggle);
        toggleButton.setOnClickListener(this);
        Button resetButton = (Button) layout.findViewById(R.id.btn_stopwatch_reset);
        resetButton.setOnClickListener(this);
        return layout;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putInt("seconds", seconds);
        savedInstanceState.putBinder("chronoodometerBinder",chronoodometerBinder);
    }

    // DETECTS CLICKS
    public void onClick(View view){
        if(view.getId() == R.id.btn_stopwatch_toggle){
            onClickToggle();
        } else if (view.getId() == R.id.btn_stopwatch_reset) {
            onClickReset();
        }

    }

    // HANDLES TOGGLING STOPWATCH AND ODOMETER ON/OFF
    private void onClickToggle() {
        if(bound && chronoodometer!=null) {
            Log.i("BUTTON","Toggle");
            chronoodometer.toggle();
            toggleButton.setText(getString(chronoodometer.isRunning() ? R.string.btn_stopwatch_toggle_stop : R.string.btn_stopwatch_toggle_start));
        };
        Log.i("BUTTON","Stopwatch bound: "+bound);
    }

    // HANDLES RESETTING STOPWATCH AND ODOMETER SCORES, MADE PUBLIC TO BE ABLE TO BE CALLED WHEN CHANGING WALK FROM NAVIGATION DRAWER
    public void onClickReset(){
        if(bound && chronoodometer!=null) {
            chronoodometer.reset();
            toggleButton.setText(getString(R.string.btn_stopwatch_toggle_start));
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        if(bound){
            getActivity().unbindService(connection);
            bound=false;
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if(bound && chronoodometer!=null){
            toggleButton.setText(getString(chronoodometer.isRunning() ? R.string.btn_stopwatch_toggle_stop : R.string.btn_stopwatch_toggle_start));
        }
    }

    // UPDATES TIMER
    private void runTimer(View view){
        final TextView timeView = (TextView) view.findViewById(R.id.tv_stopwatch_time);

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(bound && chronoodometer!=null){
                    seconds = chronoodometer.getTime();
                }
                int hours = seconds / 3600;
                int minutes = (seconds%3600)/60;
                int secs = seconds % 60;
                String time = String.format("%02d:%02d:%02d", hours, minutes, secs);
                timeView.setText(time);
                handler.postDelayed(this,1000);
            }
        });
    }

    // UPDATES DISTANCE COUNTER
    private void displayDistance(View view){
        final TextView distanceView = (TextView) view.findViewById(R.id.tv_meter_odometer);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                double distance = 0.0;
                if(bound && chronoodometer!=null){
                    distance = chronoodometer.getDistance();
                }
                String distanceStr = String.format(Locale.getDefault(),"%1$,.2fkm", distance);
                if(distanceView!=null){
                    distanceView.setText(distanceStr);
                }
                handler.postDelayed(this,1000);
            }
        });
    }

    @Override
    // HANDLING NOTIFICATIONS WHEN PERMISSIONS ACCEPTED/REJECTED - CODE FROM E-BOOK
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(getActivity(), ChronoodometerService.class);
                    getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
                } else {
                    NotificationCompat.Builder builder =
                            new NotificationCompat.Builder(getActivity())
                                    .setSmallIcon(android.R.drawable.ic_menu_compass)
                                    .setContentTitle(getResources().getString(R.string.app_name))
                                    .setContentText(getResources().getString(
                                            R.string.permission_denied))
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setVibrate(new long[] { 1000, 1000})
                                    .setAutoCancel(true);
                    Intent actionIntent = new Intent(getActivity(), MainActivity.class);
                    PendingIntent actionPendingIntent = PendingIntent.getActivity(getActivity(), 0,
                            actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(actionPendingIntent);
                    NotificationManager notificationManager =
                            (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(NOTIFICATION_ID, builder.build());
                }
            }
        }
    }
}