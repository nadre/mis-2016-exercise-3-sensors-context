package com.ajjour.genc.sensorsandcontext;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;
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
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private double samplingRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_viz);

        dataFFTView = (DataFFTView) findViewById(R.id.data_fft_canvas);
        dataVizView = (DataVizView) findViewById(R.id.data_viz_canvas);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (accelerometer == null){
            Toast.makeText(this, "Whoopsie, couldn't find Accelerometer.", Toast.LENGTH_SHORT).show();
        }

        SeekBar samplingRateBar = (SeekBar)findViewById(R.id.sampling_rate_seekbar);
        samplingRateBar.setMax(100);
        samplingRateBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                samplingRate = ((double)progress)/100;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                sensorManager.unregisterListener(DataVizActivity.this);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sensorManager.registerListener(DataVizActivity.this, accelerometer, (int)(SensorManager.SENSOR_DELAY_FASTEST*samplingRate));
            }
        });

        SeekBar windowSizeBar = (SeekBar)findViewById(R.id.window_size_seekbar);
        windowSizeBar.setMax(11);
        windowSizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                dataFFTView.changeWindowSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        });
    }

    public void clearCanvas(){
        dataVizView.clearCanvas();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] values = sensorEvent.values;
        double magnitude = dataVizView.updateViewAndReturnMagnitude(values);
        dataFFTView.addNewMagValue(magnitude);
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
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
