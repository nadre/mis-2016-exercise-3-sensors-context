package com.ajjour.genc.sensorsandcontext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

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

    float[] values;
    float T;
    float dx;
    float dy;
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

        values = new float[4];
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        startTimeMillis = System.currentTimeMillis();

        dx = this.getHeight() / 2;
        dy = this.getWidth() / 2;
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

        float newVal;
        float oldVal;

        long t = (System.currentTimeMillis() - startTimeMillis) / 10;

        for (int i = 0; i < 3; i++){
            newVal = newValues[i];
            newVal = (newVal * 10) + dx;
            oldVal = this.values[i];
            paths[i].quadTo(T, oldVal, (t + T) / 2, (newVal + oldVal) / 2);
            this.values[i] = newVal;
        }
        T = t;

        invalidate();
    }
}
