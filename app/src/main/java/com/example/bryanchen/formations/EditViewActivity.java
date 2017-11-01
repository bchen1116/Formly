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
        private Paint selectPaint = new Paint();
        private Paint textPaint = new Paint();
//        List<Dot> dots = new ArrayList<>();
        Dot selectedDot = null;
        Dot current = null;
        private float mDownX;
        private float mDownY;
        private final float SCROLL_THRESHOLD = 10;
        private boolean isOnClick;



        public SingleTouchEventView(Context context) {
            super(context);
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            // draw stage
            paint.setColor(Color.CYAN);
            paint.setStrokeWidth(5);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(left, top, right, bottom, paint);

            //draw dots
            paint.setStyle(Paint.Style.FILL);
            selectPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            selectPaint.setColor(Color.BLUE);
            textPaint.setColor(Color.BLACK);
            textPaint.setTextAlign(Paint.Align.CENTER);

            for (Dot p : dots) {
                paint.setColor(p.getColor());
                Log.e("COLOR ", ""+p.getColor());
                int size = p.getName().length();
                if (size > 0){
                    textPaint.setTextSize(26 - 3*size/4);
                } else {
                    textPaint.setTextSize(10f);
                }
                if (current != null) {
                    if (p == current) {
                        canvas.drawCircle(p.getX(), p.getY(), (float) p.getDiameter()+10, selectPaint);
                    } else {
                        canvas.drawCircle(p.getX(), p.getY(), (float) p.getDiameter(), paint);
                    }
                    canvas.drawText(p.getName(), p.getX(), p.getY(), textPaint);
                } else {
                    canvas.drawCircle(p.getX(), p.getY(), (float) p.getDiameter(), paint);
                    canvas.drawText(p.getName(), p.getX(), p.getY(), textPaint);
                }
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
                            current = d;
                            mDownX = event.getX();
                            mDownY = event.getY();
                            isOnClick = true;

                            editPeople.setVisibility(VISIBLE);
                            editPeople.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    Toast.makeText(getContext(), "WE IN "+ current.toString(), Toast.LENGTH_SHORT).show();
                                    ColorPickerDialog dialog = ColorPickerDialog.newInstance(
                                            ColorPickerDialog.SELECTION_SINGLE,
                                            closestColorsList,
                                            5, // Number of columns
                                            ColorPickerDialog.SIZE_SMALL);

                                    dialog.show(getFragmentManager(), "some_tag");

                                    dialog.setOnDialodButtonListener(new ColorPickerDialog.OnDialogButtonListener() {
                                        @Override
                                        public void onDonePressed(ArrayList<Integer> mSelectedColors) {
                                            current.setColor(mSelectedColors.get(0));
                                        }
                                        @Override
                                        public void onDismiss() {
                                        }
                                    });

                                    // Creating alert Dialog with one Button
                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditViewActivity.this);

//                                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();

                                    // Setting Dialog Title
                                    alertDialog.setTitle("SET NAME");

                                    // Setting Dialog Message
//                                    alertDialog.setMessage("Enter a name");
                                    final EditText input = new EditText(EditViewActivity.this);



                                    ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(
                                            ConstraintLayout.LayoutParams.MATCH_PARENT,
                                            ConstraintLayout.LayoutParams.MATCH_PARENT);
                                    input.setLayoutParams(lp);
                                    alertDialog.setView(input);

                                    if (current.getName() != "") {
                                        input.setText(current.getName());
                                    }
                                    // Setting Positive "Yes" Button
                                    alertDialog.setPositiveButton("YES",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,int which) {
                                                    // Write your code here to execute after dialog
                                                    Toast.makeText(getApplicationContext(),input.getText().toString(), Toast.LENGTH_SHORT).show();
                                                    current.setName(input.getText().toString());
                                                }
                                            });
                                    // Setting Negative "NO" Button
                                    alertDialog.setNegativeButton("NO",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // Write your code here to execute after dialog
                                                    dialog.cancel();
                                                }
                                            });

                                    // closed

                                    // Showing Alert Message
                                    alertDialog.show();

                                }
                            });
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
                            if (isOnClick && (Math.abs(mDownX - x) > SCROLL_THRESHOLD || Math.abs(mDownY - y) > SCROLL_THRESHOLD)) {
                                selectedDot.setLocation(x, y);
                            }
                        }
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    isOnClick = false;
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
                            if (current != null) {
                                current = null;
                                editPeople.setVisibility(INVISIBLE);
                            } else {
                                if (selectedDot != null) {
                                    selectedDot.setLocation(x, y);
                                    selectedDot = null;
                                    break;
                                }
                                Dot p = new Dot(x, y);
                                dots.add(p);
                            }
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




