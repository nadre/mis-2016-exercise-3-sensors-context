package com.ajjour.genc.sensorsandcontext;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by neffle on 15.05.16.
 *
 * Inspiration:
 * https://developer.android.com/guide/topics/sensors/sensors_overview.html
 */
public class DataVizActivity extends AppCompatActivity implements SensorEventListener {

    private DataVizView dataVizView;
    private DataFFTView dataFFTView;

    private TextView samplingRateText;
    private TextView fftWindowText;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private double samplingRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_viz);

        dataFFTView = (DataFFTView) findViewById(R.id.data_fft_canvas);
        dataVizView = (DataVizView) findViewById(R.id.data_viz_canvas);

        samplingRateText = (TextView) findViewById(R.id.sampling_rate);
        fftWindowText = (TextView) findViewById(R.id.fft_window_size);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (accelerometer == null){
            Toast.makeText(this, "Whoopsie, couldn't find Accelerometer.", Toast.LENGTH_SHORT).show();
        }

        SeekBar samplingRateBar = (SeekBar)findViewById(R.id.sampling_rate_seekbar);
        samplingRateBar.setMax(10);
        samplingRateBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                samplingRate = ((double)progress)/10;
                samplingRateText.setText(getString(R.string.sampling_rate)+": "+samplingRate);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                sensorManager.unregisterListener(DataVizActivity.this);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                double currentDelay = SensorManager.SENSOR_DELAY_NORMAL- SensorManager.SENSOR_DELAY_NORMAL*samplingRate;
                sensorManager.registerListener(DataVizActivity.this, accelerometer, (int)currentDelay);
            }
        });

        SeekBar windowSizeBar = (SeekBar)findViewById(R.id.window_size_seekbar);
        windowSizeBar.setMax(10);
        windowSizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                dataFFTView.changeWindowSize(progress);
                fftWindowText.setText(getString(R.string.fft_window_size)+": "+(int)Math.pow(2,progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {         }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {         }
        });
    }

    public void clearCanvas(){
        dataVizView.clearCanvas();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] values = sensorEvent.values;
        double magnitude = dataVizView.updateViewAndReturnMagnitude(values);
        dataFFTView.handleNewMagnitudeValue(magnitude);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    public void sendNotification(String name) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("New Activity!")
                        .setContentText(name);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, DataVizActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(DataVizActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        mNotificationManager.notify(123, mBuilder.build());
    }

}
