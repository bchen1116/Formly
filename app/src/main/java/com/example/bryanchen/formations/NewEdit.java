package com.example.bryanchen.formations;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.google.firebase.firestore.FirebaseFirestore;
import com.savvisingh.colorpickerdialog.ColorPickerDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Math.min;

public class NewEdit extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<Dot> dots = new ArrayList<>();
    ArrayList<Dot> deleted = new ArrayList<>();
    float top = 10;
    float bottom = 1200;
    float left = 10;
    float right = 1000;
    private Button editPeople;
    private ArrayList<Integer> closestColorsList = new ArrayList<>();
    float lefty, topy, righty, bottomy;
    private boolean isGrid;

    // creates editViewActivity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            dots = savedInstanceState.getParcelableArrayList("DOTS");
        }

        // sets the colors for the dialog
        String[] colorsTxt = getApplicationContext().getResources().getStringArray(R.array.colors);
        for (int i = 0; i < colorsTxt.length; i++) {
            int newColor = Color.parseColor(colorsTxt[i]);
            closestColorsList.add(newColor);
        }

        // checks if there are dots passed through and sets them if there are
        if (getIntent().getParcelableArrayListExtra("DOTS") != null) {
            dots = getIntent().getParcelableArrayListExtra("DOTS");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_edit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Mode");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Switch gridView = findViewById(R.id.grid);
        isGrid = gridView.isChecked();
        gridView.setText("Grid");
        gridView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isGrid = !isGrid;
                if (isGrid) {
                    Toast.makeText(getApplicationContext(), "Make grid", Toast.LENGTH_SHORT).show();
                    makeGridLayout();
                } else {
                    Toast.makeText(getApplicationContext(), "Don't make grid", Toast.LENGTH_SHORT).show();
                }
            }
        });
        editPeople = (Button) findViewById(R.id.editDancer);

        // create a viewGroup to use SingleTouchEventView on the activity_new_edit.xml
        ViewGroup mainView = (ViewGroup) findViewById(R.id.editLayout);

        // overlay the touchView on this xml
        SingleTouchEventView touch = new SingleTouchEventView(this.getApplicationContext());
        mainView.addView(touch);

        // exit on done button
        Button doneButton = (Button) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                endSession();
            }
        });

    }

    // takes care of back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void endSession() {
        Toast.makeText(getApplicationContext(), "Exiting add mode", Toast.LENGTH_SHORT).show();
        Intent data = new Intent();
        clearDots();
        data.putParcelableArrayListExtra("DOTS", dots);
        data.putParcelableArrayListExtra("DELETED", deleted);
        setResult(RESULT_OK, data);
        finish();
    }
    // unselects all dots
    public void clearDots() {
        for (Dot i : dots) {
            i.setSelected(false);
        }
    }

    // makes it into grid layout
    public void makeGridLayout() {
        for (Dot i: this.dots) {
            i.setLocation(125*(float)Math.round(i.getX()/(double)125), 125*(float)Math.round(i.getY()/(double)125));
        }
    }

    // sets it in grid layout
    public void dropGrid(Dot i) {
        i.setLocation(125*(float)Math.round(i.getX()/(double)125), 125*(float)Math.round(i.getY()/(double)125));
    }
    // auto-generated saver
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public class SingleTouchEventView extends View {
        private Paint paint = new Paint();
        private Paint selectPaint = new Paint();
        private Paint textPaint = new Paint();
        Dot selectedDot = null;
        List<Dot> multiselect = new ArrayList<>();
        private float mDownX;
        private float mDownY;
        private final float SCROLL_THRESHOLD = 10;
        private boolean isOnClick, isMoving;

        // opens a dialog that allows setting the name and color of the dot
        public SingleTouchEventView(Context context) {
            super(context);
            editPeople.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
//                    if(multiselect.size() == 0) {
                    // create dialog to rename and recolor
                    ColorDialogExtended dialog = ColorDialogExtended.newInstance(
                            ColorDialogExtended.SELECTION_SINGLE,
                            closestColorsList,
                            5, // Number of columns
                            ColorDialogExtended.SIZE_SMALL, selectedDot.getName(), selectedDot.getColor(), selectedDot.isLarge());

                    dialog.show(getFragmentManager(), "some_tag");

                    dialog.setOnDialodButtonListener(new ColorDialogExtended.OnDialogButtonListener() {
                        @Override
                        public void onDonePressed(HashMap<String, Integer> mSelectedColors) {
                            if (mSelectedColors.size() > 0) {
                                for (String k : mSelectedColors.keySet()) {
                                    String name = k.substring(0, k.length()-1);
                                    int sizeChange = Integer.valueOf(k.substring(k.length()-1));
                                    if (name != "NO_NAME") {
                                        selectedDot.setName(name);
                                    }
                                    selectedDot.setColor(mSelectedColors.get(k));
                                    if (sizeChange == 1) {
                                        selectedDot.setDiameter();
                                    } else {
                                        selectedDot.setDefaultDiameter();
                                    }
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

        // draws the stage and dots and animates the dot moving
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            // draw stage
            paint.setColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
            paint.setStrokeWidth(5);
            paint.setStyle(Paint.Style.STROKE);
            float canvasHeight = canvas.getHeight()/2;
            float canvasWidth = canvas.getWidth()/2;
            lefty = canvasWidth - (right-left)/2;
            righty = canvasWidth + (right-left)/2;
            topy = canvasHeight - (bottom-top)/2-20;
            bottomy = canvasHeight + (bottom-top)/2-20;
            canvas.drawRect(lefty, topy, righty, bottomy, paint);

            // set up paint to draw dots
            paint.setStyle(Paint.Style.FILL);
            selectPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            textPaint.setColor(Color.BLACK);
            textPaint.setTextAlign(Paint.Align.CENTER);
            Paint outline = new Paint();
            outline.setStyle(Paint.Style.FILL);
            outline.setColor(Color.WHITE);

            // drawing dots
            for (Dot p : dots) {
                paint.setColor(p.getColor());
                textPaint.setTextSize(30);
                textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

                if (p.isSelected()) {
                    editPeople.setVisibility(VISIBLE);
                    selectedDot = p;
                    selectPaint.setColor(p.getColor());
                    canvas.drawCircle(p.getX()+lefty, p.getY()+topy, (float) p.getDiameter()+20, selectPaint);
                    canvas.drawCircle(p.getX()+lefty, p.getY()+topy, (float) p.getDiameter()+10, outline);
                    canvas.drawCircle(p.getX()+lefty, p.getY()+topy, (float) p.getDiameter(), selectPaint);

                } else {
                    canvas.drawCircle(p.getX()+lefty, p.getY()+topy, (float) p.getDiameter(), paint);
                }
                String tempName = "";
                if (p.getName().length() > 0) {
                    String[] temp = p.getName().split(" ");
                    if (temp.length > 1) {
                        tempName = temp[0].substring(0, 1).toUpperCase() + temp[1].substring(0, 1).toUpperCase();
                    } else {
                        tempName = temp[0].substring(0, min(2, temp[0].length()));
                    }
                }
                canvas.drawText(tempName, p.getX()+lefty, p.getY()+topy, textPaint);
            }
            invalidate();
        }

        // logic method for moving around the dots
        public boolean onTouchEvent(MotionEvent event) {
            boolean occupied = false;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    float x = event.getX();
                    float y = event.getY();
                    if (!(x < righty && x > lefty && y < bottomy && y > topy)) {
                        try {
                            selectedDot.setSelected(false);
                            selectedDot = null;
                        } catch (Exception e) {}
                    } else {
                        isMoving = false;
                        for (Dot d : dots) {
                            // check if we are initially hitting a dot on down motion. If we are, we want to modify this dot
                            if (d.isHit(x - lefty, y - topy)) {
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
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    x = event.getX();
                    y = event.getY();
                    for (Dot d : dots) {   // if we hit another dot, we cannot move this dot over it
                        if (d.isHit(x-lefty, y-topy) && d != selectedDot) {
                            occupied = true;
                            break;
                        }
                    }
                    if (!occupied) {   // if the spot isn't occupied while we're moving, we should move the dot around
                        if (selectedDot != null) {
                            // this makes sure that we are actually trying to move the dot, and not just touching it
                            if (isOnClick && (Math.abs(mDownX - x) > SCROLL_THRESHOLD || Math.abs(mDownY - y) > SCROLL_THRESHOLD)) {
                                selectedDot.setLocation(x-lefty, y-topy);
                                if (isGrid) {
                                    dropGrid(selectedDot);
                                }
                                isMoving = true;
                            }
                        }
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    isOnClick = false;
                    x = (Math.round(event.getX() / 50) * 50);
                    y = (Math.round(event.getY() / 50) * 50);

                    // if the touch is in range
                    if (x < righty && x > lefty && y < bottomy && y > topy) {
                        for (Dot d : dots) {
                            if (d.isHit(x-lefty, y-topy)) {    // if the spot is occupied, we cannot drop the dot here
                                occupied = true;
                                selectedDot = null;
                                break;
                            }
                        }
                        // if the spot isn't occupied where user lifts finger, hide the button
                        if (!occupied) {
                            if (selectedDot != null) {    //if we currently hold a dot, we are dropping the dot here
                                if (isMoving) {
                                    if (isGrid) {
                                        selectedDot.setLocation(x-lefty, y-topy);
                                        selectedDot.setSelected(false);
                                        dropGrid(selectedDot);
                                    } else {
                                        selectedDot.setLocation(x - lefty, y - topy);
                                        selectedDot.setSelected(false);
                                    }
                                    selectedDot = null;
                                } else {
                                    selectedDot.setSelected(false);
                                    selectedDot = null;
                                }
                                editPeople.setVisibility(INVISIBLE);
                                break;
                            }

                            Dot p = new Dot(x-lefty, y-topy);        // otherwise, we add a new dot here
                            if (isGrid) {
                                dropGrid(p);
                            }
                            dots.add(p);
                        }
                        selectedDot = null;
                        // otherwise, it is outside of range, so we delete this dot
                    } else {
                        deleted.add(selectedDot);
                        dots.remove(selectedDot);
                        selectedDot = null;

//                        db.collection("User1").document("Fragments").

                        editPeople.setVisibility(GONE);
                    }
                    isMoving = false;
                    break;
            }
            invalidate();
            return true;
        }
    }
}




