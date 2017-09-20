package com.example.bryanchen.formations;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.Optional;
//import android.annotation.TargetApi;
//import android.os.Build;
import android.support.v4.content.ContextCompat;

/**
 * Created by BryanChen on 9/20/17.
 */

public class Dot {
    private String name;
    private double xLocation, yLocation;
    private Color color;
    private double diameter;
    private final double DEFAULT_DIAMETER = 1.5;

//    @TargetApi(Build.VERSION_CODES.M)
    public void Dot(Optional<String> name, double x, double y) {
        this.name = name.isPresent() ? name.toString() : "";
        this.xLocation = x;
        this.yLocation = y;
        this.color = new Color();
        this.diameter = DEFAULT_DIAMETER;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(double x, double y) {
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

    public ArrayList<Double> getLocation() {
        ArrayList<Double> location = new ArrayList<Double>();
        location.add(xLocation);
        location.add(yLocation);
        return location;
    }

    public Color getColor() {
        return this.color;
    }

    public double getDiameter() {
        return this.diameter;
    }

}
