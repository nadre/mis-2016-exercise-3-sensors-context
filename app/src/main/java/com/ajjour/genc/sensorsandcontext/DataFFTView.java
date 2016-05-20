package com.ajjour.genc.sensorsandcontext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import org.apache.commons.collections4.queue.CircularFifoQueue;

/**
 * Created by neffle on 15.05.16.
 *
 * Inspiration:
 * https://examples.javacodegeeks.com/android/core/graphics/canvas-graphics/android-canvas-example/
 * http://stackoverflow.com/questions/5498865/size-limited-queue-that-holds-last-n-elements-in-java
 */
public class DataFFTView extends View {

    Context context;
    AttributeSet attrs;

    Canvas mCanvas;
    Bitmap mBitmap;

    Paint paint;
    Path path;

    //should be an power of 2
    int windowSize = 512;

    FFT fft = new FFT(windowSize);

    CircularFifoQueue<Double> magnitudes = new CircularFifoQueue<Double>(windowSize);

    int counter = 0;

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

        magnitudes.add(magVal);

        // draw not every at every step
        counter++;
        if (counter != 10) return;
        counter = 0;

        double[] fft_out_x = new double[windowSize];
        double[] fft_out_y = new double[windowSize];

        for (int i = 0; i < magnitudes.size(); i++) {
            fft_out_x[i] = magnitudes.get(i);
        }

        fft.fft(fft_out_x, fft_out_y);

        path.reset();
        for(int i = 0; i < fft_out_x.length; i++){
            double abs = fft.abs(fft_out_x[i], fft_out_y[i]);
//            if(mCanvas != null && abs < mCanvas.getDensity())
                path.lineTo(i, (float) abs);
        }
        invalidate();
    }

    public void changeWindowSize(int newWindowSize) {
        if(newWindowSize < 2) return;
        windowSize = (int) Math.pow(2, newWindowSize);
        fft = new FFT(newWindowSize);
    }
}
