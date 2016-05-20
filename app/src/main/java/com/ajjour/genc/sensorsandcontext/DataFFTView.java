package com.ajjour.genc.sensorsandcontext;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v4.app.NotificationCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import static com.ajjour.genc.sensorsandcontext.ActivityRecognizer.recognizeActivity;

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
    Point[] points;

    ActivityTypes currActivity = ActivityTypes.RESTING;

    //should be an power of 2
    int windowSize = 128;

    FFT fft = new FFT(windowSize);

    CircularFifoQueue<Double> magnitudes = new CircularFifoQueue<Double>(windowSize);

    int counter = 0;

    public DataFFTView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        this.attrs = attrs;

        paint = new Paint();
        paint.setColor(Color.MAGENTA);
        paint.setStrokeWidth(10f);

        points = new Point[windowSize];
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
        canvas.drawColor(Color.TRANSPARENT);
        for (Point point : points) {
            if(point == null) continue;
            canvas.drawPoint(point.x, point.y, paint);
        }
    }

    public void handleNewMagnitudeValue(double magVal) {

        magnitudes.add(magVal);

        // don't calculate until the window is filled
        if(magnitudes.size() < magnitudes.maxSize()) return;

        // do not draw at every step
//        counter++;
//        if (counter != 10) return;
//        counter = 0;

        if (mCanvas == null) return;

        double[] fft_out_x = new double[windowSize];
        double[] fft_out_y = new double[windowSize];
        double[] fft_magnitudes = new double[windowSize];

        for (int i = 0; i < magnitudes.size(); i++) {
            fft_out_x[i] = magnitudes.get(i);
        }

        fft.fft(fft_out_x, fft_out_y);

        float expansionFactor = (float) mCanvas.getWidth() / (float) windowSize;

        double abs;
        for(int i = 0; i < windowSize; i++){
            abs = fft.abs(fft_out_x[i], fft_out_y[i]);
            fft_magnitudes[i] = abs;
            points[i] = new Point((int) (i * expansionFactor),(int)  (mCanvas.getHeight () - (float) abs));
        }
        invalidate();

        ActivityTypes newActivity = recognizeActivity(fft_magnitudes);

        if (newActivity != currActivity){
            currActivity = newActivity;
            Log.d("ACTIVITY", newActivity.name());
            ((DataVizActivity) getContext()).sendNotification(newActivity.name());
        }

    }


    public void changeWindowSize(int newWindowSize) {
        if(newWindowSize < 2) return;
        windowSize = (int) Math.pow(2, newWindowSize);
        Log.d("newWindowSize", ""+newWindowSize);
        magnitudes = new CircularFifoQueue<Double>(windowSize);
        points = new Point[windowSize];
        fft = new FFT(windowSize);
    }
}
