package com.gb.finddining.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.gb.finddining.NavigationHost;
import com.gb.finddining.R;
import com.gb.finddining.data.DataRepository;
import com.gb.finddining.model.Restaurant;
import com.gb.finddining.model.RestaurantPhoto;

public class RestaurantDetailsFragment extends Fragment {

    private static final String KEY_ID = "id";

    public static RestaurantDetailsFragment newInstance(String id) {
        RestaurantDetailsFragment fragment = new RestaurantDetailsFragment();
        Bundle args = new Bundle();
        args.putString(KEY_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_restaurant_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String id = getArguments() != null ? getArguments().getString(KEY_ID) : null;
        Restaurant restaurant = DataRepository.getInstance(requireContext()).getRestaurant(id);
        if (restaurant == null) {
            Toast.makeText(requireContext(), "Restaurant not found", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
            return;
        }

        TextView name = view.findViewById(R.id.detail_name);
        TextView address = view.findViewById(R.id.detail_address);
        TextView tags = view.findViewById(R.id.detail_tags);
        TextView rating = view.findViewById(R.id.detail_rating);
        ViewPager2 photosPager = view.findViewById(R.id.detail_photos_pager);
        RecyclerView reviewsList = view.findViewById(R.id.detail_reviews);

        name.setText(restaurant.name);
        address.setText(restaurant.address);
        tags.setText(join(restaurant.tags));
        rating.setText(restaurant.rating + " • " + restaurant.reviewCount + " reviews");

        java.util.List<RestaurantPhoto> photos = restaurant.photos.isEmpty()
                ? java.util.Collections.singletonList(new RestaurantPhoto("Photo", null))
                : restaurant.photos;
        photosPager.setAdapter(new PhotoPagerAdapter(photos));

        reviewsList.setLayoutManager(new LinearLayoutManager(requireContext()));
        reviewsList.setAdapter(new ReviewAdapter(restaurant.reviews));

        view.findViewById(R.id.button_rate).setOnClickListener(v -> {
            if (getActivity() instanceof NavigationHost) {
                ((NavigationHost) getActivity()).navigateToRate(restaurant.id);
            }
        });
        view.findViewById(R.id.button_add_photo).setOnClickListener(v -> {
            if (getActivity() instanceof NavigationHost) {
                ((NavigationHost) getActivity()).navigateToAddPhoto(restaurant.id);
            }
        });
        view.findViewById(R.id.button_share).setOnClickListener(v -> {
            if (getActivity() instanceof NavigationHost) {
                ((NavigationHost) getActivity()).navigateToShare(restaurant.id);
            }
        });
        view.findViewById(R.id.button_directions).setOnClickListener(v -> openMaps(restaurant));
    }

    private void openMaps(Restaurant restaurant) {
        Uri uri = Uri.parse("geo:0,0?q=" + Uri.encode(restaurant.name + " " + restaurant.address));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
        mapIntent.setPackage("com.google.android.apps.maps");
        try {
            startActivity(mapIntent);
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }
    }

    private String join(java.util.List<String> items) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            builder.append(items.get(i));
            if (i < items.size() - 1) {
                builder.append(" • ");
            }
        }
        return builder.toString();
    }
}