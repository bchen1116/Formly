package com.example.bryanchen.formations;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class Slidescreen extends Fragment {
    private List<Dot> dots = new ArrayList<>();
    public int page;
    public Slidescreen() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_slidescreen, container, false);
        TextView text = (TextView) rootView.findViewById(R.id.textView);
        text.setText(String.valueOf("Slide "+ this.getInt()+1));
        return rootView;
    }

    public Slidescreen newInstance(String key, int p) {
        Slidescreen fragment = new Slidescreen();
        Bundle args = new Bundle();
        args.putInt(key, p);
        fragment.setArguments(args);
        this.page = p;
        return fragment;
    }

    public int getInt() {
        return this.page;
    }
}
