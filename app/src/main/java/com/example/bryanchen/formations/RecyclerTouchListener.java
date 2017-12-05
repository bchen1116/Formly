//package com.example.bryanchen.formations;
//
//import android.content.Context;
//import android.os.Build;
//import android.support.annotation.RequiresApi;
//import android.support.v7.widget.RecyclerView;
//import android.view.GestureDetector;
//import android.view.MotionEvent;
//import android.view.View;
//
///**
// * Created by BryanChen on 12/4/17.
// */
//
//public interface ClickListener {
//    void onClick(View view, int position);
//
//    void onLongClick(View view, int position);
//}
//
//public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
//
//    private GestureDetector gestureDetector;
//    private StartPage.ClickListener clickListener;
//
//    public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final StartPage.ClickListener clickListener) {
//        this.clickListener = clickListener;
//        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
//            @Override
//            public boolean onSingleTapUp(MotionEvent e) {
//                return true;
//            }
//
//            @Override
//            public void onLongPress(MotionEvent e) {
//                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
//                if (child != null && clickListener != null) {
//                    clickListener.onLongClick(child, recyclerView.getChildPosition(child));
//                }
//            }
//        });
//    }
//
//    @Override
//    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
//
//        View child = rv.findChildViewUnder(e.getX(), e.getY());
//        if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
//            clickListener.onClick(child, rv.getChildPosition(child));
//        }
//        return false;
//    }
//
//    @Override
//    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
//    }
//
//    @Override
//    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//
//    }
//}
