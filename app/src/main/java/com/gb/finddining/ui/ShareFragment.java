package com.gb.finddining.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gb.finddining.R;
import com.gb.finddining.data.DataRepository;
import com.gb.finddining.model.Restaurant;

public class ShareFragment extends Fragment {

    private static final String KEY_ID = "id";

    public static ShareFragment newInstance(String id) {
        ShareFragment fragment = new ShareFragment();
        Bundle args = new Bundle();
        args.putString(KEY_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_share, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String id = getArguments() != null ? getArguments().getString(KEY_ID) : null;
        Restaurant restaurant = DataRepository.getInstance(requireContext()).getRestaurant(id);
        if (restaurant == null) {
            requireActivity().onBackPressed();
            return;
        }

        view.findViewById(R.id.button_share_facebook).setOnClickListener(v -> share("Facebook", restaurant));
        view.findViewById(R.id.button_share_twitter).setOnClickListener(v -> share("Twitter", restaurant));
        view.findViewById(R.id.button_share_generic).setOnClickListener(v -> share("Generic", restaurant));
        view.findViewById(R.id.button_back_share).setOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void share(String platform, Restaurant restaurant) {
        String text;
        switch (platform) {
            case "Facebook":
                text = "Checking in at " + restaurant.name + "! " + restaurant.address;
                break;
            case "Twitter":
                text = "Just tried " + restaurant.name + " (" + restaurant.rating + "/5).";
                break;
            default:
                text = "Explore " + restaurant.name + ": " + restaurant.address;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(intent, "Share with"));
    }
}
