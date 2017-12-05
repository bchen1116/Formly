package com.example.bryanchen.formations;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static java.security.AccessController.getContext;

public class StartPage extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public int numLoadedFrags = 0;
    private int GET_MAIN_REQUEST = 15;
    private ArrayList<FragList> mains = new ArrayList<>();
    private RecyclerView recycle;
    private formationsAdapter mAdapter;
    String name = "";

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
                                } catch (InterruptedException ie) {
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

        ImageView imageView = (ImageView) findViewById(R.id.titlePage);
        imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.logo));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAndDisplayDialog();
            }
        });
        recycle = (RecyclerView) findViewById(R.id.recycler);

        mAdapter = new formationsAdapter(new formationsAdapter.OnItemClicked() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(getApplicationContext(), "Position " + position + " clicked", Toast.LENGTH_SHORT).show();
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
//        mAdapter.setOnClick(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recycle.setLayoutManager(mLayoutManager);
        recycle.setItemAnimator(new DefaultItemAnimator());
        recycle.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recycle.setAdapter(mAdapter);
    }

    public void updateNumFrags(int num) {
        numLoadedFrags = num;
    }

    private void createAndDisplayDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        TextView tvMessage = new TextView(this);
        final EditText etInput = new EditText(this);

        tvMessage.setText("Formation name: ");
        etInput.setSingleLine();
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(tvMessage);
        layout.addView(etInput);
        layout.setPadding(50, 40, 50, 10);

        builder.setView(layout);

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            Toast.makeText(this, "Cancel clicked", Toast.LENGTH_SHORT).show();
            dialog.cancel();
        });

        builder.setPositiveButton("Done", (dialog, which) -> {
            name = etInput.getText().toString();
            if (!hasName(name)) {
                Toast.makeText(getApplicationContext(), "This formation name already exists!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(StartPage.this, "Creating new formations", Toast.LENGTH_SHORT).show();
                Intent creatingIntent = new Intent(getApplicationContext(), MainActivity.class);
                creatingIntent.putExtra("ActivityName", name);
                startActivityForResult(creatingIntent, GET_MAIN_REQUEST);
            }
        });

        builder.create().show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_MAIN_REQUEST && resultCode == RESULT_OK) {
            Boolean added = false;
            Log.e("the result OK", data.toString());
            FragList f = data.getExtras().getParcelable("FINAL");
            for (int i = 0; i < mains.size(); i++) {
                Log.e(mains.get(i).getActivityName(), f.getActivityName());
                if (mains.get(i).getActivityName().equals(f.getActivityName())) {
                    mains.set(i, f);
                    added = true;
                }
            }
            if (!added) {
                mains.add(f);
            }
        }
        mAdapter.notifyDataSetChanged();

    }

    private boolean hasName(String name) {
        for (int i = 0; i<mains.size(); i++) {
            if (mains.get(i).getActivityName().equals(name)){
                return false;
            }
        }
        return true;
    }
}
