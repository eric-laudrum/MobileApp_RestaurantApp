package com.gb.finddining.ui;

import android.net.Uri;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gb.finddining.R;
import com.gb.finddining.data.DataRepository;
import com.gb.finddining.model.RestaurantPhoto;

public class AddPhotoFragment extends Fragment {

    private static final String KEY_ID = "id";
    private Uri selectedUri;

    private final ActivityResultLauncher<String[]> imagePicker = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(),
            uri -> {
                selectedUri = uri;
                ImageView preview = getView() != null ? getView().findViewById(R.id.photo_preview) : null;
                if (preview != null && uri != null) {
                    preview.setImageURI(uri);
                }
                if (uri != null) {
                    try {
                        requireContext().getContentResolver().takePersistableUriPermission(
                                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (Exception ignored) {
                        // Some providers don't allow persistable permissions; best-effort only.
                    }
                }
            }
    );

    public static AddPhotoFragment newInstance(String id) {
        AddPhotoFragment fragment = new AddPhotoFragment();
        Bundle args = new Bundle();
        args.putString(KEY_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_photo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String id = getArguments() != null ? getArguments().getString(KEY_ID) : null;
        EditText caption = view.findViewById(R.id.input_caption);
        Button choose = view.findViewById(R.id.button_choose_photo);
        Button post = view.findViewById(R.id.button_post_photo);

        choose.setOnClickListener(v -> imagePicker.launch(new String[]{"image/*"}));
        post.setOnClickListener(v -> {
            if (id == null) return;
            String captionText = caption.getText() != null ? caption.getText().toString() : "";
            DataRepository.getInstance(requireContext()).addPhoto(id, new RestaurantPhoto(captionText, selectedUri != null ? selectedUri.toString() : null));
            Toast.makeText(requireContext(), "Photo added", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
        });
    }
}
