package com.example.bryanchen.formations;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class StartPage extends AppCompatActivity {

    private int GET_MAIN_REQUEST = 15;
    private ArrayList<MainActivity> mains = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

//        TextView startText = (TextView) findViewById(R.id.titlePage);
//        startText.setText("FormLy");
//        startText.setTextSize(60);
//        startText.setGravity(Gravity.CENTER);

        ImageView imageView = (ImageView) findViewById(R.id.titlePage);
        imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.logo));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(StartPage.this, "Creating new formations", Toast.LENGTH_SHORT).show();
                Intent creatingIntent = new Intent(view.getContext(), MainActivity.class);
//                startActivityForResult(creatingIntent, FORMATIONRESULT);
                startActivityForResult(creatingIntent, GET_MAIN_REQUEST);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_MAIN_REQUEST && resultCode == RESULT_OK) {
            Log.e("the result OK", data.toString());
            if (data.getExtras().getParcelable("FINAL") == null) {
                Log.e("WE NULL", "shit");
            } else {
                FragList f = data.getExtras().getParcelable("FINAL");
//            Log.e("NULL SHIT", String.valueOf(data.getExtras().getParcelable("FINAL") != null));
                Log.e("bundlestart page", "" + f.toString());
            }
//                Log.e("startPageFragsB", b.describeContents()+"");
//                Log.e("Number of pages", b.getParcelable("FINAL").toString());

//                f = data.getParcelableExtra("FINAL");
//                Log.e("startPageFrags", data.getParcelableExtra("FINAL").describeContents()+"");
        }

    }

}
