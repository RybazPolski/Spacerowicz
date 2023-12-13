package com.rybarczykzsl.spacerowicz;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import java.util.Objects;


public class WalkMeterFragment extends Fragment implements View.OnClickListener {


    private ChronoodometerService chronoodometer;
    private ChronoodometerService.ChronoodometerBinder chronoodometerBinder;
    private boolean bound;
    public final int PERMISSION_REQUEST_CODE = 698; // VALUE FROM E-BOOK
    private final int NOTIFICATION_ID = 423; // VALUE FROM E-BOOK
    public final static String NOTIFICATION_CHANNEL_PERMISSIONS = "Permission notifications"; // VALUE FROM WHO KNOWS WHERE
    private int seconds = 0;
    private double distance = 0.0;
    private Button toggleButton;

    public final ServiceConnection connection = new ServiceConnection() {
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
        if(ContextCompat.checkSelfPermission(requireActivity(),ChronoodometerService.PERMISSION_STRING)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(), new String[]{ChronoodometerService.PERMISSION_STRING},PERMISSION_REQUEST_CODE);
        }else{
            Intent intent = new Intent(requireActivity(), ChronoodometerService.class);
            requireActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            seconds = savedInstanceState.getInt("seconds");
            // TRIES TO GET OLD BINDERS IN ORDER TO RESTORE OLD SERVICES IF THEY EXIST
            chronoodometerBinder = (ChronoodometerService.ChronoodometerBinder) savedInstanceState.getBinder("chronoodometerBinder");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_walk_meter, container, false);
        startMeasuring(layout);
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
            chronoodometer.toggle();
            toggleButton.setText(getString(chronoodometer.isRunning() ? R.string.btn_stopwatch_toggle_stop : R.string.btn_stopwatch_toggle_start));
        }else{
            NotificationCompat.Builder builder = new NotificationCompat.Builder(requireActivity(), NOTIFICATION_CHANNEL_PERMISSIONS)
                            .setSmallIcon(android.R.drawable.ic_menu_compass)
                            .setContentTitle(getResources().getString(R.string.app_name))
                            .setContentText(getResources().getString(R.string.permission_denied))
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setVibrate(new long[]{1000, 1000})
                            .setAutoCancel(true)
                            .setChannelId(NOTIFICATION_CHANNEL_PERMISSIONS);
            Intent actionIntent = new Intent(requireActivity(), MainActivity.class);
            PendingIntent actionPendingIntent = PendingIntent.getActivity(requireActivity(), 0,
                    actionIntent, Build.VERSION.SDK_INT>31?PendingIntent.FLAG_IMMUTABLE:PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(actionPendingIntent);
            NotificationManager notificationManager =
                    (NotificationManager) requireActivity().getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
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
            requireActivity().unbindService(connection);
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
    private void startMeasuring(View view){
        final TextView timeView = (TextView) view.findViewById(R.id.tv_stopwatch_time);
        final TextView distanceView = (TextView) view.findViewById(R.id.tv_meter_odometer);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(bound && chronoodometer!=null){
                    seconds = chronoodometer.getTime();
                    distance = chronoodometer.getDistance();
                }
                int hours = seconds / 3600;
                int minutes = (seconds%3600)/60;
                int secs = seconds % 60;
                String timeStr = String.format("%02d:%02d:%02d", hours, minutes, secs);
                timeView.setText(timeStr);

                String distanceStr = String.format(Locale.getDefault(),"%1$,.2fkm", distance);
                distanceView.setText(distanceStr);

                handler.postDelayed(this,1000);
            }
        });
    }
}