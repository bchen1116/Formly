package com.example.bryanchen.formations;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class StartPage extends AppCompatActivity {

    private int FORMATIONRESULT = 15;
    private ArrayList<MainActivity> mains = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        TextView startText = (TextView) findViewById(R.id.titlePage);
        startText.setText("FormLy");
        startText.setTextSize(60);
        startText.setGravity(Gravity.CENTER);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(StartPage.this, "Creating new formations", Toast.LENGTH_SHORT).show();
                Intent creatingIntent = new Intent(view.getContext(), MainActivity.class);
//                startActivityForResult(creatingIntent, FORMATIONRESULT);
                startActivity(creatingIntent);
            }
        });
    }

//    public void onActivityResult (int requestCode, int resultCode, Intent data) {
//        if (requestCode == FORMATIONRESULT) {
//            if (resultCode == RESULT_OK) {
//                mains.add(data.getParcelableArrayListExtra("DOTS"));
//                Slidescreen f = (Slidescreen)myAdapter.getCurrentFrag(mViewPager.getCurrentItem());
//                f.setDots(dots);
//                for (Fragment fr:  myAdapter.getFragment()) {
//                    Slidescreen frag = (Slidescreen) fr;
//                    frag.updateDots(dots);
//                }
//                myAdapter.notifyDataSetChanged();
//
//            }
//
//        }
//    }

}
