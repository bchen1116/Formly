package com.example.bryanchen.formations;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// represents a single set of formations
public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    List<Dot> dots = new ArrayList<>();
    static final int EDIT_DOTS_REQUEST = 12;
    static final String STORE = "store";
    static final String STORE_FAIL = "fail";

    public int numLoadedFrags = 0;
    public String className = "";

    private int NUM_ITEMS = 0;
    ArrayList<Fragment> fragList = new ArrayList<>();
    android.support.v4.view.ViewPager mViewPager;
    public TabsPagerAdapter myAdapter;
    private Toolbar mActionBarToolbar;
    private Bundle b = null;
    public TextView pageNumbers;
    private ViewPager.OnPageChangeListener onPageChangeListener;
    private FloatingActionButton deletePage, comments;
    private InputMethodManager imm;

    // Initializes the mainActivity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find the pageNumber textView
        pageNumbers = (TextView) findViewById(R.id.pageNumber);
        deletePage = (FloatingActionButton) findViewById(R.id.removeButton);
        comments = (FloatingActionButton) findViewById(R.id.comments);
        // find if there is a bundle passed through to load
        try {
            b = getIntent().getExtras().getBundle("fragList");
        } catch (Exception e) {

        }
        className = getIntent().getExtras().getString("ActivityName");

        // sets the name at the top of the activity to the ActivityName
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(className);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endSession();
            }
        });

        Intent intent = getIntent();
        numLoadedFrags = intent.getExtras().getInt("numFrags");

        // creates the slides
        mViewPager = (android.support.v4.view.ViewPager) findViewById(R.id.pager);
        myAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(myAdapter);


        // checks if there exists a bundle. Loads data if there is
        if (b != null) {
            className = b.getString("name");
            List<DotList> dlists = b.getParcelableArrayList("dotlists");
            for (int i = 0; i < dlists.size(); i++) {
                Slidescreen s = new Slidescreen().newInstance(String.valueOf(i), i);
                s.setPage(i);
                s.setDots(dlists.get(i).getDots());
                s.setComments(dlists.get(i).getComment());
                addView(s, i);
                NUM_ITEMS++;
            }
            myAdapter.notifyDataSetChanged();
            Slidescreen s = (Slidescreen) myAdapter.getCurrentFrag(0);
            dots = s.getDots();
            mViewPager.setCurrentItem(0, true);

        }
        onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                deletePage.setVisibility(View.GONE);
                pageNumbers.setText(mViewPager.getCurrentItem()+1+" of "+ NUM_ITEMS);

            }

            @Override
            public void onPageSelected(int position) {
                deletePage.setVisibility(View.VISIBLE);
                pageNumbers.setText(mViewPager.getCurrentItem()+1+" of "+ NUM_ITEMS);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                pageNumbers.setText(mViewPager.getCurrentItem()+1+" of "+ NUM_ITEMS);
                Slidescreen d =(Slidescreen) myAdapter.getCurrentFrag(mViewPager.getCurrentItem());
                dots = d.getDots();
                deletePage.setVisibility(View.VISIBLE);
            }
        };

        mViewPager.addOnPageChangeListener(onPageChangeListener);
        // adds a slide if there are no slides on this formation
        if (NUM_ITEMS < 1 || numLoadedFrags < 1 && NUM_ITEMS<1) {
            Slidescreen s = new Slidescreen().newInstance(String.valueOf(NUM_ITEMS), NUM_ITEMS);
            addView(s, 0);
            NUM_ITEMS++;
            onPageChangeListener.onPageSelected(mViewPager.getCurrentItem());
            myAdapter.notifyDataSetChanged();
            deletePage.setVisibility(View.VISIBLE);
        }
        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                onPageChangeListener.onPageSelected(mViewPager.getCurrentItem());
            }
        });

