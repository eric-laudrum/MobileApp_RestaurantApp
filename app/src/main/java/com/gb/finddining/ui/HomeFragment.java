package com.gb.finddining.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gb.finddining.NavigationHost;
import com.gb.finddining.R;
import com.gb.finddining.data.DataRepository;
import com.gb.finddining.model.Restaurant;
import com.gb.finddining.ui.RestaurantListAdapter;

import java.util.List;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText searchInput = view.findViewById(R.id.input_search);
        Button searchButton = view.findViewById(R.id.button_search);
        ListView list = view.findViewById(R.id.restaurant_list);

        List<Restaurant> restaurants = DataRepository.getInstance(requireContext()).getRestaurants();
        list.setAdapter(new RestaurantListAdapter(requireContext(), restaurants, restaurant -> {
            if (getActivity() instanceof NavigationHost) {
                ((NavigationHost) getActivity()).navigateToDetails(restaurant.id);
            }
        }));

        searchButton.setOnClickListener(v -> {
            if (getActivity() instanceof NavigationHost) {
                String q = searchInput.getText() != null ? searchInput.getText().toString() : "";
                ((NavigationHost) getActivity()).navigateToSearch(q);
            }
        });
    }
}