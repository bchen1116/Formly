package com.example.bryanchen.formations;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guillermo on 12/12/2017.
 */

public class StringList implements Parcelable {

    List<String> strings = new ArrayList<>();

    public StringList() {

    }

    public StringList (List<String> list) {
        strings = list;
    }

    protected StringList(Parcel in) {
        strings = in.createStringArrayList();
    }

    @Override
    public int describeContents() { return strings.size(); }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringList(this.strings);
    }

    public static final Parcelable.Creator<StringList> CREATOR = new Parcelable.Creator<StringList>() {
        public StringList createFromParcel(Parcel in) { return new StringList(in); }

        public StringList[] newArray(int size) { return new StringList[size]; }
    };


}
