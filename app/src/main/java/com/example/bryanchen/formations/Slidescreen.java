package com.example.bryanchen.formations;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;



public class Slidescreen extends Fragment{
    private List<Dot> dots = new ArrayList<>();
    public int page;
    ViewGroup rootView;
    drawView s;
    private String comments="";
    //    static final int EDIT_DOTS_REQUEST = 12;
    MotionEvent e;
    boolean hasMotion;
    // class member variable to save the X,Y coordinates
    private float[] lastTouchDownXY = new float[2];
    private EditText inputEdit;

    public Slidescreen() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_slidescreen, container, false);

        s = new drawView(getActivity());
        inputEdit = new EditText(getActivity());

        // the purpose of the touch listener is just to store the touch X,Y coordinates
        s.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hasMotion = false;
                // save the X,Y coordinates
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    lastTouchDownXY[0] = event.getX();
                    lastTouchDownXY[1] = event.getY();
                }
                // let the touch event pass on to whoever needs it
                comments = inputEdit.getText().toString();
                return false;
            }
        });

        s.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hasMotion = true;
                // retrieve the stored coordinates
                float x = lastTouchDownXY[0];
                float y = lastTouchDownXY[1];
                Dot selectedDot = null;
                // use the coordinates to find the dot selected, if any
                if(dots.size()>0){
                    for (Dot d:dots) {
                        if (d.isHit(x-10, y-10)) {
//                            hit = true;
                            dots = clearSelected(dots);
                            d.setSelected(true);
                        }
                    }
                }
                if (inRange(x, y)) {
                    Intent goingToEdit = new Intent(getActivity(), EditViewActivity.class);
//                    offsetDots(dots, true);
                    goingToEdit.putParcelableArrayListExtra("DOTS", (ArrayList) dots);
                    getActivity().startActivityForResult(goingToEdit, MainActivity.EDIT_DOTS_REQUEST);
                }

            }
        });

        ViewGroup linear = (ViewGroup) rootView.findViewById(R.id.linear);
        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(1000, ViewGroup.LayoutParams.WRAP_CONTENT);
        inputEdit.setLayoutParams(lparams);
        inputEdit.setHint("Comments:");
        inputEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    Toast.makeText(getContext(), "on focus", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getContext(), "lost focus", Toast.LENGTH_LONG).show();
                }
            }
        });
        linear.addView(s);
        linear.addView(inputEdit);
        if (comments.equals("")) {
            comments = inputEdit.getText().toString();
        } else {
            inputEdit.setText(this.comments);
        }
        return rootView;
    }

    public ViewGroup setDots(List<Dot> d) {
        dots = d;
        return rootView;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPage() {
        return this.page;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void updateComments() {
        this.comments = inputEdit.getText().toString();
    }

    public String getComments() {
        Log.e("THESE COMMENTS", this.comments);
        return this.comments;
    }

    public void updateDots(List<Dot> d) {
        if (dots != null) {
            for (Dot dot: d) {
                for (Dot thisDot: dots) {
                    if (thisDot.getID() == dot.getID()){
                        thisDot.setName(dot.getName());
                    }
                }
            }
        }
    }

    public List<Dot> getDots() {
        return dots;
    }

    public List<Dot> clearSelected(List<Dot> dots) {
        for (Dot i : dots) {
            i.setSelected(false);
        }
        return dots;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", page);
    }

    public Slidescreen newInstance(String key, int p) {
        Slidescreen fragment = new Slidescreen();
//        Bundle args = new Bundle();
//        args.putInt(key, p);
//        fragment.setArguments(args);
        this.page = p;
//        onSaveInstanceState(args);
        return fragment;
    }

    public class drawView extends View {
        Paint paint = new Paint();
        private Paint textPaint = new Paint();

        public drawView(Context context) {
            super(context);
            this.setWillNotDraw(false);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(1050, 1500);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            paint.setColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
            paint.setStrokeWidth(5);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(10, 10, 1000, 1200, paint);

            paint.setStyle(Paint.Style.FILL);
            textPaint.setColor(Color.BLACK);
            textPaint.setTextAlign(Paint.Align.CENTER);

            if (dots != null) {
                for (Dot p : dots) {
                    paint.setColor(p.getColor());
                    int size = p.getName().length();
                    if (size > 0){
                        textPaint.setTextSize(26 - 3*size/4);
                    } else {
                        textPaint.setTextSize(25f);
                    }
                    float xLoc = p.getX()+10;
                    float yLoc = p.getY()+10;
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

    public boolean inRange(float x, float y) {
        return x > 10 && x < 1000 && y < 1200 && y > 10;
    }

    public DotList getDotList() {
        Log.e("GETDOTLIST", this.comments);
        return new DotList(this.page, this.getDots(), this.comments);
    }

    public void onBackPressed() {
        this.comments = inputEdit.getText().toString();
    }
}