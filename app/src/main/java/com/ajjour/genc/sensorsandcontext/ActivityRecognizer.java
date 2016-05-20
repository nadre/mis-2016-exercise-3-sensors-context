package com.ajjour.genc.sensorsandcontext;

import java.util.Arrays;

public class ActivityRecognizer {

    static double restThres = 115;
    static double walkThres = 160;

    //  will use the first 10% of values.
    static double cutOffPercentage = 0.1;

    public static ActivityTypes recognizeActivity(double[] fft_magnitude){

        int windowSize = fft_magnitude.length;

        int cutOff = (int)(windowSize * cutOffPercentage);

        if (cutOff == 0) cutOff++;

        double[] frequencyComponents = Arrays.copyOf(fft_magnitude, cutOff);

        double avg = 0.0;
        for (double frequencyComponent : frequencyComponents) {
            avg += frequencyComponent;
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
