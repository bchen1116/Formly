package com.example.bryanchen.formations;
import android.graphics.Color;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
//import android.annotation.TargetApi;
//import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;

/**
 * Created by BryanChen on 9/20/17.
 */

public class Dot implements Serializable{
    private String name;
    private float xLocation, yLocation;
    private Color color;
    private double diameter;
    private final double DEFAULT_DIAMETER = 40;


    public Dot(float x, float y) {
        this.name = "";
        this.xLocation = x;
        this.yLocation = y;
        this.color = new Color();
        this.diameter = DEFAULT_DIAMETER;

    }
//
//    private Dot(Parcel in) {
//        this.name = in.readString();
//        this.xLocation = Float.parseFloat(in.readString());
//        this.yLocation = Float.parseFloat(in.readString());
//        this.diameter = Float.parseFloat(in.readString());
//    }
//    @Override
//    public int describeContents() {
//        // TODO Auto-generated method stub
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//
//        dest.writeString(this.name);
//        dest.writeString(String.valueOf(this.xLocation));
//        dest.writeString(String.valueOf(this.yLocation));
//        dest.writeString(String.valueOf(this.diameter));
//
//    }
//
//    public static final Parcelable.Creator<Dot> CREATOR = new Parcelable.Creator<Dot>() {
//        public Dot createFromParcel(Parcel in) {
//            return new Dot(in);
//        }
//
//        public Dot[] newArray(int size) {
//            return new Dot[size];
//
//        }
//    };
    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(float x, float y) {
        this.xLocation = x;
        this.yLocation = y;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setDiameter(double diam) {
        this.diameter = diam;
    }

    public void setDefaultDiameter() {
        this.diameter = DEFAULT_DIAMETER;
    }

    public String getName() {
        return this.name;
    }

    public float getX() {
        return this.xLocation;
    }

    public float getY() {
        return this.yLocation;
    }

    public Color getColor() {
        return this.color;
    }

    public double getDiameter() {
        return this.diameter;
    }

    public boolean isHit(float x, float y) {
        float dx = x-this.getX();
        float dy = y-this.getY();
        double length = Math.sqrt(dx*dx+dy*dy);
        return length <= 2*diameter;
    }

}
