package com.example.bryanchen.formations;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<Dot> dots = null;
//    private Paint paint = new Paint();
    static final int EDIT_DOTS_REQUEST = 12;
    float top = 300;
    float bottom = 1800;
    float left = 0;
    float right = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ImageButton addButton = (ImageButton) findViewById(R.id.addButton1);

        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Adding people mode", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(v.getContext(), EditViewActivity.class);
//                dot = (ArrayList<Dot>) getIntent().getSerializableExtra("doList");
//
                startActivityForResult(intent, EDIT_DOTS_REQUEST);

            }
        });
//        if (dot != null) {
//            Canvas canvas = new Canvas();
//            Paint paint = new Paint();
//            paint.setStyle(Paint.Style.FILL);
//            paint.setColor(Color.BLACK);
//            for (Dot d : dot) {
//                canvas.drawCircle(d.getX(), d.getY(), (float) d.getDiameter(), paint);
//            }
//        }
    }

//    private class DrawView extends View {
//
//        public DrawView(Context context) {
//            super(context);
//        }
//
//        protected void onDraw(Canvas canvas) {
//            super.onDraw(canvas);
//            Paint paint = new Paint();
//            paint.setColor(Color.CYAN);
//            canvas.drawRect(left, top, right, bottom, paint);
//            paint.setColor(Color.BLACK);
//            paint.setStyle(Paint.Style.FILL);
//            for (Dot p : dots) {
//                canvas.drawCircle(p.getX(), p.getY(), (float) p.getDiameter(), paint);
//            }
//            invalidate();
//        }
//
//    }
    //        List<Dot> dots = new ArrayList<>();
//    Dot selectedDot = null;



//    protected void onDraw(Canvas canvas) {
//        onDraw(canvas);
//        paint.setColor(Color.CYAN);
//        paint.setStrokeWidth(5);
//        paint.setStyle(Paint.Style.STROKE);
//        canvas.drawRect(left, top, right, bottom, paint);
//        paint.setColor(Color.BLACK);
//        paint.setStyle(Paint.Style.FILL);
//        for (Dot p : dots) {
//            canvas.drawCircle(p.getX(), p.getY(), (float) p.getDiameter(), paint);
//        }
//        invalidate();
//    }

    /**
     * Fragment
     *      onStart()
     *
     *
     *      whenever edit ends, update fragment
     *      whenever we enter edit we can keep track of the ID of fragment we were viewing (if we were on one)
     *      use that to know which fragment to update
     *
     */

    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_DOTS_REQUEST) {
            if (resultCode == RESULT_OK) {
                dots = data.getParcelableArrayListExtra("DOTS");
                Log.v("TAG", "added dots");
                for (Dot p : dots) {
                    Log.v("NEW", "one dot added with location x: " + p.getX() + ", and y: " + p.getY());
                }
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
