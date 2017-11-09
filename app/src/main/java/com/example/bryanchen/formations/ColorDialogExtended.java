package com.example.bryanchen.formations;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.savvisingh.colorpickerdialog.ColorPickerDialog;
import com.savvisingh.colorpickerdialog.ColorPickerPalette;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by BryanChen on 11/1/17.
 */

public class ColorDialogExtended extends ColorPickerDialog{
    public static final int SIZE_LARGE = 1;
    public static final int SIZE_SMALL = 2;

    public static final int SELECTION_SINGLE = 1;

    protected AlertDialog mAlertDialog;

    protected static final String KEY_TITLE_ID = "Edit Member";
    protected static final String KEY_COLORS = "colors";
    protected static final String KEY_SELECTED_COLOR = "selected_color";
    protected static final String KEY_COLUMNS = "columns";
    protected static final String KEY_SIZE = "size";
    protected static final String KEY_SELECTION_TYPE = "selection_type";



    protected String mTitleResId = "Edit Member";
    protected ArrayList<Integer> mColors = null;
    protected ArrayList<Integer> mSelectedColor;
    protected int mColumns;
    protected int mSize;
    protected String name;
    protected int color;

    public HashMap<String, Integer> result = new HashMap<>();

    private ColorPickerPalette mPalette;
    private ProgressBar mProgress;

    protected OnDialogButtonListener mListener;

    private int mSelectionType;

    public ColorDialogExtended() {
        // Empty constructor required for dialog fragments.
    }

    public static ColorDialogExtended newInstance(int mSelectionType, ArrayList<Integer> colors,
                                                int columns, int size, String name, int color) {

        ColorDialogExtended ret = new ColorDialogExtended();
        ret.initialize(mSelectionType, colors, columns, size, name, color);
        return ret;
    }

    public void initialize(int selectionType, ArrayList<Integer> colors, int columns, int size, String name, int color) {

        mSelectedColor = new ArrayList<>();
        setArguments(selectionType, columns, size);
        setColors(colors);
        this.name = name;
        this.color = color;

    }

    public void setArguments(int selectionType, int columns, int size) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_SELECTION_TYPE, selectionType);
        bundle.putInt(KEY_COLUMNS, columns);
        bundle.putInt(KEY_SIZE, size);
        setArguments(bundle);
    }

    public void setOnDialodButtonListener(OnDialogButtonListener listener) {
        mListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mSelectionType = getArguments().getInt(KEY_SELECTION_TYPE);
            mColumns = getArguments().getInt(KEY_COLUMNS);
            mSize = getArguments().getInt(KEY_SIZE);
        }

        if (savedInstanceState != null) {
            mColors = savedInstanceState.getIntegerArrayList(KEY_COLORS);
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity activity = getActivity();
        final EditText input = new EditText(getActivity());
        if (this.name != "") {
            input.setText(this.name);
        } else {
            input.setHint("New Name");
        }

        LinearLayout l = new LinearLayout(getActivity());
        l.setOrientation(LinearLayout.VERTICAL);
        l.setGravity(Gravity.CENTER);

        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        l.addView(input);
        View view = LayoutInflater.from(getActivity()).inflate(com.savvisingh.colorpickerdialog.R.layout.color_picker_dialog, null);
        l.addView(view);
        mProgress = (ProgressBar) view.findViewById(android.R.id.progress);
        mPalette = (ColorPickerPalette) view.findViewById(com.savvisingh.colorpickerdialog.R.id.color_picker);
        mPalette.init(mSize, mColumns, this);

        if (mColors != null) {
            showPaletteView();
        }

        mAlertDialog = new AlertDialog.Builder(activity)
                .setTitle(mTitleResId)
                .setView(l)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (input.getText().toString() != getName()) {
                            setName(input.getText().toString());
                        }
                        if(mListener!=null) {
                            if (getName() != null){
                                if (mSelectedColor.size()>0) {
                                    result.put(getName(), mSelectedColor.get(0));
                                } else {
                                    result.put(getName(), getColor());
                                }
                            } else {
                                if (mSelectedColor.size()>0) {
                                    result.put("NO_NAME", mSelectedColor.get(0));
                                } else {
                                    result.put("NO_NAME", getColor());
                                }
                            }
                            mListener.onDonePressed(result);
                        }
                        dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(mListener!=null)
                            mListener.onDismiss();
                        dismiss();
                    }
                })
                .create();

        return mAlertDialog;
    }
    @Override
    public void onColorSelected(int color) {

        if(mSelectionType == SELECTION_MULTI){
            if(!mSelectedColor.contains(color))
                mSelectedColor.add(color);
            else mSelectedColor.remove((Object) color);
        }
        else if(mSelectionType == SELECTION_SINGLE){
            if(!mSelectedColor.contains(color)){
                mSelectedColor.clear();
                mSelectedColor.add(color);
            } else mSelectedColor.remove((Object) color);
        }


        refreshPalette();
    }

    public void showPaletteView() {
        if (mProgress != null && mPalette != null) {
            mProgress.setVisibility(View.GONE);
            refreshPalette();
            mPalette.setVisibility(View.VISIBLE);
        }
    }



    public void setColors(ArrayList<Integer> colors) {
        if (mColors != colors) {
            mColors = colors;
            refreshPalette();
        }
    }




    private void refreshPalette() {
        if (mPalette != null && mColors != null) {
            mPalette.drawPalette(mColors, mSelectedColor);
        }
    }

    public int getColor() {return this.color; }
    public List<Integer> getColors() {
        return mColors;
    }

    public ArrayList<Integer> getSelectedColors() {
        return mSelectedColor;
    }

    public String getName() {return this.name; }

    public void setName(String name) { this.name = name;}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList(KEY_COLORS, mColors);
        outState.putSerializable(KEY_SELECTED_COLOR, mSelectedColor);
    }

    /**
     * Interface for a callback when a color square is selected.
     */
    public interface OnDialogButtonListener {

        /**
         * Called when a specific color square has been selected.
         */
        public void onDonePressed(HashMap<String, Integer> result);

        public void onDismiss();

    }
}
