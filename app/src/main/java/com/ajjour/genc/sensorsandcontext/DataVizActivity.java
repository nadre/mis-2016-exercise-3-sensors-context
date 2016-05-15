package com.ajjour.genc.sensorsandcontext;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by neffle on 15.05.16.
 *
 * Inspiration:
 * https://developer.android.com/guide/topics/sensors/sensors_overview.html
 */
public class DataVizActivity extends AppCompatActivity implements SensorEventListener {

    private DataVizView dataVizView;
    private SensorManager sensorManager;
    private Sensor accelerometer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_viz);

        dataVizView = (DataVizView) findViewById(R.id.data_viz_canvas);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (accelerometer == null){
            Toast.makeText(this, "Whoopsie, couldn't find Accelerometer.", Toast.LENGTH_SHORT).show();
        }
    }

    public void clearCanvas(){
        dataVizView.clearCanvas();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] values = sensorEvent.values;
        dataVizView.updateView(values);
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
