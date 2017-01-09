package com.example.stpl.cameraapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.stpl.cameraapp.Models.ImageDetails;
import com.example.stpl.cameraapp.R;

import java.util.ArrayList;

/**
 * Created by stpl on 11/21/2016.
 */

public class ImageGridViewAdapter extends ArrayAdapter<ImageDetails> {
    private Context context;
    private ArrayList<ImageDetails> imageDetails;
    private String filePath;

    public ImageGridViewAdapter(Context context, ArrayList<ImageDetails> imageDetails) {
        super(context, 0);
        this.context=context;
        this.imageDetails=imageDetails;
    }

    @Override
    public int getCount() {
        return imageDetails.size();
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
        viewHolder.imageView.setImageBitmap(imageDetails.get(position).getImage());

        return row;
    }




    private static class ViewHolder{
        ImageView imageView;
    }
}
