package com.ajjour.genc.sensorsandcontext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
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

    public DataVizView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        this.attrs = attrs;

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Paint paint = new Paint();
        paint.setARGB(100, 100, 100, 100);

        super.onDraw(canvas);
        canvas.drawCircle(100, 100, 100, paint);
    }

    public void clearCanvas() {
        /*TODO: Clear something*/
        invalidate();
    }
}
