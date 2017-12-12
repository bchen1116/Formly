package com.example.bryanchen.formations;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

// page that contains all of the different formations
public class StartPage extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();


    public int numLoadedFrags = 0;
    private int GET_MAIN_REQUEST = 15;
    private ArrayList<FragList> mains = new ArrayList<>();
    private RecyclerView recycle;
    private formationsAdapter mAdapter;
    private TextView emptyView;
    String name = "";
    private final int CAMERACODE = 5;

    // creates the StartPage
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
                            try {
                                t.join();
                            } catch(InterruptedException ie) {
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
        
        // add button to add a new formation
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAndDisplayDialog();
            }
        });
        recycle = (RecyclerView) findViewById(R.id.recycler);
        emptyView = (TextView) findViewById(R.id.empty_view);

        // brings up camera to scan
        FloatingActionButton camera = (FloatingActionButton) findViewById(R.id.camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(StartPage.this, Camera.class);
                startActivityForResult(cameraIntent, CAMERACODE);
            }
        });

        // launches the formation clicked from the recyclerView
        mAdapter = new formationsAdapter(new formationsAdapter.OnItemClicked() {
            @Override
            public void onItemClick(int position) {
                Bundle b = new Bundle();
                FragList current = mains.get(position);
                b.putString("name", current.getActivityName());
                b.putParcelableArrayList("dotlists", current.getDots());
                Intent mainIntent = new Intent(getApplication(), MainActivity.class);
                mainIntent.putExtra("fragList", b);
                mainIntent.putExtra("ActivityName", current.getActivityName());
                startActivityForResult(mainIntent, GET_MAIN_REQUEST);
            }
        }, mains);
        if (mains.isEmpty()) {
            recycle.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            recycle.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recycle.getBackground().setAlpha(25);
        recycle.setLayoutManager(mLayoutManager);
        recycle.setItemAnimator(new DefaultItemAnimator());
        recycle.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recycle.setAdapter(mAdapter);
    }

    // updates the number of loaded frags
    public void updateNumFrags(int num) {
        numLoadedFrags = num;
    }

    // creates a dialog and allows activity to launch
    private void createAndDisplayDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        TextView tvMessage = new TextView(this);
        final EditText etInput = new EditText(this);

        tvMessage.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        tvMessage.setText("Formation name: ");
        etInput.setSingleLine();
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(tvMessage);
        layout.addView(etInput);
        layout.setPadding(50, 40, 50, 10);

        builder.setView(layout);

        // cancel
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
        });

        // if ok is pressed, creates a new formation
        builder.setPositiveButton("Done", (dialog, which) -> {
            name = etInput.getText().toString();
            if (!hasName(name)) {
                Toast.makeText(getApplicationContext(), "This formation name already exists!", Toast.LENGTH_SHORT).show();
            } else if(name.length() == 0) {
                Toast.makeText(getApplicationContext(), "Needs a name!", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(StartPage.this, "Creating new formations", Toast.LENGTH_SHORT).show();
                Intent creatingIntent = new Intent(getApplicationContext(), MainActivity.class);
                creatingIntent.putExtra("ActivityName", name);
                creatingIntent.putExtra("numFrags", numLoadedFrags);
                startActivityForResult(creatingIntent, GET_MAIN_REQUEST);
            }
        });

        builder.create().show();
    }

    // adds the formation if it's new, otherwise updates it
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        recycle.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        if (requestCode == GET_MAIN_REQUEST && resultCode == RESULT_OK) {
            FragList f = data.getExtras().getParcelable("FINAL");


            String formationName = data.getExtras().getString("Formation Name");
            int numSlides = data.getExtras().getInt("Num Frags");
            Map<String, Integer> numSlidesMap = new HashMap<>();
            numSlidesMap.put("num", numSlides);

            db.collection("Users").document(auth.getUid())
                    .collection(formationName + " " + auth.getUid())
                    .document("Num Frags")
                    .set(numSlidesMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("num frags", "" + numSlidesMap.get("num"));
                        }
                    });

            for (int i = 0; i < mains.size(); i++) {
                if (mains.get(i).getActivityName().equals(f.getActivityName())) {
                    mains.remove(i);
                }
            }

            List<DotList> allDots = data.getParcelableArrayListExtra("Dots");
            for (int i = 0; i < numSlides; i++) {
                DotList slideDots = allDots.get(i);
                for (int dot = 0; dot < slideDots.getDots().size(); dot++) {
                    Dot currentDot = slideDots.getDots().get(dot);
                    db.collection("Users").document(auth.getUid()).collection(formationName + " " + auth.getUid())
                            .document("Fragments").collection("Fragment " + i)
                            .document("" + currentDot.getID())
                            .set(currentDot)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            });

                }


            }


            mains.add(0, f);
            mAdapter.notifyDataSetChanged();
        } else if (requestCode == CAMERACODE && resultCode == RESULT_OK) {
            Log.e("WE COOL", data.getExtras().getString("work"));
        }

    }

    // checks if the new name exists in the current list of formations
    private boolean hasName(String name) {
        for (int i = 0; i<mains.size(); i++) {
            if (mains.get(i).getActivityName().equals(name)){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().signOut();
    }

//    public void setQR() {
//        QRshow.setImageDrawable(getResources().getDrawable(R.drawable.ic_logo));
//        QRshow.setVisibility(View.VISIBLE);
//    }
}