//        DocumentReference docRef = db.collection("User0")

        Log.d("MAIN", "numLoadedFrags: " + numLoadedFrags);
        for (int i = 0; i < numLoadedFrags; i++) {
            final int currentFragNum = i;
            Log.d("FOR", "lesgoooo " + i);
            db.collection("User1").document("Fragments").collection("Fragment " + i)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            dots = new ArrayList<>();
                            for (DocumentSnapshot doc : task.getResult()) {
                                Map<String, Object> newDotData = doc.getData();
                                Dot newDot = new Dot(newDotData);
                                dots.add(newDot);
//                                Log.e("Dot Object",doc.getId() + " => " + doc.getData());
                                }

                                if (currentFragNum == 0) {
                                    Slidescreen f = (Slidescreen) myAdapter.getCurrentFrag(mViewPager.getCurrentItem());
                                    f.setDots(dots);
                                } else {
                                    Slidescreen s = new Slidescreen();
                                    s.setPage(NUM_ITEMS);
                                    s.setDots(dots);

                                    NUM_ITEMS++;
                                    myAdapter.addView(s);
                                }

                                myAdapter.notifyDataSetChanged();
                            } else {
                                Log.e("Load Fail", "Error getting documents: ", task.getException());
                            }
                        }
                    });
                }

        // button to add more slides to the activity
        FloatingActionButton fragButton = (FloatingActionButton) findViewById(R.id.button1);
        fragButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (dots.size() > 0) {
                    Toast.makeText(getApplicationContext(), "Adding a new page", Toast.LENGTH_SHORT).show();
                    int index = mViewPager.getCurrentItem();
                    Slidescreen currentSlide = (Slidescreen) myAdapter.getCurrentFrag(index);
                    Slidescreen s = new Slidescreen();
                    s.setPage(index +1);
                    s.setDots(dots);
                    NUM_ITEMS++;
                    pageNumbers.setText(NUM_ITEMS+" of "+ NUM_ITEMS);
                    addView(s, index+1);
                    onPageChangeListener.onPageSelected(mViewPager.getCurrentItem());
                    myAdapter.notifyDataSetChanged();
                    deletePage.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getApplicationContext(), "Add people first!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // done button to end this activity and return to the start page
        Button doneButton = (Button) findViewById(R.id.button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Exiting", Toast.LENGTH_SHORT).show();
                endSession();
            }
        });

        // handles page deletes
        deletePage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Deleting page", Toast.LENGTH_SHORT);
                NUM_ITEMS--;
                int page =  mViewPager.getCurrentItem();
                myAdapter.destroyItem(mViewPager, page, myAdapter.getCurrentFrag(page));
                myAdapter.notifyDataSetChanged();
                myAdapter.deleteFrag(page);
                if (page > 0) {
                    mViewPager.setCurrentItem(page-1);
                    Slidescreen s = (Slidescreen) myAdapter.getCurrentFrag(page-1);
                    dots = s.getDots();
                } else {
                    try {
                        mViewPager.setCurrentItem(page);
                        Slidescreen s = (Slidescreen) myAdapter.getCurrentFrag(page);
                        dots = s.getDots();
                    } catch (Exception e) {
                        dots.clear();
                        Slidescreen s = new Slidescreen().newInstance(String.valueOf(NUM_ITEMS), NUM_ITEMS);
                        addView(s, 0);
                        NUM_ITEMS++;
                        onPageChangeListener.onPageSelected(mViewPager.getCurrentItem());
                        myAdapter.notifyDataSetChanged();
                    }
                }
                myAdapter.notifyDataSetChanged();
                deletePage.setVisibility(View.VISIBLE);
                for (Fragment f: myAdapter.getFragment()) {
                    Slidescreen s = (Slidescreen) f;
                    int newPage = s.getPage();
                    if (newPage>page) {
                        s.setPage(newPage-1);
                    }
                }
            }
        });

        // handles comments
        comments.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LinearLayout layout = new LinearLayout(MainActivity.this);
                TextView tvMessage = new TextView(MainActivity.this);
                final EditText etInput = new EditText(MainActivity.this);
                int page = mViewPager.getCurrentItem();
                Slidescreen s = (Slidescreen) myAdapter.getCurrentFrag(page);
                String commentary = s.getComments();
                etInput.setText(commentary);
                etInput.requestFocus();
                tvMessage.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                tvMessage.setText("Comments: ");
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(tvMessage);
                layout.addView(etInput);
                layout.setPadding(50, 40, 50, 10);

                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                builder.setView(layout);

                // cancel
                builder.setNegativeButton("Cancel", (dialog, which) -> {
                    imm.hideSoftInputFromWindow(etInput.getWindowToken(), 0);
                    dialog.cancel();
                });

                // if ok is pressed, creates a new formation
                builder.setPositiveButton("Done", (dialog, which) -> {
                    imm.hideSoftInputFromWindow(etInput.getWindowToken(), 0);
                    String com = etInput.getText().toString();
                    s.setComments(com);
                    s.updateComments();
                });
                builder.create().show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        deletePage.setVisibility(View.VISIBLE);
        int fragNumInt = 0;
        for (Fragment fr : myAdapter.getFragment()) {
            Slidescreen frag = (Slidescreen) fr;
            frag.updateDots(dots);

            int dotNumInt = 0;
            for (Dot d : frag.getDots()) {
                // Writing to fireStore
                // Root Collection will later be named to a logged-in user ID or some other identifier
                String fragNum = String.valueOf(fragNumInt);
                db.collection("User1").document("Fragments").collection("Fragment " + fragNum).document(d.getID().toString())
                        .set(d)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
//                                        Log.d(STORE, "DocumentSnapshot successfully written");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(STORE_FAIL, "Error writing document", e);
                            }
                        });
            }
            fragNumInt++;
        }
    }

    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_DOTS_REQUEST) {
            if (resultCode == RESULT_OK) {
                List<Dot> deleted = new ArrayList<>();

                dots = data.getParcelableArrayListExtra("DOTS");
                if (data.getParcelableArrayListExtra("DELETED") != null){
                    deleted = data.getParcelableArrayListExtra("DELETED");
                }

                Slidescreen f = (Slidescreen)myAdapter.getCurrentFrag(mViewPager.getCurrentItem());
                f.setDots(dots);
                int fragNumInt = 0;

                for (Fragment fr:  myAdapter.getFragment()) {
                    Log.d("fragment", "another fragment at page " );
                    Slidescreen frag = (Slidescreen) fr;
                    frag.updateDots(dots);

                    Log.v("FRAG_NUM", String.valueOf(fragNumInt));
                    int dotNumInt = 0;
                    for (Dot d : frag.getDots()) {
                        // Writing to fireStore
                        // Root Collection will later be named to a logged-in user ID or some other identifier
                        String fragNum = String.valueOf(fragNumInt);
                        db.collection("User1").document("Fragments").collection("Fragment " + fragNum).document(d.getID().toString())
                                .set(d)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
//                                        Log.d(STORE, "DocumentSnapshot successfully written");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(STORE_FAIL, "Error writing document", e);
                                    }
                                });
                    }
                    fragNumInt++;
                }
                int i = 0;
                for (Dot d: deleted) {
//                    Log.d("Deleted Dot: ", d.toString());
                    if (d != null) {
                        db.collection("User1").document("Fragments").collection("Fragment " + f.getPage()).document(d.getID().toString())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("Delete Success", "deleted " + d.toString());
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("Delete Fail", "Error deleting document", e);

                                    }
                                });
                    }
                    else {
                        Log.d("NULL DOT", String.valueOf(i));
                    }
                    i++;
                }

                Map<String, Integer> numData = new HashMap<>();
                numData.put("num", fragNumInt);
                db.collection("User1").document("Num Frags")
                    .set(numData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(STORE, "Stored the number of total fragments");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(STORE_FAIL, "Error writing num", e);
                        }
                    });

                myAdapter.notifyDataSetChanged();

            }

        }
    }


    // adds a new slide to the viewPager
    public void addView(Fragment newPage, int index) {
        if (index < NUM_ITEMS) {
            for (Fragment f: myAdapter.getFragment()) {
            Slidescreen s = (Slidescreen) f;
            int page = s.getPage();
            if (page >= index) {
                s.setPage(page+1);
            }
        }
            myAdapter.addView(newPage, index);

        } else {
            myAdapter.addView(newPage);
        }
        // You might want to make "newPage" the currently displayed page:
        mViewPager.setCurrentItem(index, false);
        myAdapter.notifyDataSetChanged();
    }

