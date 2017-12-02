package com.example.bryanchen.formations;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.List;

/**
 * Created by BryanChen on 11/29/17.
 */

public class DotList implements Parcelable {
        private List<Dot> dotList;
        private int pageNumber;

        public DotList() {
            //empty constructor
        }
        public DotList(int num, List<Dot> dots) {
            this.dotList = dots;
            this.pageNumber = num;
        }

        protected DotList(Parcel in) {
            this.pageNumber = Integer.parseInt(in.readString());
            in.readTypedList(dotList, Dot.CREATOR);
            Log.e("dotList valid?", dotList.size()+"");
        }

        @Override
        public String toString() {
            return String.valueOf(this.pageNumber) +" " + this.dotList.size();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(String.valueOf(this.pageNumber));
            parcel.writeTypedList(this.dotList);
        }

        public static final Parcelable.Creator<DotList> CREATOR = new Parcelable.Creator<DotList>() {
            public DotList createFromParcel(Parcel in) {
                return new DotList(in);
            }

            public DotList[] newArray(int size) {
                return new DotList[size];
            }
        };


    }

