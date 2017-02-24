package com.example.stpl.cameraapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;


public class ScrollListener extends RecyclerView.OnScrollListener {
    private Context context;
    private Picasso picasso;

    public ScrollListener(Context context) {
        this.context = context;
//        picasso = Picasso.with(context);
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//            picasso.resumeTag(context);
            Glide.with(context).resumeRequests();
        } else {
            Glide.with(context).pauseRequests();

//            picasso.pauseTag(context);
        }

    }


}
