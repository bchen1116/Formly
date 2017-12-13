package com.example.bryanchen.formations;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BryanChen on 11/29/17.
 */

public class FragList implements Parcelable {

    public List<Fragment> slides;
    public List<DotList> fragList = new ArrayList<>();
    public String activityName;
    public int numFrags;

    // initialize a FragList object
    public FragList(String name, ArrayList<Fragment> frags) {
        this.slides = frags;
        for (Fragment d: frags) {
            Slidescreen r = (Slidescreen) d;
            r.updateComments();
            DotList dottie = new DotList(r.getPage(), r.getDots(), r.getComments());
            this.fragList.add(dottie);
        }
        this.activityName = name;
    }

    // used for loading
    public FragList(String name, List<DotList> dotLists, boolean b) {
        this.activityName = name;
        this.fragList = dotLists;
        Log.d("shit", name + " " + dotLists.size());
    }

    public FragList(String name, int numFrags) {
        this.activityName = name;
        this.numFrags = numFrags;
    }

    // initialize a FragList object with a parcel
    protected FragList(Parcel in) {
        this.activityName = in.readString();
        List<DotList> fList = new ArrayList<>();
        in.readTypedList(fList, DotList.CREATOR);
        fragList = fList;
    }

    // returns the activity name of the formations associated
    public String getActivityName() {
        return this.activityName;
    }

    // returns the list of DotLists associated with these formations
    public ArrayList<DotList> getDots() {
        return (ArrayList) this.fragList;
    }

    // toString method to help distinguish these FragLists. Mainly for debugging
    @Override
    public String toString() {
        return String.valueOf(this.activityName) +" " + this.fragList.size();
    }

    // returns the number of slides that this formation group has
    @Override
    public int describeContents() {
        return this.fragList.size();
    }

    // writes a parcel with the necessary information
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.activityName);
        parcel.writeTypedList(this.fragList);
    }

    // necessary CREATOR method
    public static final Parcelable.Creator<FragList> CREATOR = new Parcelable.Creator<FragList>() {
        public FragList createFromParcel(Parcel in) {
            return new FragList(in);
        }

        public FragList[] newArray(int size) {
            return new FragList[size];
        }
    };

}