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
import java.util.concurrent.atomic.AtomicLong;
//import android.annotation.TargetApi;
//import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;

/**
 * Created by BryanChen on 9/20/17.
 */

public class Dot implements Parcelable{
    static final AtomicLong NEXT_ID = new AtomicLong(0);
    private long id = NEXT_ID.getAndIncrement();

    public String name;
    private float xLocation, yLocation;
    public int color;
    private double diameter;
    private final double DEFAULT_DIAMETER = 40;
    private boolean selected;


    public Dot(float x, float y) {
        this.name = "";
        this.xLocation = x;
        this.yLocation = y;
        this.color = Color.GRAY;
        this.diameter = DEFAULT_DIAMETER;
        selected = false;

    }

    public Dot(int color, int diameter, Long id, String name, boolean selected, float x, float y) {
        this.name = name;
        this.xLocation = x;
        this.yLocation = y;
        this.color = color;
        this.diameter = diameter;
        this.selected = selected;
        this.id = id;
    }

    public Dot(Map<String, Object> data) {
        Double x = (Double) data.get("x");
        Double y = (Double) data.get("y");
        Long c = (Long) data.get("color");
        Double d = (Double) data.get("diameter");
        Long id = (Long) data.get("id");

        this.name = (String) data.get("name");
        this.xLocation = x.floatValue();
        this.yLocation = y.floatValue();
        this.color = c.intValue();
        this.diameter = d.doubleValue();
        this.selected = (boolean) data.get("selected");
        this.id = id.longValue();
    }

    private Dot(Parcel in) {
        this.name = in.readString();
        this.xLocation = Float.parseFloat(in.readString());
        this.yLocation = Float.parseFloat(in.readString());
        this.color = Integer.parseInt(in.readString());
        this.diameter = Float.parseFloat(in.readString());
        this.id = Long.parseLong(in.readString());
        this.selected = Boolean.parseBoolean(in.readString());
    }
    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(this.name);
        dest.writeString(String.valueOf(this.xLocation));
        dest.writeString(String.valueOf(this.yLocation));
        dest.writeString(String.valueOf(this.color));
        dest.writeString(String.valueOf(this.diameter));
        dest.writeString(String.valueOf(this.id));
        dest.writeString(String.valueOf(this.selected));

    }

    public static final Parcelable.Creator<Dot> CREATOR = new Parcelable.Creator<Dot>() {
        public Dot createFromParcel(Parcel in) {
            return new Dot(in);
        }

        public Dot[] newArray(int size) {
            return new Dot[size];

        }
    };
    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(float x, float y) {
        this.xLocation = x;
        this.yLocation = y;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setDiameter(double diam) {
        this.diameter = diam;
    }

    public void setDefaultDiameter() {
        this.diameter = DEFAULT_DIAMETER;
    }

    public void setSelected(boolean select) {
        this.selected = select;
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

    public int getColor() {
        return this.color;
    }

    public double getDiameter() {
        return this.diameter;
    }

    public Long getID() {return this.id; }

    public boolean isSelected() { return this.selected;}

    public boolean isHit(float x, float y) {
        float dx = x-this.getX();
        float dy = y-this.getY();
        double length = Math.sqrt(dx*dx+dy*dy);
        return length <= 2*diameter;
    }

    @Override
    public String toString(){
        return this.name + " at location " + "(" + this.xLocation + ", " + this.yLocation + ")";
    }
//    public boolean inBounds(float top, float bottom, float left, float right) {
//        Boolean vertical = this.yLocation >= top && this.yLocation <= bottom;
//        Boolean horizontal = this.xLocation >= left && this.xLocation <= right;
//        return vertical && horizontal;
//    }

}
