package com.gb.finddining.ui;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gb.finddining.R;
import com.gb.finddining.model.RestaurantPhoto;

import java.util.List;

class PhotoPagerAdapter extends RecyclerView.Adapter<PhotoPagerAdapter.PhotoViewHolder> {

    private final List<RestaurantPhoto> photos;

    PhotoPagerAdapter(List<RestaurantPhoto> photos) {
        this.photos = photos;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_page, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        RestaurantPhoto photo = photos.get(position);
        holder.caption.setText(photo.caption != null && !photo.caption.isEmpty() ? photo.caption : "Photo");
        if (photo.uri != null && !photo.uri.isEmpty()) {
            try {
                holder.image.setImageURI(Uri.parse(photo.uri));
            } catch (Exception e) {
                holder.image.setImageResource(R.drawable.ic_app_logo);
            }
        } else {
            holder.image.setImageResource(R.drawable.ic_app_logo);
        }
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final TextView caption;

        PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.photo_image);
            caption = itemView.findViewById(R.id.photo_caption);
        }
    }
}
