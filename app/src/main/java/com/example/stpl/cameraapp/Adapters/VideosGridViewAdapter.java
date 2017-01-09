package com.example.stpl.cameraapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.stpl.cameraapp.R;
import com.example.stpl.cameraapp.Models.VideoDetails;

import java.util.ArrayList;

/**
 * Created by stpl on 12/16/2016.
 */

public class VideosGridViewAdapter extends ArrayAdapter<VideoDetails> {
    private ArrayList<VideoDetails> videoDetails;
    private Context context;

    public VideosGridViewAdapter(Context context, ArrayList<VideoDetails> objects) {
        super(context,0, objects);
        this.videoDetails=objects;
        this.context=context;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row=convertView;
        VideosGridViewAdapter.ViewHolder viewHolder;
        if(row==null)
        {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row=inflater.inflate(R.layout.row,null);
            viewHolder=new VideosGridViewAdapter.ViewHolder();
            viewHolder.imageView= (ImageView) row.findViewById(R.id.image);
            row.setTag(viewHolder);
        }
        else
            viewHolder= (VideosGridViewAdapter.ViewHolder) row.getTag();
        viewHolder.imageView.setImageBitmap(videoDetails.get(position).getImage());

        return row;
    }


    private static class ViewHolder{
        ImageView imageView;
    }
}
