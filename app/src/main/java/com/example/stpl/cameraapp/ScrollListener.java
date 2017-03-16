package com.example.stpl.cameraapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.stpl.cameraapp.main.MainActivity;


public class ScrollListener extends RecyclerView.OnScrollListener {
    private Context context;

    public ScrollListener(Context context) {
        this.context = context;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            Glide.with(((MainActivity) context)).resumeRequests();
        } else {
            Glide.with(((MainActivity) context)).pauseRequests();
        }

    }
}
