package com.gb.finddining.ui;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gb.finddining.R;
import com.gb.finddining.model.Restaurant;

import java.text.DecimalFormat;
import java.util.List;

public class RestaurantListAdapter extends ArrayAdapter<Restaurant> {

    public interface OnRestaurantClick {
        void onClick(Restaurant restaurant);
    }

    private final LayoutInflater inflater;
    private final DecimalFormat df = new DecimalFormat("#.#");
    private final OnRestaurantClick listener;

    public RestaurantListAdapter(@NonNull Context context, @NonNull List<Restaurant> restaurants, OnRestaurantClick listener) {
        super(context, 0, restaurants);
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_restaurant, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Restaurant restaurant = getItem(position);
        if (restaurant != null) {
            holder.name.setText(restaurant.name);
            holder.address.setText(restaurant.address);
            holder.meta.setText(df.format(restaurant.rating) + " • " + restaurant.reviewCount + " reviews • " + restaurant.distanceKm + " km away");
            holder.tags.setText(join(restaurant.tags));
            String primaryUri = restaurant.photos.isEmpty() ? null : restaurant.photos.get(0).uri;
            if (primaryUri != null && !primaryUri.isEmpty()) {
                try {
                    holder.image.setImageURI(Uri.parse(primaryUri));
                } catch (Exception e) {
                    holder.image.setImageResource(R.drawable.ic_app_logo);
                }
            } else {
                holder.image.setImageResource(R.drawable.ic_app_logo);
            }
            convertView.setOnClickListener(v -> {
                if (listener != null) listener.onClick(restaurant);
            });
        }
        return convertView;
    }

    private String join(List<String> items) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            builder.append(items.get(i));
            if (i < items.size() - 1) {
                builder.append(" • ");
            }
        }
        return builder.toString();
    }

    static class ViewHolder {
        final TextView name;
        final TextView address;
        final TextView tags;
        final TextView meta;
        final ImageView image;

        ViewHolder(@NonNull View itemView) {
            name = itemView.findViewById(R.id.restaurant_name);
            address = itemView.findViewById(R.id.restaurant_address);
            tags = itemView.findViewById(R.id.restaurant_tags);
            meta = itemView.findViewById(R.id.restaurant_meta);
            image = itemView.findViewById(R.id.restaurant_image);
        }
    }
}
