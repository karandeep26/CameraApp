package com.example.stpl.cameraapp.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.stpl.cameraapp.R;
import com.example.stpl.cameraapp.customViews.CircleRectView;
import com.example.stpl.cameraapp.fullImageView.FullImageActivity;
import com.example.stpl.cameraapp.models.MediaDetails;

import java.util.ArrayList;


public class CustomViewPagerAdapter extends PagerAdapter {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<MediaDetails> mediaDetails;
    private Animation fadeOut;
    boolean animate = true;

    public CustomViewPagerAdapter(Context mContext, ArrayList<MediaDetails> mediaDetails) {
        this.mediaDetails = mediaDetails;
        this.mContext = mContext;
        fadeOut = AnimationUtils.loadAnimation(mContext, R.anim.fade_out);
        this.mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((View) object).startAnimation(fadeOut);
        ImageView imageView = (ImageView) ((View) object).findViewById(R.id.image_item);
        container.removeView((View) object);
        imageView.setImageDrawable(null);
    }

    @Override
    public int getCount() {
        return mediaDetails.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.viewpager_item, container, false);
        CircleRectView imageView = (CircleRectView) itemView.findViewById(R.id.image_item);
//        imageView.isFullScreen(true);
        if (imageView.getTransitionName() == null) {
            imageView.setTransitionName(position + "");
        }
        Glide.with(((FullImageActivity) mContext)).load(mediaDetails.get(position).getFilePath())
                .fitCenter().into(imageView);


        imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver
                .OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                ActivityCompat.startPostponedEnterTransition(((FullImageActivity) mContext));
                animate = false;
                String n = imageView.getTransitionName();
                Log.d("ViewPager Transition", n);
                return true;
            }
        });

        container.addView(itemView);
        itemView.setTag("myView" + position);
        return itemView;
    }

    public MediaDetails getObjectAt(int i) {
        return mediaDetails.get(i);
    }

    public void removeItemAt(int i) {
        mediaDetails.remove(i);
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }


}



