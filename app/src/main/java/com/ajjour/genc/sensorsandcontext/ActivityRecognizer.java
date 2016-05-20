package com.ajjour.genc.sensorsandcontext;

import android.util.Log;

import java.util.Arrays;

/**
 * Created by projectsw on 20.05.16.
 */
public class ActivityRecognizer {

    static double restThres = 125;
    static double walkThres = 160;
    static double runThres = 150.0;
    static int cutOff = 12;

    public static ActivityTypes recognizeActivity(double[] fft_magnitude){

        double[] values = fft_magnitude;
        if (fft_magnitude.length > cutOff){
            values = Arrays.copyOf(fft_magnitude, cutOff);
        }

        double avg = 0.0;
        for (double value : values) {
            avg += value;
        }
        avg = avg / cutOff;

//        Log.d("AVG", ""+avg);

        if (avg < restThres){
            return ActivityTypes.RESTING;
        }
        else if (avg < walkThres){
            return ActivityTypes.WALKING;
        }
        return ActivityTypes.RUNNING;

    }

}
