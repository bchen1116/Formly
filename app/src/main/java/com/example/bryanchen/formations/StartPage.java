package com.example.bryanchen.formations;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

// page that contains all of the different formations
public class StartPage extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public int numLoadedFrags = 0;
    private int GET_MAIN_REQUEST = 15;
    private ArrayList<FragList> mains = new ArrayList<>();
    private RecyclerView recycle;
    private formationsAdapter mAdapter;
    private TextView emptyView;
    String name = "";

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

        // launches the formation clicked from the recyclerView
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
            } else {
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
            Log.e("the result OK", data.toString());
            FragList f = data.getExtras().getParcelable("FINAL");
            for (int i = 0; i < mains.size(); i++) {
                Log.e(mains.get(i).getActivityName(), f.getActivityName());
                if (mains.get(i).getActivityName().equals(f.getActivityName())) {
                    mains.remove(i);
                }
            }
            mains.add(0, f);
        }
        mAdapter.notifyDataSetChanged();

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
}
