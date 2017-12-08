package com.example.bryanchen.formations;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BryanChen on 12/4/17.
 */

// formation entries in recyclerView
public class formationsAdapter extends RecyclerView.Adapter<formationsAdapter.MyViewHolder> {
    private List<FragList> fragLists = new ArrayList<>();
    private String titles;
    private ImageView getQR;
    private final String p = " pages";
    private Context c;

//    private OnItemClicked mListener;

    // class for entry
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, pages;
        public ImageView getQR;

        // initializes the entry
        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            pages = (TextView) view.findViewById(R.id.pages);
            getQR = (ImageView) view.findViewById(R.id.QRbutton);
        }
    }

    // checking for item clicking
    private OnItemClicked onClick;
    public interface OnItemClicked {
        void onItemClick(int position);
    }

    // initializes the entry
    public formationsAdapter(OnItemClicked listener, List<FragList> fraglist) {
        this.fragLists = fraglist;
        onClick = listener;
    }

    // creates the view for the entry
    @Override
    public formationsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.formations, parent, false);
        c = parent.getContext();
        return new MyViewHolder(itemView);
    }

    // sets the view holder
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        FragList frag = fragLists.get(position);
        holder.title.setText(frag.getActivityName());
        holder.pages.setText(String.valueOf(frag.describeContents()));
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(position);
            }
        });
        holder.getQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("CLICKED", "YAY");
                ImageView image = new ImageView(v.getContext());
                ImageView org = (ImageView) holder.getQR;
                // Copy drawable of that image
                image.setImageDrawable(org.getDrawable());
                // Copy Background of that image
                image.setBackground(org.getBackground());

                AlertDialog.Builder builder =
                        new AlertDialog.Builder(v.getContext()).
                                setMessage("QR code").
                                setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).
                                setView(image);
                builder.create().show();
            }
        });

    }

    // returns the size of the list of formations
    @Override
    public int getItemCount() {
        return fragLists.size();
    }

    // sets the onclick for the entry
    public void setOnClick(OnItemClicked onClick)
    {
        this.onClick=onClick;
    }
}
