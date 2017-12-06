package com.example.bryanchen.formations;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

// represents a single set of formations
public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    List<Dot> dots = new ArrayList<>();
    static final int EDIT_DOTS_REQUEST = 12;
    private static final int REQUEST_PERMISSION = 1;
    static final String STORE = "store";
    static final String STORE_FAIL = "fail";
    static final String LOAD = "load";
    static final String DOC_GET = "DOC GET";

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

    // Initializes the mainActivity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find the pageNumber textView
        pageNumbers = (TextView) findViewById(R.id.pageNumber);

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

        Intent intent = getIntent();
        numLoadedFrags = intent.getExtras().getInt("numFrags");

        // creates the slides
        mViewPager = (android.support.v4.view.ViewPager) findViewById(R.id.pager);
        myAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(myAdapter);

//        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Log.e("onPageScrolled" , " "+ position);
//                pageNumbers.setText(mViewPager.getCurrentItem()+" of "+ NUM_ITEMS);
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                Log.e("onPageSelected" , " "+ position);
//
//                pageNumbers.setText(mViewPager.getCurrentItem()+" of "+ NUM_ITEMS);
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//                Log.e("onPageSSC" , " "+ state);
//                pageNumbers.setText(mViewPager.getCurrentItem()+" of "+ NUM_ITEMS);
//
//            }
//        });
//        mViewPager.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                switch (motionEvent.getAction()) {
//
////                    case ACTION_DOWN:
////                        pageNumbers.setText(mViewPager.getCurrentItem()+" of "+ NUM_ITEMS);
////                    case ACTION_UP:
////                        pageNumbers.setText(mViewPager.getCurrentItem()+" of "+ NUM_ITEMS);
//                    default:
//                        Log.e("Are we touching" , "yes we are");
//                        pageNumbers.setText(mViewPager.getCurrentItem()+" of "+ NUM_ITEMS);
//                }
//                return true;
//            }
//        });

        // checks if there exists a bundle. Loads data if there is
        if (b != null) {
            savedInstanceState = b;
            className = b.getString("name");
            List<DotList> dlists = b.getParcelableArrayList("dotlists");
            for (int i = 0; i < dlists.size(); i++) {
                Log.e("PAGES SUP", " " + i);
                Slidescreen s = new Slidescreen().newInstance(String.valueOf(i), i);
                s.setPage(i);
                s.setDots(dlists.get(i).getDots());
                s.setComments(dlists.get(i).getComment());
                addView(s);
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
                Log.e("onPageScrolled" , " "+ position);
                pageNumbers.setText(mViewPager.getCurrentItem()+" of "+ NUM_ITEMS);

            }

            @Override
            public void onPageSelected(int position) {
                Log.e("onPageSelected" , " "+ position);

                pageNumbers.setText(mViewPager.getCurrentItem()+" of "+ NUM_ITEMS);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.e("onPageSSC" , " "+ state);
                pageNumbers.setText(mViewPager.getCurrentItem()+" of "+ NUM_ITEMS);

            }
        };

        mViewPager.addOnPageChangeListener(onPageChangeListener);
        // adds a slide if there are no slides on this formation
        if (NUM_ITEMS < 1) {
            Slidescreen s = new Slidescreen().newInstance(String.valueOf(NUM_ITEMS), NUM_ITEMS);
            addView(s);
            NUM_ITEMS++;
            onPageChangeListener.onPageSelected(mViewPager.getCurrentItem());
            myAdapter.notifyDataSetChanged();
        }
        mViewPager.post(new Runnable()
        {
            @Override
            public void run()
            {
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
                                    Slidescreen s = new Slidescreen().newInstance(String.valueOf(NUM_ITEMS), NUM_ITEMS);
                                    s.setDots(dots);

                                    NUM_ITEMS++;
                                    myAdapter.addView(s, currentFragNum);
                                }

                                myAdapter.notifyDataSetChanged();
                            } else {
                                Log.e("Load Fail", "Error getting documents: ", task.getException());
                            }
                        }
                    });
                }



