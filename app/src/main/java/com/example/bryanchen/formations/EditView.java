package com.example.bryanchen.formations;

import android.content.Context;
import android.view.View;
import java.util.Optional;
import android.graphics.Color;
/**
 * Created by BryanChen on 9/22/17.
 */

public class EditView {

    public void addDot(float x, float y, Optional<Integer> color) {
        Dot t = new Dot(x, y);
//        if (color.isPresent()) {
//            t.setColor(Color.valueOf(Integer.valueOf(color)));
//        }
    }

    public void setDotName(String name, Dot t) {
        t.setName(name);
    }


}
