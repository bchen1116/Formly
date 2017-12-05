package com.example.bryanchen.formations;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Dialog_edit extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String name;

    public Dialog_edit() {
//        this.onCreate(new Bundle());
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment Dialog_edit.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static Dialog_edit newInstance(String param1, String param2) {
//        Dialog_edit fragment = new Dialog_edit();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Activity activity = getActivity();
//        activity.setContentView(R.layout.fragment_dialog_edit);
        LinearLayout l = new LinearLayout(getContext());
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_dialog_edit, null);
        l.addView(v);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this.getContext());
        EditText input = (EditText) v.findViewById(R.id.new_name);
//        l.addView(input);
        alert.setView(l);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString().trim();
                setName(value);
                Toast.makeText(getContext(), value, Toast.LENGTH_SHORT).show();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        alert.show();
    }

    public void setName(String in) {
        this.name = in;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dialog_edit, container, false);
    }

}
