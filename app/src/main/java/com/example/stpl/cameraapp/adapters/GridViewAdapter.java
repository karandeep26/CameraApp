package com.example.stpl.cameraapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.stpl.cameraapp.R;
import com.example.stpl.cameraapp.models.MediaDetails;

import java.util.ArrayList;


public class GridViewAdapter extends ArrayAdapter<MediaDetails> {
    private Context context;
    private ArrayList<MediaDetails> mediaDetails;
    private LayoutInflater inflater;

    public GridViewAdapter(Context context, ArrayList<MediaDetails> mediaDetails) {
        super(context, 0);
        this.context = context;
        this.mediaDetails = mediaDetails;
        inflater = ((Activity) context).getLayoutInflater();
    }


    @Override
    public int getCount() {
        return mediaDetails.size();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder viewHolder;
        MediaDetails currentObject = mediaDetails.get(position);
        if (row == null) {
            row = inflater.inflate(R.layout.row, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) row.findViewById(R.id.image);
            viewHolder.tickView = (ImageView) row.findViewById(R.id.tick);
            row.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) row.getTag();
        }
        viewHolder.imageView.setImageBitmap(currentObject.getImage());
        if (currentObject.isChecked()) {
            viewHolder.tickView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tickView.setVisibility(View.GONE);
        }

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

    private static class ViewHolder {
        ImageView imageView, tickView;
    }
}
