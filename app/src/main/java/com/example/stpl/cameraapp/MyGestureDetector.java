package com.example.stpl.cameraapp;

import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.GridLayoutManager;
import android.view.MotionEvent;

/**
 * Created by stpl on 2/22/2017.
 */

public class MyGestureDetector extends android.view.GestureDetector.SimpleOnGestureListener {
    private static final int SWIPE_MIN_DISTANCE = 120;
    private GridLayoutManager gridLayoutManager;
    private BottomSheetBehavior bottomSheetBehavior;

    public MyGestureDetector(GridLayoutManager gridLayoutManager, BottomSheetBehavior
            bottomSheetBehavior) {
        this.gridLayoutManager = gridLayoutManager;
        this.bottomSheetBehavior = bottomSheetBehavior;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float diffY = e2.getY() - e1.getY();

        if (Math.abs(diffY) > SWIPE_MIN_DISTANCE) {
            if (diffY > 0) {
                //SwipeDown

                if (gridLayoutManager.findFirstCompletelyVisibleItemPosition() <= 0) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }


        }


        return false;
    }
}
