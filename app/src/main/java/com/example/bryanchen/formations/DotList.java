package com.example.bryanchen.formations;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by BryanChen on 11/29/17.
 */

public class DotList implements Parcelable {
        private List<Dot> dotList=new ArrayList<Dot>();
        private int pageNumber;
        private String comment = "";

        // empty constructor
        public DotList() {
        }

        // initializes a DotList obj
        public DotList(int num, List<Dot> dots, String edit) {
            this.dotList = dots;
            this.pageNumber = num;
            this.comment = edit;
        }

        // creates a DotList obj from a parcel
        protected DotList(Parcel in) {
            this.pageNumber = Integer.parseInt(in.readString());
            in.readTypedList(dotList, Dot.CREATOR);
            this.comment = in.readString();
        }

        // toString method
        @Override
        public String toString() {
            return String.valueOf(this.pageNumber) +" " + this.dotList.size();
        }

        // returns the list of Dots in this slide
        public List<Dot> getDots() {
            return this.dotList;
        }

        // returns the comments associated with this slide
        public String getComment() {
            return this.comment;
        }

        // returns size of DotList
        @Override
        public int describeContents() {
            return dotList.size();
        }

        // writes into the DotList obj parcel
        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(String.valueOf(this.pageNumber));
            parcel.writeTypedList(this.dotList);
            parcel.writeString(this.comment);
        }

        // necessary CREATOR method
        public static final Parcelable.Creator<DotList> CREATOR = new Parcelable.Creator<DotList>() {
            public DotList createFromParcel(Parcel in) {
                return new DotList(in);
            }

            public DotList[] newArray(int size) {
                return new DotList[size];
            }
        };


    }

