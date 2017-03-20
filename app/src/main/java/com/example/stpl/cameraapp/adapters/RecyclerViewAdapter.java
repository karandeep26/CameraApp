package com.example.stpl.cameraapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.stpl.cameraapp.R;
import com.example.stpl.cameraapp.Utils;
import com.example.stpl.cameraapp.customViews.SquareImageView;
import com.example.stpl.cameraapp.main.MainActivity;
import com.example.stpl.cameraapp.models.MediaDetails;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by stpl on 2/22/2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<MediaDetails> mediaDetailsList;
    private Context mContext;
    private boolean multiMode;

    private String mediaType;

    public RecyclerViewAdapter() {
        mediaDetailsList = new ArrayList<>();
    }

    public void setMediaDetailsList(List<MediaDetails> mediaDetailsList, String type) {
        this.mediaDetailsList = mediaDetailsList;
        mediaType = type;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent,
                false);
        mContext = parent.getContext();
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MediaDetails mediaDetails = this.mediaDetailsList.get(position);
        if (mediaDetails.getMediaType().equals(Utils.IMAGE)) {

            holder.imageView.setTransitionName(mediaDetails.getFilePath());
//            String path=Utils.getPath(mContext.getContentResolver(),mediaDetails.getFilePath());
//            Log.d("path",path);
            Glide.with(((MainActivity) mContext)).load(mediaDetails.getFilePath()).
                    load(mediaDetails.getFilePath()).fitCenter()
                    .centerCrop().placeholder(R.drawable.placeholder).into(holder.imageView);
            holder.playButton.setVisibility(View.INVISIBLE);
        } else {
            Glide.with(((MainActivity) mContext)).load(mediaDetails.getFilePath()).fitCenter()
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(holder.imageView);
            holder.playButton.setVisibility(View.VISIBLE);
        }
        if (mediaDetails.isChecked()) {
            holder.tickView.setVisibility(View.VISIBLE);
        } else {
            holder.tickView.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return mediaDetailsList.size();
    }

    public String getMediaType() {
        return mediaType;
    }

    public void remove(MediaDetails mediaDetails) {
        int position = mediaDetailsList.indexOf(mediaDetails);
        if (position != -1) {
            mediaDetailsList.remove(position);
            notifyItemRemoved(position);
        }

    }

    public void removeItemAt(int index) {
        mediaDetailsList.remove(index);
        notifyItemRemoved(index);
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image)
        SquareImageView imageView;
        @BindView(R.id.tick)
        ImageView tickView;
        @BindView(R.id.play)
        ImageView playButton;
        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

    public void addItem(MediaDetails mediaDetails) {
        mediaDetailsList.add(mediaDetails);
        notifyItemInserted(mediaDetailsList.size()-1);
    }

    public void addItem(MediaDetails mediaDetails, int index) {
        mediaDetailsList.add(index, mediaDetails);
        notifyItemInserted(index);
    }


    public MediaDetails getItemAt(int position) {
        return mediaDetailsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
