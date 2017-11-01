package com.example.bryanchen.formations;

import android.view.View;
import android.widget.Button;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<Dot> dots = null;
    static final int EDIT_DOTS_REQUEST = 12;

    private int NUM_ITEMS = 0;
    ArrayList<Fragment> fragList = new ArrayList<>();
    android.support.v4.view.ViewPager mViewPager;
    TabsPagerAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mViewPager = (android.support.v4.view.ViewPager) findViewById(R.id.pager);
        myAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(myAdapter);


        // button to add people to the screen
        ImageButton addButton = (ImageButton) findViewById(R.id.addButton1);

        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (NUM_ITEMS > 0) {
                    Toast.makeText(getApplicationContext(), "Adding people mode", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(v.getContext(), EditViewActivity.class);
                    Slidescreen f = (Slidescreen)myAdapter.getCurrentFrag(mViewPager.getCurrentItem());
                    dots = f.getDots();

                    if (dots != null) {
                        intent.putParcelableArrayListExtra("DOTS", (ArrayList) dots);
                    }
                    startActivityForResult(intent, EDIT_DOTS_REQUEST);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Add a new page first!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // button to add more slides to the activity
        Button fragButton = (Button) findViewById(R.id.button);
        fragButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Adding a new page", Toast.LENGTH_SHORT).show();
                Slidescreen s = new Slidescreen().newInstance(String.valueOf(NUM_ITEMS), NUM_ITEMS);
                s.setDots(dots);
                NUM_ITEMS++;
                addView(s);
                myAdapter.notifyDataSetChanged();
            }
        });
    }


    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_DOTS_REQUEST) {
            if (resultCode == RESULT_OK) {
                dots = data.getParcelableArrayListExtra("DOTS");
                Slidescreen f = (Slidescreen)myAdapter.getCurrentFrag(mViewPager.getCurrentItem());
                f.setDots(dots);
                for (Fragment fr:  myAdapter.getFragment()) {
                    Slidescreen frag = (Slidescreen) fr;
                    frag.updateDots(dots);
                }
                myAdapter.notifyDataSetChanged();

//                Log.v("TAG", "added dots to page " + mViewPager.getCurrentItem());
//                for (Dot p : dots) {
//                    Log.v("NEW", "one dot added with location x: " + p.getX() + ", and y: " + p.getY());
//                }
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
}
