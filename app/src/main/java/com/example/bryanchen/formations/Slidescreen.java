package com.example.bryanchen.formations;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class Slidescreen extends Fragment {
    private List<Dot> dots = new ArrayList<>();
    public int page;
    ViewGroup rootView;
    drawView s;
    public Slidescreen() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_slidescreen, container, false);

        s = new drawView(getActivity());
        ViewGroup linear = (ViewGroup) rootView.findViewById(R.id.linear);
        linear.addView(s);
        return rootView;
    }

    public ViewGroup setDots(List<Dot> d) {
        dots = d;
//        for (Dot f: dots) {
//            Log.v("SET DOTS", "dotdotdot");
//        }
        return rootView;
    }

    public void updateDots(List<Dot> d) {
        if (dots != null) {
            for (Dot dot: d) {
                for (Dot thisDot: dots) {
                    if (thisDot.getID() == dot.getID()){
                        thisDot.setName(dot.getName());
//                        thisDot.setColor(dot.getColor());
                    }
                }
            }
        }
    }

    public List<Dot> getDots() {
        return dots;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", page);
    }

    public Slidescreen newInstance(String key, int p) {
        Slidescreen fragment = new Slidescreen();
        Bundle args = new Bundle();
        args.putInt(key, p);
        fragment.setArguments(args);
        this.page = p;
        onSaveInstanceState(args);
        return fragment;
    }

    public class drawView extends View {
        Paint paint = new Paint();
        private Paint textPaint = new Paint();

        public drawView(Context context) {
            super(context);
            this.setWillNotDraw(false);
//            Log.v("AM I DRAWING?", ""+ context.toString());
        }
        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(1050, 1500);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            paint.setColor(Color.CYAN);
            paint.setStrokeWidth(5);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(10, 30, 1000, 1200, paint);

            paint.setStyle(Paint.Style.FILL);
            textPaint.setColor(Color.BLACK);
            textPaint.setTextAlign(Paint.Align.CENTER);

            if (dots != null) {
                for (Dot p : dots) {
                    Log.e("DOTS", p.toString() + " color " + p.getColor());
                    paint.setColor(p.getColor());
                    int size = p.getName().length();
                    if (size > 0){
                        textPaint.setTextSize(26 - 3*size/4);
                    } else {
                        textPaint.setTextSize(25f);
                    }
                    float xLoc = p.getX() * (53 / (float) 56);
                    float yLoc = p.getY() * (11 / (float) 14);
                    canvas.drawCircle(xLoc, yLoc, (float) p.getDiameter(), paint);
                    canvas.drawText(p.getName(), xLoc, yLoc, textPaint);
                }
                invalidate();
            }
        }
    }

    public int getInt() {
        return this.page;
    }
}
