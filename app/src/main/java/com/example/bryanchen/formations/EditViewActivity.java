package com.example.bryanchen.formations;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.savvisingh.colorpickerdialog.ColorPickerDialog;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class EditViewActivity extends AppCompatActivity {
    ArrayList<Dot> dots = new ArrayList<>();
    float top = 100;
    float bottom = 1500;
    float left = 20;
    float right = 1050;
    private Button editPeople;
    private ArrayList<Integer> closestColorsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            dots = savedInstanceState.getParcelableArrayList("DOTS");
        }
        String[] colorsTxt = getApplicationContext().getResources().getStringArray(R.array.colors);
        for (int i = 0; i < colorsTxt.length; i++) {
            int newColor = Color.parseColor(colorsTxt[i]);
            closestColorsList.add(newColor);
        }

        if (getIntent().getParcelableArrayListExtra("DOTS") != null) {
            dots = getIntent().getParcelableArrayListExtra("DOTS");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_view);


        editPeople = (Button) findViewById(R.id.editDancer);

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
                clearDots();
                data.putParcelableArrayListExtra("DOTS", dots);
                setResult(RESULT_OK, data);
                finish();
            }
        });

    }

    public void clearDots() {
        for (Dot i : dots) {
            i.setSelected(false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        clearDots();
        outState.putParcelableArrayList("DOTS", dots);
        super.onSaveInstanceState(outState);
    }

    public class SingleTouchEventView extends View {
        private Paint paint = new Paint();
        private Paint selectPaint = new Paint();
        private Paint textPaint = new Paint();
        Dot selectedDot = null;
        private float mDownX;
        private float mDownY;
        private final float SCROLL_THRESHOLD = 10;
        private boolean isOnClick, isMoving;


        public SingleTouchEventView(Context context) {
            super(context);
            editPeople.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // create dialog to rename and recolor
                    ColorDialogExtended dialog = ColorDialogExtended.newInstance(
                            ColorDialogExtended.SELECTION_SINGLE,
                            closestColorsList,
                            5, // Number of columns
                            ColorDialogExtended.SIZE_SMALL, selectedDot.getName(), selectedDot.getColor());

                    dialog.show(getFragmentManager(), "some_tag");

                    dialog.setOnDialodButtonListener(new ColorDialogExtended.OnDialogButtonListener() {
                        @Override
                        public void onDonePressed(HashMap<String, Integer> mSelectedColors) {
                            if (mSelectedColors.size() > 0) {
                                for (String k : mSelectedColors.keySet()) {
                                    if (k != "NO_NAME") {
                                        selectedDot.setName(k);
                                    }
                                    selectedDot.setColor(mSelectedColors.get(k));
                                }
                            }
                        }

                        @Override
                        public void onDismiss() {
                        }
                    });

                }
            });
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            // draw stage
            paint.setColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
            paint.setStrokeWidth(5);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(left, top, right, bottom, paint);

            // set up paint to draw dots
            paint.setStyle(Paint.Style.FILL);
            selectPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            selectPaint.setColor(Color.BLUE);
            textPaint.setColor(Color.BLACK);
            textPaint.setTextAlign(Paint.Align.CENTER);

            // drawing dots
            for (Dot p : dots) {
                paint.setColor(p.getColor());
                int size = p.getName().length();
                if (size > 0) {
                    textPaint.setTextSize(26 - 3 * size / 4);
                } else {
                    textPaint.setTextSize(10f);
                }
                if (p.isSelected()) {
                    editPeople.setVisibility(VISIBLE);
                    selectedDot = p;
                    canvas.drawCircle(p.getX(), p.getY(), (float) p.getDiameter() + 10, selectPaint);
                } else {
                    canvas.drawCircle(p.getX(), p.getY(), (float) p.getDiameter(), paint);
                }
                canvas.drawText(p.getName(), p.getX(), p.getY(), textPaint);
            }
            invalidate();
        }

        public boolean onTouchEvent(MotionEvent event) {
            boolean occupied = false;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isMoving = false;
                    Log.e("Hi we here", "supDOWNNN");
                    for (Dot d : dots) {
                        // check if we are initially hitting a dot on down motion. If we are, we want to modify this dot
                        if (d.isHit(event.getX(), event.getY())) {
                            if (selectedDot != null) {
                                selectedDot.setSelected(false);
                            }
                            d.setSelected(true);
                            selectedDot = d;
                            mDownX = event.getX();
                            mDownY = event.getY();
                            isOnClick = true;

                            // the editPeople button should now come up
                            editPeople.setVisibility(VISIBLE);
                            break;
                        }
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.e("Hi we here", "sup");
                    float x = event.getX();
                    float y = event.getY();
                    for (Dot d : dots) {   // if we hit another dot, we cannot move this dot over it
                        if (d.isHit(x, y) && d != selectedDot) {
                            occupied = true;
                            break;
                        }
                    }
                    if (!occupied) {   // if the spot isn't occupied while we're moving, we should move the dot around
                        if (selectedDot != null) {
                            // this makes sure that we are actually trying to move the dot, and not just touching it
                            if (isOnClick && (Math.abs(mDownX - x) > SCROLL_THRESHOLD || Math.abs(mDownY - y) > SCROLL_THRESHOLD)) {
                                selectedDot.setLocation(x, y);
                                isMoving = true;
                            }
                        }
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    Log.e("HI we here", "UPPPP");
                    isOnClick = false;
                    x = (Math.round(event.getX() / 50) * 50);
                    y = (Math.round(event.getY() / 50) * 50);

                    // if the touch is in range
                    if (x <= right && x >= left && y <= bottom && y >= top) {
                        for (Dot d : dots) {
                            if (d.isHit(x, y)) {    // if the spot is occupied, we cannot drop the dot here
                                occupied = true;
                                selectedDot = null;
                                break;
                            }
                        }
                        // if the spot isn't occupied where user lifts finger, hide the button
                        if (!occupied) {
                            if (selectedDot != null) {    //if we currently hold a dot, we are dropping the dot here
                                if (isMoving) {
                                    selectedDot.setLocation(x, y);
                                    selectedDot.setSelected(false);
                                    selectedDot = null;
                                } else {
                                    selectedDot.setSelected(false);
                                    selectedDot = null;
                                }
                                editPeople.setVisibility(INVISIBLE);
                                break;
                            }
                            Dot p = new Dot(x, y);        // otherwise, we add a new dot here
                            dots.add(p);
                        }
                        selectedDot = null;
                        // otherwise, it is outside of range, so we delete this dot
                    } else {
                        dots.remove(selectedDot);
                        selectedDot = null;
                    }
                    isMoving = false;
                    break;
            }
            invalidate();
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        Log.e("BAAAACCCKKK", "IT UPPPP biatch");
    }
}




