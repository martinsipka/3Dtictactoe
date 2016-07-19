package com.example.dtictactoe.frontend.animations;

/**
 * Created by martin on 17.7.16.
 */
public class NiceInterpolator {

    float begin;
    float end;

    public NiceInterpolator(float begin, float end){
        this.begin = begin;
        this.end = end;
    }

    public float smoothStep(float x){
        return  ((x) * (x) * (x) * ((x) * ((x) * 6 - 15) + 10));
    }

    public float interpolate(float step){
        float v = smoothStep(step);
        return (end * v) + (begin * (1 - v));
    }
}
