package com.example.bryanchen.formations;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class StartPage extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public int numLoadedFrags = 0;
    private int FORMATIONRESULT = 15;
    private int GET_MAIN_REQUEST = 15;
    private ArrayList<MainActivity> mains = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        db.collection("User1").document("Num Frags")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("LOAD", "DocumentSnapshot data: Number of Fragments: " + task.getResult().getData());
                                Long loadedNum = (Long) document.getData().get("num");

                                Thread t = new Thread() {
                                    public void run() {
                                        updateNumFrags(loadedNum.intValue());
                                    }
                                };

                                t.start();
                                try
                                {
                                    t.join();
                                }catch(InterruptedException ie)
                                {
                                    //Log message if required.
                                }
                            } else {
                                Log.d("ERROR", "No such document");
                            }
                        } else {
                            Log.d("FAIL", "get failed with ", task.getException());
                        }
                    }
                });

        TextView startText = (TextView) findViewById(R.id.titlePage);
        startText.setText("FormLy");
        startText.setTextSize(60);
        startText.setGravity(Gravity.CENTER);
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

//                Bundle bundle = new Bundle();
//                bundle.putInt("numFrags", numLoadedFrags);
//                creatingIntent.putExtras(bundle);
                creatingIntent.putExtra("numFrags", numLoadedFrags);
                startActivity(creatingIntent);
                startActivityForResult(creatingIntent, GET_MAIN_REQUEST);
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
