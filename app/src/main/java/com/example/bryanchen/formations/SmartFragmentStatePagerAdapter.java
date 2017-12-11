package com.example.bryanchen.formations;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by BryanChen on 10/16/17.
 */

/*
   Extension of FragmentStatePagerAdapter which intelligently caches
   all active fragments and manages the fragment lifecycles.
   Usage involves extending from SmartFragmentStatePagerAdapter as you would any other PagerAdapter.
*/
public abstract class SmartFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
    // Sparse array to keep track of registered fragments in memory
    private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
//    private Map<Integer, String> fragTags = new HashMap<>();
//    private Map<String, Fragment> taggedFrags = new HashMap<>();

    public SmartFragmentStatePagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    // Register the fragment when the item is instantiated
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        String tag = fragment.getTag();
//        fragTags.put(position, tag);
//        taggedFrags.put(tag, fragment);
        registeredFragments.put(position, fragment);
        return fragment;
    }

//    // get the fragment associated with the tag
//    public Fragment getFragment(int position) {
//        String tag = fragTags.get(position);
//        if (tag == null) {
//            return null;
//        }
//        return taggedFrags.get(tag);
//    }

    // Unregister when the item is inactive
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    // Returns the fragment for the position (if instantiated)
    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}
