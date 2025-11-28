package com.gb.finddining.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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

public class SearchResultsFragment extends Fragment {

    private static final String KEY_QUERY = "query";

    public static SearchResultsFragment newInstance(String query) {
        SearchResultsFragment fragment = new SearchResultsFragment();
        Bundle args = new Bundle();
        args.putString(KEY_QUERY, query);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_results, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String query = getArguments() != null ? getArguments().getString(KEY_QUERY, "") : "";
        TextView title = view.findViewById(R.id.search_title);
        ListView list = view.findViewById(R.id.search_list);

        title.setText("Results for \"" + query + "\"");
        List<Restaurant> filtered = DataRepository.getInstance(requireContext()).search(query);
        list.setAdapter(new RestaurantListAdapter(requireContext(), filtered, restaurant -> {
            if (getActivity() instanceof NavigationHost) {
                ((NavigationHost) getActivity()).navigateToDetails(restaurant.id);
            }
        }));

   }
}
