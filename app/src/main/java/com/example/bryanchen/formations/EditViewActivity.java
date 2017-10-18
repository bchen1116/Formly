package com.example.bryanchen.formations;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

public class EditViewActivity extends AppCompatActivity {
    ArrayList<Dot> dots = new ArrayList<>();
    float top = 100;
    float bottom = 1500;
    float left = 20;
    float right = 1050;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent().getParcelableArrayListExtra("DOTS") != null) {
            dots = getIntent().getParcelableArrayListExtra("DOTS");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_view);

        // create a viewGroup to use SingleTouchEventView on the activity_edit_view.xml
        ViewGroup mainView = (ViewGroup) findViewById(R.id.editLayout);

        // overlay the touchView on this xml
        SingleTouchEventView touch = new SingleTouchEventView(this.getApplicationContext());
        mainView.addView(touch);


        // exit on done button
        Button doneButton = (Button) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Exiting add mode", Toast.LENGTH_SHORT).show();
                Intent data = new Intent();
                data.putParcelableArrayListExtra("DOTS", dots);

                setResult(RESULT_OK, data);
                finish();
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public class SingleTouchEventView extends View {
        private Paint paint = new Paint();
//        List<Dot> dots = new ArrayList<>();
        Dot selectedDot = null;


        public SingleTouchEventView(Context context) {
            super(context);
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            paint.setColor(Color.CYAN);
            paint.setStrokeWidth(5);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(left, top, right, bottom, paint);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
            for (Dot p : dots) {
                canvas.drawCircle(p.getX(), p.getY(), (float) p.getDiameter(), paint);
            }
            invalidate();
        }

        public boolean onTouchEvent(MotionEvent event) {
            boolean occupied = false;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    for (Dot d : dots) {
                        if (d.isHit(event.getX(), event.getY())) {
                            selectedDot = d;
                            break;
                        }
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    float x = event.getX();
                    float y = event.getY();
                    for (Dot d : dots) {
                        if (d.isHit(x, y) && d != selectedDot) {
                            occupied = true;
                            break;
                        }
                    }
                    if (!occupied) {
                        if (selectedDot != null) {
                        selectedDot.setLocation(x, y);
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    x = (Math.round(event.getX() / 50) * 50);
                    y = (Math.round(event.getY() / 50) * 50);
                    if (x<=right && x >=left && y<=bottom && y>=top) {
                        for (Dot d : dots) {
                            if (d.isHit(x, y)) {
                                occupied = true;
                                selectedDot = null;
                                break;
                            }
                        }
                        if (!occupied) {
                            if (selectedDot != null) {
                                selectedDot.setLocation(x, y);
                                selectedDot = null;
                                break;
                            }
                            Dot p = new Dot(x, y);
                            dots.add(p);
                        }
                    } else {
                        dots.remove(selectedDot);
                        selectedDot = null;
                    }
                    break;
                case MotionEvent.ACTION_CANCEL: {
                    break;
                }
            }
            invalidate();
            return true;
        }
    }

}




