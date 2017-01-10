package com.example.stpl.cameraapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.stpl.cameraapp.Models.MediaDetails;
import com.example.stpl.cameraapp.R;

import java.util.ArrayList;

/**
 * Created by stpl on 11/21/2016.
 */

public class ImageGridViewAdapter extends ArrayAdapter<MediaDetails> {
    private Context context;
    private ArrayList<MediaDetails> mediaDetails;

    public ImageGridViewAdapter(Context context, ArrayList<MediaDetails> mediaDetails) {
        super(context, 0);
        this.context=context;
        this.mediaDetails = mediaDetails;
    }


    @Override
    public int getCount() {
        return mediaDetails.size();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row=convertView;
        ViewHolder viewHolder;
        if(row==null){
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row=inflater.inflate(R.layout.row,null);
            viewHolder=new ViewHolder();
            viewHolder.imageView= (ImageView) row.findViewById(R.id.image);
            row.setTag(viewHolder);
        }
        else
        viewHolder= (ViewHolder) row.getTag();
        viewHolder.imageView.setImageBitmap(mediaDetails.get(position).getImage());

        return row;
    }

    @Nullable
    @Override
    public MediaDetails getItem(int position) {
        return mediaDetails.get(position);
    }

    public void setMediaDetails(ArrayList<MediaDetails> mediaDetails) {
        this.mediaDetails = mediaDetails;
        notifyDataSetChanged();
    }

    private static class ViewHolder{
        ImageView imageView;
    }
}
