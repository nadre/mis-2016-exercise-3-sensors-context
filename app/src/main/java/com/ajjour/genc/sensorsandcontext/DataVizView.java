package com.ajjour.genc.sensorsandcontext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

import java.util.Arrays;
import java.util.List;

/**
 * Created by neffle on 15.05.16.
 *
 * Inspiration:
 * https://examples.javacodegeeks.com/android/core/graphics/canvas-graphics/android-canvas-example/
 */
public class DataVizView extends View {

    Context context;
    AttributeSet attrs;

    Canvas canvas;
    Bitmap bitmap;

    Path[] paths;
    Paint[] paints;

    boolean increasing = true;

    //should be an power of 2
    int windowSize = 1024;

    int index = 0;
    double[] fft_values = new double[windowSize];

    double[] values;
    double T;
    double dx;
    double dy;
    long startTimeMillis;

    public DataVizView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        this.attrs = attrs;

        paths = new Path[4];
        paints = new Paint[4];

        Paint paint = new Paint();

        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(4f);

        for (int i = 0; i < 4; i++){
            paints[i] = new Paint(paint);
            paths[i] = new Path();
        }

        paints[0].setColor(Color.RED);
        paints[1].setColor(Color.GREEN);
        paints[2].setColor(Color.BLUE);
        paints[3].setColor(Color.BLACK);

        values = new double[4];
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        startTimeMillis = System.currentTimeMillis();

        dx = this.getWidth() / 2;
        dy = this.getHeight() / 2;
        T = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < 4; i++){
            canvas.drawPath(paths[i], paints[i]);
        }

    }

    public void clearCanvas() {
        for (int i = 0; i < 4; i++){
            paths[i].reset();
        }
        invalidate();
    }

    public void updateView(float newValues[]) {

        double newVal;
        double oldVal;

        long t = (System.currentTimeMillis() - startTimeMillis);
        double dt = T-t;

        double newMag = 0;
        double oldMag = this.values[3];

        for (int i = 0; i < 3; i++){
            newVal = newValues[i];
            newMag += Math.pow(newVal, 2);
            newVal = newVal * 10 + dy;
            oldVal = this.values[i];
            paths[i].lineTo(t, (float) newVal);
            if (t > 20){
                paths[i].offset( (float) dt, 0);
            }
            this.values[i] = newVal;
        }

        newMag = (float) Math.sqrt(newMag);

        addNewMagValue(newMag);

        newMag = newMag * 10 + dy;
//        paths[3].quadTo(T, oldMag, (t + T) / 2, (newMag + oldMag) / 2);
        paths[3].lineTo(t, (float) newMag);
        if (t > 20){
            paths[3].offset((float) dt ,0);
            startTimeMillis += dt;
        }
        this.values[3] = newMag;
        T = t;
        invalidate();
    }

    public void addNewMagValue(double magVal) {
        if (index == windowSize - 1){
            increasing = false;
        }
        if (increasing) {
            fft_values[index] = magVal;
            index++;
        }
        else {
            for (int i = windowSize - 1; i == 0; i--){
                fft_values[i-1] = fft_values[i];
            }
            fft_values[windowSize - 1] = magVal;
        }

        double[] fft_out_x = Arrays.copyOf(fft_values, fft_values.length);
        double[] fft_out_y = new double[fft_values.length];

        FFT fft = new FFT(windowSize);
        fft.fft(fft_out_x, fft_out_y);

    }
}
