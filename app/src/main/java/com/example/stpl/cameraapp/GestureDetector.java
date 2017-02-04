package com.example.stpl.cameraapp;

import android.support.design.widget.BottomSheetBehavior;
import android.view.MotionEvent;
import android.widget.GridView;


public class GestureDetector extends android.view.GestureDetector.SimpleOnGestureListener {
    private static final int SWIPE_MIN_DISTANCE = 120;
    private GridView gridView;
    private BottomSheetBehavior bottomSheetBehavior;

    public GestureDetector(GridView gridView, BottomSheetBehavior bottomSheetBehavior) {
        this.gridView = gridView;
        this.bottomSheetBehavior = bottomSheetBehavior;

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float diffY = e2.getY() - e1.getY();

        if (Math.abs(diffY) > SWIPE_MIN_DISTANCE) {
            if (diffY > 0) {
                //SwipeDown
                if (gridView.getFirstVisiblePosition() == 0) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }


        }


        return false;
    }
}
