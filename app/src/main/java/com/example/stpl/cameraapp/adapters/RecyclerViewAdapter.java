package com.example.stpl.cameraapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.stpl.cameraapp.R;
import com.example.stpl.cameraapp.Utils;
import com.example.stpl.cameraapp.customViews.SquareImageView;
import com.example.stpl.cameraapp.models.MediaDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stpl on 2/22/2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<MediaDetails> mediaDetailsList;
    private Context mContext;
    private boolean multiMode;

    public RecyclerViewAdapter() {
        mediaDetailsList = new ArrayList<>();
    }

    public void setMediaDetailsList(List<MediaDetails> mediaDetailsList) {
        this.mediaDetailsList = mediaDetailsList;
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
            Glide.with(mContext).load(mediaDetails.getFilePath()).fitCenter().centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(holder.imageView);
            holder.playButton.setVisibility(View.INVISIBLE);
        } else {
            Glide.with(mContext).load(mediaDetails.getFilePath()).asBitmap().fitCenter()
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(holder.imageView);

        }
    }

    @Override
    public int getItemCount() {
        return mediaDetailsList.size();
    }

    public String getMediaType() {
        if (mediaDetailsList.size() != 0) {
            return mediaDetailsList.get(0).getMediaType();
        }
        return null;
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
        SquareImageView imageView;
        ImageView tickView, playButton;
        FrameLayout rootLayout;
        View itemView;

        ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            rootLayout = (FrameLayout) itemView.findViewById(R.id.root_layout);
            imageView = (SquareImageView) itemView.findViewById(R.id.image);
            tickView = (ImageView) itemView.findViewById(R.id.tick);
            playButton = (ImageView) itemView.findViewById(R.id.play);

        }
    }

    public void addItem(MediaDetails mediaDetails) {
        mediaDetailsList.add(mediaDetails);
        notifyItemInserted(mediaDetailsList.size() - 1);
    }

    public void addItem(MediaDetails mediaDetails, int index) {
        mediaDetailsList.add(index, mediaDetails);
        notifyItemInserted(index);
    }

    void setMultiMode(boolean isEnabled) {
        this.multiMode = isEnabled;
    }

    public void checkItem(View view) {
        view.findViewById(R.id.tick).setVisibility(View.VISIBLE);
    }

    public MediaDetails getItemAt(int position) {
        return mediaDetailsList.get(position);
    }

}
