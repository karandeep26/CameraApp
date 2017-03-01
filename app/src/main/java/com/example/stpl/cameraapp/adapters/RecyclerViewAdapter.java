package com.example.stpl.cameraapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.stpl.cameraapp.R;
import com.example.stpl.cameraapp.Utils;
import com.example.stpl.cameraapp.customViews.SquareImageView;
import com.example.stpl.cameraapp.main.MainActivity;
import com.example.stpl.cameraapp.models.MediaDetails;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by stpl on 2/22/2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<MediaDetails> mediaDetailsList;
    private Context mContext;
    private boolean multiMode;
    private HashSet<Integer> selectedIndex;
    private File thumbnailFile;
    public RecyclerViewAdapter() {
        mediaDetailsList = new ArrayList<>();
        selectedIndex = new HashSet<>();

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
            thumbnailFile = new File(Environment.
                    getExternalStoragePublicDirectory
                            (Environment.DIRECTORY_PICTURES) +
                    File.separator + new File(mediaDetails.getFilePath()).getName());
//            if (thumbnailFile.exists()) {
//                Log.d("file exist",thumbnailFile.getPath());
//                Glide.with(((MainActivity) mContext)).load(thumbnailFile).asBitmap()
//                        .placeholder(R.drawable.placeholder).fitCenter().centerCrop()
//                        .into(holder.imageView);
//            } else {
                Glide.with(((MainActivity) mContext)).load(mediaDetails.getFilePath()).asBitmap()
                        .placeholder(R.drawable.placeholder).fitCenter().centerCrop()
                        .into(new BitmapImageViewTarget(holder.imageView) {
                            @Override
                            protected void setResource(final Bitmap resource) {
                                super.setResource(resource);
                                new Thread() {
                                    @Override
                                    public void run() {
//                                        if (!thumbnailFile.exists()) {
//                                            FileOutputStream out = null;
//                                            try {
//                                                Log.d("thumbnail exist before", thumbnailFile
// .exists() + "");
//                                                out = new FileOutputStream(thumbnailFile);
//                                                resource.compress(Bitmap.CompressFormat.JPEG, 100,
//                                                        out);
//                                            } catch (Exception e) {
//                                                e.printStackTrace();
//                                            } finally {
//                                                try {
//                                                    if (out != null) {
//                                                        out.close();
//                                                        Log.d("thumbnail created for",
//                                                                thumbnailFile.getName());
//                                                        Log.d("thumbnail exist after",
//                                                                thumbnailFile.exists() + "");
//
//                                                    }
//                                                } catch (IOException e) {
//                                                    e.printStackTrace();
//                                                }
//                                            }
//                                        }
                                    }
                                }.start();

                            }
                        });

            holder.playButton.setVisibility(View.INVISIBLE);
            //}
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


    public MediaDetails getItemAt(int position) {
        return mediaDetailsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
