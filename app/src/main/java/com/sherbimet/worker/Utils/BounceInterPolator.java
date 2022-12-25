package com.sherbimet.worker.Utils;

import android.view.animation.Interpolator;

public class BounceInterPolator implements Interpolator {
    double mAmplitude = 1;
    double mFrequency = 10;

    BounceInterPolator() {
    }

    public BounceInterPolator(double amp, double freq) {
        mAmplitude = amp;
        mFrequency = freq;
    }

    public float getInterpolation(float time) {
        return (float) (-1 * Math.pow(Math.E, -time / mAmplitude) * Math.cos(mFrequency * time) + 1);
    }
}
