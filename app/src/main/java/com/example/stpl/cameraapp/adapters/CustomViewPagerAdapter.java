package com.example.stpl.cameraapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.stpl.cameraapp.R;
import com.example.stpl.cameraapp.Utils;
import com.example.stpl.cameraapp.fullImageView.FullImageActivity;
import com.example.stpl.cameraapp.models.MediaDetails;

import java.util.ArrayList;


public class CustomViewPagerAdapter extends PagerAdapter {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private int position;
    private ArrayList<MediaDetails> mediaDetails;
    private Animation fadeOut;
    boolean animate = true;

    public CustomViewPagerAdapter(Context mContext, ArrayList<MediaDetails> mediaDetails, int
            position) {
        this.mediaDetails = mediaDetails;
        this.mContext = mContext;
        fadeOut = AnimationUtils.loadAnimation(mContext, R.anim.fade_out);
        this.mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        this.position = position;
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
        ImageView imageView = (ImageView) itemView.findViewById(R.id.image_item);

            imageView.setTransitionName(position + "");


        Glide.with(((FullImageActivity) mContext)).load(mediaDetails.get(position).getFilePath())
                .asBitmap()
                .override(Utils.width, Utils.height).listener(new RequestListener<String, Bitmap>
                () {

            @Override
            public boolean onException(Exception e, String model, Target<Bitmap> target, boolean
                    isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target,
                                           boolean isFromMemoryCache, boolean isFirstResource) {
                ActivityCompat.startPostponedEnterTransition(((FullImageActivity) mContext));

                return false;
            }
        }).into(imageView);


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



