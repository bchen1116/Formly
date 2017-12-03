package com.example.bryanchen.formations;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.content.Intent;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    List<Dot> dots = null;
    static final int EDIT_DOTS_REQUEST = 12;
    private static final int REQUEST_PERMISSION = 1;
    static final String STORE = "store";
    static final String STORE_FAIL = "fail";
    static final String LOAD = "load";
    static final String DOC_GET = "DOC GET";

    public String className = "hi";

    private int NUM_ITEMS = 0;
    ArrayList<Fragment> fragList = new ArrayList<>();
    android.support.v4.view.ViewPager mViewPager;
    public TabsPagerAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            dots = savedInstanceState.getParcelableArrayList("DOTDOT");
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mViewPager = (android.support.v4.view.ViewPager) findViewById(R.id.pager);
        myAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(myAdapter);

        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.addButton1);
        if (NUM_ITEMS < 1){
            Slidescreen s = new Slidescreen().newInstance(String.valueOf(NUM_ITEMS), NUM_ITEMS);
            addView(s);
            NUM_ITEMS++;
            myAdapter.notifyDataSetChanged();
        }

//        DocumentReference docRef = db.collection("User0")

        
        CollectionReference collectionRef = db.collection("User0").document("Fragment 0").collection("Dots");
        collectionRef.get()
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

                            Slidescreen f = (Slidescreen)myAdapter.getCurrentFrag(mViewPager.getCurrentItem());
                            f.setDots(dots);
                            f.setPage(NUM_ITEMS);
                            myAdapter.notifyDataSetChanged();

                        }
                        else {
                            Log.e("Load Fail", "Error getting documents: ", task.getException());
                        }
                    }
                });

//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
////                        List<Dot> doc_dots = document.toObject(dots.getClass());
////                        GenericTypeIndicator<List<Dot>> t = new GenericTypeIndicator<List<Dot>>() {};
////                        db.collection("frags").get().;
//
////                        Log.d(DOC_GET, "DocumentSnapshot data toObject: " + doc_dots); //task.getResult().getData().get("0").getClass().toString());
////                        for (Object d : task.getResult().getData().values()) {
////                            Log.d(DOC_GET, "maybe a dot: " + d.toString());
////                        }
//                    }
//                    else {
//                        Log.d(DOC_GET, "No such document");
//                    }
//                }
//                else {
//                    Log.d("DOC FAIL", "get failed with ", task.getException());
//                }
//            }
//        });

        // button to add more slides to the activity
        FloatingActionButton fragButton = (FloatingActionButton) findViewById(R.id.button);
        fragButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                addButton.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Adding a new page", Toast.LENGTH_SHORT).show();
                Slidescreen s = new Slidescreen().newInstance(String.valueOf(NUM_ITEMS), NUM_ITEMS);
                s.setDots(dots);
                NUM_ITEMS++;
                addView(s);
                myAdapter.notifyDataSetChanged();
            }
        });

        // button to add people to the screen

        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "Adding people mode", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(v.getContext(), EditViewActivity.class);
                Slidescreen f = (Slidescreen) myAdapter.getCurrentFrag(mViewPager.getCurrentItem());
                dots = f.getDots();

                if (dots != null) {
                    intent.putParcelableArrayListExtra("DOTS", (ArrayList) dots);
                }
                startActivityForResult(intent, EDIT_DOTS_REQUEST);


            }
        });
    }

    public void dotsUpdate(){
        myAdapter.notifyDataSetChanged();
    }

    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_DOTS_REQUEST) {
            if (resultCode == RESULT_OK) {
                dots = data.getParcelableArrayListExtra("DOTS");
                Slidescreen f = (Slidescreen)myAdapter.getCurrentFrag(mViewPager.getCurrentItem());
                f.setDots(dots);

                for (Fragment fr:  myAdapter.getFragment()) {
//                    Log.d("fragment", "another fragment at page " + fr.)
                    Slidescreen frag = (Slidescreen) fr;
                    frag.updateDots(dots);

                    Log.v("FRAG_ID", String.valueOf(frag.getId()));
                    int num = 0;
                    for (Dot d : frag.getDots()) {
                        // Writing to fireStore
                        // Root Collection will later be named to a logged-in user ID or some other identifier
                        String fragID = String.valueOf(num);
                        db.collection("User0").document("Fragment " + fragID).collection("Dots").document(d.getID().toString())
                                .set(d)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(STORE, "DocumentSnapshot successfully written");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(STORE_FAIL, "Error writing document", e);
                                    }
                                });
                        num++;
                    }
                }
                myAdapter.notifyDataSetChanged();

            }

        }
    }

    public void addView(Fragment newPage) {
        int pageIndex = myAdapter.addView(newPage);
        // You might want to make "newPage" the currently displayed page:
        mViewPager.setCurrentItem(pageIndex, true);
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
//        outState.putParcelableArrayList("DOTDOT", allDots);
        super.onSaveInstanceState(outState);
    }

    private class TabsPagerAdapter extends SmartFragmentStatePagerAdapter {
        ArrayList<Fragment> mlist = new ArrayList<>();

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
//            setContentView(R.layout.activity_slide_screen);
        }


        public int addView(Fragment f) {
            return myAdapter.addView(f, mlist.size());
        }

        public int addView(Fragment f, int position) {
            mlist.add(position, f);
            myAdapter.notifyDataSetChanged();
            return position;
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public int getItemPosition(Object obj) {
            Slidescreen o = (Slidescreen) obj;
            for (int i = 0; i < getCount(); i++) {
                if (mlist.get(i).equals(o)) {
                    return i;
                }
            }
            return POSITION_NONE;
        }

        public boolean validPosition(int position) {
            if (position >= 0 && position < mlist.size()){
                return true;
            }
            return false;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            return mlist.get(position);
        }

        public Fragment getCurrentFrag(int position) {
            return mlist.get(position);
        }

        public ArrayList<Fragment> getFragment() { return mlist;}
    }

//    public class FragList implements Parcelable {
//
//        public List<Fragment> slides;
//        public List<DotList> fragList = new ArrayList<>();
//        public String activityName;
//
//        public FragList(String name, List<Fragment> frags) {
//            this.slides = frags;
//            for (Fragment d: frags) {
//                Slidescreen r = (Slidescreen) d;
//                this.fragList.add((r.getDotList()));
//            }
//            this.activityName = name;
//        }
//
//        protected FragList(Parcel in) {
//            this.activityName = in.readString();
//            in.readTypedList(fragList, DotList.CREATOR);
//        }
//
//        @Override
//        public int describeContents() {
//            return 0;
//        }
//
//        @Override
//        public void writeToParcel(Parcel parcel, int i) {
//            parcel.writeString(this.activityName);
//            parcel.writeTypedList(fragList);
//        }
//
//        public final Parcelable.Creator<FragList> CREATOR = new Parcelable.Creator<FragList>() {
//            public FragList createFromParcel(Parcel in) {
//                return new FragList(in);
//            }
//
//            public FragList[] newArray(int size) {
//                return new FragList[size];
//            }
//        };
//
//    }

    @Override
    public void onBackPressed() {
        FragList result = new FragList(this.className, this.myAdapter.getFragment());
        Log.e("onbackpressedfrag", ""+result.fragList.size());
        Intent finishing = new Intent();
//        Bundle b = new Bundle();
//        b.putParcelable("FINAL", result);
        finishing.putExtra("FINAL", result);
        setResult(RESULT_OK, finishing);
        finish();
    }
}