//        CollectionReference collectionRef = db.collection("User1").document("Fragment 0").collection("Dots");
//        collectionRef.get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            dots = new ArrayList<>();
//                            for (DocumentSnapshot doc : task.getResult()) {
//                                Map<String, Object> newDotData = doc.getData();
//                                Dot newDot = new Dot(newDotData);
//                                dots.add(newDot);
//
////                                Log.e("Dot Object",doc.getId() + " => " + doc.getData());
//                            }
//
//                            Slidescreen f = (Slidescreen)myAdapter.getCurrentFrag(mViewPager.getCurrentItem());
//                            f.setDots(dots);
//                            f.setPage(NUM_ITEMS);
//                            myAdapter.notifyDataSetChanged();
//
//                        }
//                        else {
//                            Log.e("Load Fail", "Error getting documents: ", task.getException());
//                        }
//                    }
//                });


        // button to add more slides to the activity
        FloatingActionButton fragButton = (FloatingActionButton) findViewById(R.id.button1);
        fragButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (dots.size() > 0) {
                    Toast.makeText(getApplicationContext(), "Adding a new page", Toast.LENGTH_SHORT).show();
                    Slidescreen s = new Slidescreen().newInstance(String.valueOf(NUM_ITEMS), NUM_ITEMS);
                    s.setDots(dots);
                    NUM_ITEMS++;
                    pageNumbers.setText(NUM_ITEMS+" of "+ NUM_ITEMS);
                    addView(s);
                    onPageChangeListener.onPageSelected(mViewPager.getCurrentItem());

                    myAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "Add people first!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // done button to end this activity and return to the start page
        Button doneButton = (Button) findViewById(R.id.button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "Adding people mode", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(v.getContext(), EditViewActivity.class);
                Slidescreen f = (Slidescreen) myAdapter.getCurrentFrag(mViewPager.getCurrentItem());
                dots = f.getDots();

                if (dots != null) {
                    intent.putParcelableArrayListExtra("DOTS", (ArrayList) dots);
                }
//                intent.putExtra("fragNum", f.) TODO
                startActivityForResult(intent, EDIT_DOTS_REQUEST);


                endSession();
            }
        });

    }

    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_DOTS_REQUEST) {
            if (resultCode == RESULT_OK) {
                dots = data.getParcelableArrayListExtra("DOTS");
                Log.d("dotList", dots.toString());
                Slidescreen f = (Slidescreen)myAdapter.getCurrentFrag(mViewPager.getCurrentItem());
                f.setDots(dots);
                Log.d("fragDotList", f.getDots().toString());
                int fragNumInt = 0;

//                db.collection("User1").document("Fragments").delete();
//                deleteCollection()

//                db.collection("User1").document("Fragments").collection()

//                db.collection("User1").document("Fragments")
//                        .delete()
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                Log.d("BYE", "Document snapshot successfully deleted");
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.w("BAD BYE", "Error deleting document", e);
//                            }
//                        });
                for (Fragment fr:  myAdapter.getFragment()) {
//                    Log.d("fragment", "another fragment at page " + fr.)
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
                                        Log.d(STORE, "DocumentSnapshot successfully written");
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

    /**
     * Delete all documents in a collection. Uses an Executor to perform work on a background
     * thread. This does *not* automatically discover and delete subcollections.
     */
    private Task<Void> deleteCollection(final CollectionReference collection,
                                        final int batchSize,
                                        Executor executor) {

        // Perform the delete operation on the provided Executor, which allows us to use
        // simpler synchronous logic without blocking the main thread.
        return Tasks.call(executor, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                // Get the first batch of documents in the collection
                Query query = collection.orderBy(FieldPath.documentId()).limit(batchSize);

                // Get a list of deleted documents
                List<DocumentSnapshot> deleted = deleteQueryBatch(query);

                // While the deleted documents in the last batch indicate that there
                // may still be more documents in the collection, page down to the
                // next batch and delete again
                while (deleted.size() >= batchSize) {
                    // Move the query cursor to start after the last doc in the batch
                    DocumentSnapshot last = deleted.get(deleted.size() - 1);
                    query = collection.orderBy(FieldPath.documentId())
                            .startAfter(last.getId())
                            .limit(batchSize);

                    deleted = deleteQueryBatch(query);
                }

                return null;
            }
        });

    }

    /**
     * Delete all results from a query in a single WriteBatch. Must be run on a worker thread
     * to avoid blocking/crashing the main thread.
     */
    @WorkerThread
    private List<DocumentSnapshot> deleteQueryBatch(final Query query) throws Exception {
        QuerySnapshot querySnapshot = Tasks.await(query.get());

        WriteBatch batch = query.getFirestore().batch();
        for (DocumentSnapshot snapshot : querySnapshot) {
            batch.delete(snapshot.getReference());
        }
        Tasks.await(batch.commit());

        return querySnapshot.getDocuments();
    }

    // adds a new slide to the viewPager
    public void addView(Fragment newPage) {
        int pageIndex = myAdapter.addView(newPage);
        // You might want to make "newPage" the currently displayed page:
        mViewPager.setCurrentItem(pageIndex, true);
    }

    // auto-generated method
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // necessary method
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
//        outState.putParcelableArrayList("DOTDOT", allDots);
        super.onSaveInstanceState(outState);
    }

    private class TabsPagerAdapter extends SmartFragmentStatePagerAdapter implements View.OnTouchListener {
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

        public boolean onTouch(View v, MotionEvent e) {
            Log.e("did we touch",e.toString());
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
            Slidescreen o = (Slidescreen) obj;
            for (int i = 0; i < getCount(); i++) {
                if (mlist.get(i).equals(o)) {
                    return i;
                }
            }
            return POSITION_NONE;
        }

        // checks if the position is valid
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
        setResult(RESULT_OK, finishing);
        finish();
    }

    // ends session if user presses back button
    @Override
    public void onBackPressed() {
        endSession();
    }
}