//    // auto-generated method
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    // necessary method
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return false;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    // returns the list of dots
    public List<Dot> getDots() {
        return dots;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        ArrayList<List> allDots = new ArrayList<>();
        for (Fragment i: myAdapter.getFragment()) {
            Slidescreen newI = (Slidescreen) i;
            allDots.add(newI.getDots());
        }
        super.onSaveInstanceState(outState);
    }

    private class TabsPagerAdapter extends SmartFragmentStatePagerAdapter implements View.OnTouchListener {
        ArrayList<Fragment> mlist = new ArrayList<>();

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        public int addView(Fragment f) {
            return myAdapter.addView(f, mlist.size());
        }

        public int addView(Fragment f, int position) {
            mlist.add(position, f);
            myAdapter.notifyDataSetChanged();
            return position;
        }

        public boolean onTouch(View v, MotionEvent e) {
            return true;
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // gets the position for the object
        @Override
        public int getItemPosition(Object obj) {
            return POSITION_NONE;
        }

        // checks if the position is valid
        public boolean validPosition(int position) {
            if (position >= 0 && position < mlist.size()){
                return true;
            }
            return false;
        }

        // deletes the page
        public void deleteFrag(int position) {
            mlist.remove(position);
        }
        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            return mlist.get(position);
        }

        public Fragment getCurrentFrag(int position) {
            pageNumbers.setText(position+1 + " of "+ NUM_ITEMS);
            return mlist.get(position);
        }

        public ArrayList<Fragment> getFragment() { return mlist;}
    }

    // ends the MainActivity session
    public void endSession() {
        FragList result = new FragList(this.className, this.myAdapter.getFragment());
        Intent finishing = new Intent();
        finishing.putExtra("FINAL", result);
        finishing.putExtra("Formation Name", this.className);
        finishing.putExtra("Num Frags", this.myAdapter.getCount());
        finishing.putParcelableArrayListExtra("Dots", result.getDots());
        setResult(RESULT_OK, finishing);
        finish();
    }

    // ends session if user presses back button
    @Override
    public void onBackPressed() {
        endSession();
    }
}
