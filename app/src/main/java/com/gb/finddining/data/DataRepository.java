package com.gb.finddining.data;

import android.content.Context;

import com.gb.finddining.model.Restaurant;
import com.gb.finddining.model.RestaurantPhoto;
import com.gb.finddining.model.Review;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class DataRepository {
    private static DataRepository instance;

    private final LocalDatabase database;
    private final List<Restaurant> restaurants = new ArrayList<>();

    private DataRepository(Context context) {
        database = new LocalDatabase(context.getApplicationContext());
        database.seedIfEmpty(createSeedData());
        restaurants.addAll(database.getRestaurants());
    }

    public static synchronized DataRepository getInstance(Context context) {
        if (instance == null) {
            instance = new DataRepository(context);
        }
        return instance;
    }

    private List<Restaurant> createSeedData() {
        List<Restaurant> seeds = new ArrayList<>();
        Restaurant harbor = new Restaurant("1", "Harbor House", "12 Seaside Ave", Arrays.asList("Seafood", "Waterfront", "Date Night"), 4.6, 186, 1.2, 43.6426, -79.3871);
        harbor.reviews.add(new Review("Mara", 5, "Incredible oysters and a view you cannot beat.", "Nov 3"));
        harbor.reviews.add(new Review("Luis", 4, "Great service, would come again.", "Nov 1"));
        harbor.photos.add(new RestaurantPhoto("Seaside patio", "android.resource://com.gb.finddining/drawable/splash_image"));
        harbor.photos.add(new RestaurantPhoto("Signature dish", "android.resource://com.gb.finddining/drawable/ic_app_logo"));
        harbor.photos.add(new RestaurantPhoto("Chef's special", "android.resource://com.gb.finddining/drawable/splash_image"));

        Restaurant cedar = new Restaurant("2", "Cedar & Stone", "204 Garden Street", Arrays.asList("Vegan", "Brunch", "Cozy"), 4.4, 98, 0.6, 43.6665, -79.3945);
        cedar.reviews.add(new Review("Priya", 5, "Loved the seasonal menu and coffee!", "Oct 31"));
        cedar.photos.add(new RestaurantPhoto("Brunch plate", "android.resource://com.gb.finddining/drawable/splash_image"));
        cedar.photos.add(new RestaurantPhoto("Garden seating", "android.resource://com.gb.finddining/drawable/ic_app_logo"));

        Restaurant golden = new Restaurant("3", "Golden Chopsticks", "77 Market Lane", Arrays.asList("Noodles", "Takeout", "Late Night"), 4.2, 132, 2.4, 43.6532, -79.3832);
        golden.reviews.add(new Review("Sam", 4, "Quick service and generous portions.", "Oct 29"));
        golden.photos.add(new RestaurantPhoto("Hand-pulled noodles", "android.resource://com.gb.finddining/drawable/ic_app_logo"));
        golden.photos.add(new RestaurantPhoto("Late night vibe", "android.resource://com.gb.finddining/drawable/splash_image"));

        seeds.add(harbor);
        seeds.add(cedar);
        seeds.add(golden);
        return seeds;
    }

    public List<Restaurant> getRestaurants() {
        return restaurants;
    }

    public Restaurant getRestaurant(String id) {
        for (Restaurant r : restaurants) {
            if (r.id.equals(id)) {
                return r;
            }
        }
        return null;
    }

    public List<Restaurant> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return restaurants;
        }
        String lower = query.toLowerCase(Locale.getDefault());
        List<Restaurant> filtered = new ArrayList<>();
        for (Restaurant r : restaurants) {
            boolean match = r.name.toLowerCase(Locale.getDefault()).contains(lower);
            if (!match) {
                for (String tag : r.tags) {
                    if (tag.toLowerCase(Locale.getDefault()).contains(lower)) {
                        match = true;
                        break;
                    }
                }
            }
            if (match) filtered.add(r);
        }
        return filtered;
    }

    public void addReview(String restaurantId, Review review) {
        LocalDatabase.ReviewStats stats = database.addReview(restaurantId, review);
        Restaurant restaurant = getRestaurant(restaurantId);
        if (restaurant == null) return;
        restaurant.reviews.add(0, review);
        restaurant.reviewCount = stats.count;
        restaurant.rating = stats.average;
    }

    public void addPhoto(String restaurantId, RestaurantPhoto photo) {
        database.addPhoto(restaurantId, photo);
        Restaurant restaurant = getRestaurant(restaurantId);
        if (restaurant == null) return;
        restaurant.photos.add(0, photo);
    }
}
