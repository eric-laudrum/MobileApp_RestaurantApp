package com.gb.finddining.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.gb.finddining.R;
import com.gb.finddining.data.DataRepository;
import com.gb.finddining.model.Restaurant;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class RestaurantMapFragment extends Fragment implements OnMapReadyCallback {

    private static final String KEY_ID = "id";
    private static final int REQUEST_CODE_LOCATION = 42;

    private GoogleMap map;
    private Restaurant restaurant;

    public static RestaurantMapFragment newInstance(String id) {
        Bundle args = new Bundle();
        args.putString(KEY_ID, id);
        RestaurantMapFragment fragment = new RestaurantMapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_restaurant_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String id = getArguments() != null ? getArguments().getString(KEY_ID) : null;
        restaurant = DataRepository.getInstance(requireContext()).getRestaurant(id);
        if (restaurant == null) {
            Toast.makeText(requireContext(), "Restaurant not found", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
            return;
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setZoomGesturesEnabled(true);

        LatLng target = resolveLocation();
        map.addMarker(new MarkerOptions()
                .position(target)
                .title(restaurant.name)
                .snippet(restaurant.address));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(target, 14f));

        enableMyLocationIfPermitted();
    }

    private void enableMyLocationIfPermitted() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                map.setMyLocationEnabled(true);
            } catch (SecurityException ignored) {
                // Permission check passed but enable failed; ignore to avoid crash.
            }
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED && map != null) {
            enableMyLocationIfPermitted();
        }
    }

    private LatLng resolveLocation() {
        if (restaurant.latitude != 0 || restaurant.longitude != 0) {
            return new LatLng(restaurant.latitude, restaurant.longitude);
        }
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> results = geocoder.getFromLocationName(restaurant.name + " " + restaurant.address, 1);
            if (results != null && !results.isEmpty()) {
                Address a = results.get(0);
                return new LatLng(a.getLatitude(), a.getLongitude());
            }
        } catch (IOException ignored) {
        }
        // Toronto fallback to keep map centered even if geocoding fails.
        return new LatLng(43.6532, -79.3832);
    }
}
