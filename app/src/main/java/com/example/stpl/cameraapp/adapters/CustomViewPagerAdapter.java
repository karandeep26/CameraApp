package com.example.stpl.cameraapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.stpl.cameraapp.R;
import com.example.stpl.cameraapp.Utils;
import com.example.stpl.cameraapp.models.MediaDetails;

import java.util.ArrayList;



public class CustomViewPagerAdapter extends PagerAdapter {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<MediaDetails> mediaDetails;
    private Animation  fadeOut;

    public CustomViewPagerAdapter(Context mContext, ArrayList<MediaDetails> mediaDetails) {
        this.mediaDetails = mediaDetails;
        this.mContext = mContext;
        fadeOut = AnimationUtils.loadAnimation(mContext, R.anim.fade_out);
        this.mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((View)object).startAnimation(fadeOut);
        ImageView imageView = (ImageView) ((View) object).findViewById(R.id.image_item);
        container.removeView((View) object);
        imageView.setImageDrawable(null);

        BitmapDrawable bmpDrawable = (BitmapDrawable) imageView.getDrawable();
        if (bmpDrawable != null) {
            Bitmap bitmap = bmpDrawable.getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                // This is the important part

                bitmap = null;
                Log.d("on destroy", position + "");


            }
        }
        object = null;
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
        Drawable drawable = imageView.getDrawable();
        if (drawable != null) {
            ((BitmapDrawable) drawable).getBitmap().recycle();
            Log.d("recycle", "true");
        }
//        Picasso.with(mContext).load("file://" + new File(mediaDetails.get(position)
//                .getFilePath())).tag(container.getContext()).centerInside().
//                resize(Utils.width, Utils.height).onlyScaleDown().into(imageView);

        Glide.with(mContext).load(mediaDetails.get(position).getFilePath())
                .override(Utils.width, Utils.height).fitCenter()
                .into(imageView);
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



