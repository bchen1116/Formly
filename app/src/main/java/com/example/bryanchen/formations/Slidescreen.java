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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class Slidescreen extends Fragment {
    private List<Dot> dots = new ArrayList<>();
    public int page;
    public Slidescreen() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_slidescreen, container, false);
        MainActivity main = (MainActivity) this.getActivity();
        drawView draw = new drawView(main);

        dots = main.getDots();
        for (Dot p: dots) {
            Log.v("FRAG_DOT", "new dot");
        }

//        rootView.addView(draw);
        LinearLayout linearLayout = rootView.findViewById(R.id.textView);
        linearLayout.addView(new drawView(getActivity()));
//        text.setText(String.valueOf("Slide "+ this.getInt()+1));
        return rootView;
    }

    public Slidescreen newInstance(String key, int p) {
        Slidescreen fragment = new Slidescreen();
        Bundle args = new Bundle();
        args.putInt(key, p);
        fragment.setArguments(args);
        this.page = p;
        return fragment;
    }

    public class drawView extends View {
        Paint paint = new Paint();
        //        List<Dot> dots = new ArrayList<>();

        public drawView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            Log.v("DRAW", "plsplspls");
            super.onDraw(canvas);
            paint.setColor(Color.CYAN);
            paint.setStrokeWidth(5);
            paint.setStyle(Paint.Style.STROKE);
//            canvas.drawRect(left, top, right, bottom, paint);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
            for (Dot p : dots) {
                canvas.drawCircle(p.getX(), p.getY(), (float) p.getDiameter(), paint);
            }
            invalidate();
        }
    }

    public int getInt() {
        return this.page;
    }
}
