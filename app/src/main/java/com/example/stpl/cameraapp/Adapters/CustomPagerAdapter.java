package com.example.stpl.cameraapp.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.stpl.cameraapp.ImageLoadingListener;
import com.example.stpl.cameraapp.Models.MediaDetails;
import com.example.stpl.cameraapp.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

import static com.example.stpl.cameraapp.Utils.mediaStorageDir;


/**
 * Created by stpl on 1/11/2017.
 */

public class CustomPagerAdapter extends PagerAdapter  {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<MediaDetails> mediaDetails;
    private ImageLoadingListener imageLoadingListener;


    public CustomPagerAdapter(Context mContext,ArrayList<MediaDetails> mediaDetails) {
        this.mediaDetails=mediaDetails;
        this.mContext = mContext;
        this.mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoadingListener=new ImageLoaderListener();

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
        return view== object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView=mLayoutInflater.inflate(R.layout.viewpager_item,container,false);
        ImageView imageView= (ImageView) itemView.findViewById(R.id.image_item);
        Picasso.with(mContext).load(new File(mediaStorageDir + "/" +mediaDetails.get(position)
                .getFilePath())).into(imageView);
//        Bitmap bitmap = BitmapFactory.decodeFile(mediaStorageDir + "/" +mediaDetails.get(position).getFilePath());
//        imageView.setImageBitmap(bitmap);
   //     imageLoadingListener.loadImage(mediaDetails.get(position).getFilePath(),imageView);
        container.addView(itemView);
        return itemView;
    }

    private static class ImageLoaderListener implements ImageLoadingListener{

        @Override
        public void loadImage(String path, ImageView imageView) {
            Observable<Bitmap> bitmapObservable=Observable.defer(() -> {
                Bitmap bitmap = BitmapFactory.decodeFile(mediaStorageDir + "/" +path);
                return Observable.just(bitmap);

            });
            bitmapObservable.subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Bitmap>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    Log.d("bitmap error",e.toString());
                }

                @Override
                public void onNext(Bitmap bitmap) {
                    imageView.setImageBitmap(bitmap);
                }
            });


        }
    }

}