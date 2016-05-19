package com.ajjour.genc.sensorsandcontext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.Arrays;

/**
 * Created by neffle on 15.05.16.
 *
 * Inspiration:
 * https://examples.javacodegeeks.com/android/core/graphics/canvas-graphics/android-canvas-example/
 */
public class DataFFTView extends View {

    Context context;
    AttributeSet attrs;

    Canvas mCanvas;
    Bitmap mBitmap;

    Paint paint;
    Path path;

    boolean increasing = true;

    //should be an power of 2
    int initWindowSize = 1024;
    int maxWindowSize = 2048;

    double[] fft_values = new double[maxWindowSize];

    int index = 0;

    public DataFFTView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        this.attrs = attrs;

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.LTGRAY);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(4f);

        path = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paint);
    }

    public void addNewMagValue(double magVal) {
        if (index == initWindowSize - 1){
            increasing = false;
        }
        if (increasing) {
            fft_values[index] = magVal;
            index++;
        }
        else {
            for (int i = initWindowSize - 1; i == 0; i--){
                fft_values[i-1] = fft_values[i];
            }
            fft_values[initWindowSize - 1] = magVal;
        }

        double[] fft_out_x = Arrays.copyOf(fft_values, fft_values.length);
        double[] fft_out_y = new double[fft_values.length];

        FFT fft = new FFT(initWindowSize);
        fft.fft(fft_out_x, fft_out_y);

        path.reset();
        for(int i = 0; i < fft_out_x.length; i++){
            double abs = fft.abs(fft_out_x[i], fft_out_y[i]);
            path.lineTo(i, (float) abs);
        }
        invalidate();
    }

    public void changeWindowSize(int newWindowSize) {
        if(newWindowSize < 2) return;
        initWindowSize = (int) Math.pow(2, newWindowSize);
    }
}
