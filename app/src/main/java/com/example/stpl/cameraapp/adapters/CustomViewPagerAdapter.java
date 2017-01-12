package com.example.stpl.cameraapp.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.stpl.cameraapp.R;
import com.example.stpl.cameraapp.activity.FullImageActivity;
import com.example.stpl.cameraapp.models.MediaDetails;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import static com.example.stpl.cameraapp.Utils.mediaStorageDir;


public class CustomViewPagerAdapter extends PagerAdapter {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<MediaDetails> mediaDetails;

    public CustomViewPagerAdapter(Context mContext, ArrayList<MediaDetails> mediaDetails) {
        this.mediaDetails = mediaDetails;
        this.mContext = mContext;
        this.mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mediaDetails.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.viewpager_item, container, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.image_item);
        Picasso.with(mContext).load(new File(mediaStorageDir + "/" + mediaDetails.get(position)
                .getFilePath())).into(imageView);
        container.addView(itemView);
        imageView.setOnTouchListener((v, event) -> {
            Log.d("on touch", "called");
            ((FullImageActivity) mContext).toggleTopPanelVisibility();
            return false;
        });
        return itemView;
    }
}



