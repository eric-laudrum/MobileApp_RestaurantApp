package com.gb.finddining.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gb.finddining.R;
import com.gb.finddining.data.DataRepository;
import com.gb.finddining.model.Review;

public class RateReviewFragment extends Fragment {

    private static final String KEY_ID = "id";

    public static RateReviewFragment newInstance(String id) {
        RateReviewFragment fragment = new RateReviewFragment();
        Bundle args = new Bundle();
        args.putString(KEY_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rate_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String id = getArguments() != null ? getArguments().getString(KEY_ID) : null;
        RatingBar ratingBar = view.findViewById(R.id.rating_bar);
        EditText reviewText = view.findViewById(R.id.input_review);
        Button submit = view.findViewById(R.id.button_submit_review);

        submit.setOnClickListener(v -> {
            if (id == null) return;
            int rating = Math.round(ratingBar.getRating());
            String text = reviewText.getText() != null ? reviewText.getText().toString() : "";
            DataRepository.getInstance(requireContext()).addReview(id, new Review("You", rating, text, "Today"));
            Toast.makeText(requireContext(), "Review saved", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
        });

    }
}
