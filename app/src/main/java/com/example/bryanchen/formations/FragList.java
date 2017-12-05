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

    public FragList(String name, ArrayList<Fragment> frags) {
        this.slides = frags;
        Log.e("fragss", slides.size()+"");
        for (Fragment d: frags) {
            Slidescreen r = (Slidescreen) d;
            r.updateComments();
            DotList dottie = new DotList(r.getPage(), r.getDots(), r.getComments());
            this.fragList.add(dottie);
        }
        this.activityName = name;
    }

    protected FragList(Parcel in) {
        this.activityName = in.readString();
        List<DotList> fList = new ArrayList<>();
        in.readTypedList(fList, DotList.CREATOR);
        fragList = fList;
    }

    public String getActivityName() {
        return this.activityName;
    }

    public ArrayList<DotList> getDots() {
        return (ArrayList) this.fragList;
    }
    @Override
    public String toString() {
        return String.valueOf(this.activityName) +" " + this.fragList.size();
    }

    @Override
    public int describeContents() {
        return this.fragList.size();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.activityName);
        parcel.writeTypedList(this.fragList);
    }

    public static final Parcelable.Creator<FragList> CREATOR = new Parcelable.Creator<FragList>() {
        public FragList createFromParcel(Parcel in) {
            return new FragList(in);
        }

        public FragList[] newArray(int size) {
            return new FragList[size];
        }
    };

}