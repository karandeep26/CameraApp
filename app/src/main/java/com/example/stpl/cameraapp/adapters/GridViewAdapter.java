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
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;


public class GridViewAdapter extends ArrayAdapter<MediaDetails> {
    private ArrayList<MediaDetails> mediaDetails;
    private LayoutInflater inflater;

    public GridViewAdapter(Context context) {
        super(context, 0);
        inflater = ((Activity) context).getLayoutInflater();
        mediaDetails = new ArrayList<>();
    }


    @Override
    public int getCount() {
        return (mediaDetails == null) ? 0 : mediaDetails.size();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        ViewHolder viewHolder;
        if (row == null) {
            row = inflater.inflate(R.layout.row, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) row.findViewById(R.id.image);
            viewHolder.tickView = (ImageView) row.findViewById(R.id.tick);
            row.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) row.getTag();
        }

        MediaDetails currentObject = mediaDetails.get(position);
        if (currentObject.getMediaType().equals("image")) {
            Picasso.with(parent.getContext()).load(new File(currentObject.getFilePath()))
                    .tag("tag").centerCrop().resize(500, 500)
                    .placeholder(R.drawable.placeholder).into(viewHolder.imageView);
        } else {
            Picasso.with(parent.getContext()).load("video:" + currentObject.getFilePath())
                    .tag("tag").centerCrop().resize(500, 500)
                    .placeholder(R.drawable.placeholder).into(viewHolder.imageView);
        }
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

    @Override
    public void add(MediaDetails object) {
        super.add(object);
        mediaDetails.add(object);
    }
}
