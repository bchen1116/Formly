package com.example.bryanchen.formations;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BryanChen on 12/4/17.
 */

public class formationsAdapter extends RecyclerView.Adapter<formationsAdapter.MyViewHolder> {
    private List<FragList> fragLists = new ArrayList<>();
    private String titles;
//    private OnItemClicked mListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, pages;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            pages = (TextView) view.findViewById(R.id.pages);

        }
    }

    private OnItemClicked onClick;
    public interface OnItemClicked {
        void onItemClick(int position);
    }

    public formationsAdapter(OnItemClicked listener, List<FragList> fraglist) {
        this.fragLists = fraglist;
        onClick = listener;
    }

    @Override
    public formationsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.formations, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        FragList frag = fragLists.get(position);
        Log.e("HELLO", ""+position);
        Log.e("HIYAAAA", frag.toString());
        holder.title.setText(frag.getActivityName());
        holder.pages.setText(frag.describeContents() + " pages");
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return fragLists.size();
    }

    public void setOnClick(OnItemClicked onClick)
    {
        this.onClick=onClick;
    }
}
