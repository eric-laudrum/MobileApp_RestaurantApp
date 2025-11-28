package com.gb.finddining.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.gb.finddining.model.Restaurant;
import com.gb.finddining.model.RestaurantPhoto;
import com.gb.finddining.model.Review;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

class LocalDatabase extends SQLiteOpenHelper {

    private static final String DB_NAME = "finddining.db";
    private static final int DB_VERSION = 2;

    LocalDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE restaurants (" +
                "id TEXT PRIMARY KEY," +
                "name TEXT," +
                "address TEXT," +
                "tags TEXT," +
                "rating REAL," +
                "review_count INTEGER," +
                "distance REAL," +
                "latitude REAL," +
                "longitude REAL" +
                ")");
        db.execSQL("CREATE TABLE reviews (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "restaurant_id TEXT NOT NULL," +
                "author TEXT," +
                "rating INTEGER," +
                "text TEXT," +
                "date TEXT," +
                "FOREIGN KEY(restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE" +
                ")");
        db.execSQL("CREATE TABLE photos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "restaurant_id TEXT NOT NULL," +
                "caption TEXT," +
                "uri TEXT," +
                "FOREIGN KEY(restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE restaurants ADD COLUMN latitude REAL DEFAULT 0");
            db.execSQL("ALTER TABLE restaurants ADD COLUMN longitude REAL DEFAULT 0");
        }
    }

    void seedIfEmpty(List<Restaurant> seedRestaurants) {
        SQLiteDatabase db = getWritableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM restaurants", null)) {
            if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
                return;
            }
        }

        db.beginTransaction();
        try {
            for (Restaurant restaurant : seedRestaurants) {
                ContentValues values = new ContentValues();
                values.put("id", restaurant.id);
                values.put("name", restaurant.name);
                values.put("address", restaurant.address);
                values.put("tags", TextUtils.join(",", restaurant.tags));
                values.put("rating", restaurant.rating);
                values.put("review_count", restaurant.reviewCount);
                values.put("distance", restaurant.distanceKm);
                values.put("latitude", restaurant.latitude);
                values.put("longitude", restaurant.longitude);
                db.insert("restaurants", null, values);

                for (Review review : restaurant.reviews) {
                    insertReview(db, restaurant.id, review);
                }
                for (RestaurantPhoto photo : restaurant.photos) {
                    insertPhoto(db, restaurant.id, photo);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    List<Restaurant> getRestaurants() {
        SQLiteDatabase db = getReadableDatabase();
        List<Restaurant> restaurants = new ArrayList<>();
        try (Cursor cursor = db.rawQuery("SELECT id, name, address, tags, rating, review_count, distance, latitude, longitude FROM restaurants", null)) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String name = cursor.getString(1);
                String address = cursor.getString(2);
                String tags = cursor.getString(3);
                double rating = cursor.getDouble(4);
                int reviewCount = cursor.getInt(5);
                double distance = cursor.getDouble(6);
                double latitude = cursor.getDouble(7);
                double longitude = cursor.getDouble(8);
                Restaurant restaurant = new Restaurant(id, name, address, parseTags(tags), rating, reviewCount, distance, latitude, longitude);
                restaurant.reviews.addAll(getReviewsFor(id));
                restaurant.photos.addAll(getPhotosFor(id));
                restaurants.add(restaurant);
            }
        }
        return restaurants;
    }

    Restaurant getRestaurant(String id) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT name, address, tags, rating, review_count, distance, latitude, longitude FROM restaurants WHERE id = ?", new String[]{id})) {
            if (cursor.moveToFirst()) {
                String name = cursor.getString(0);
                String address = cursor.getString(1);
                String tags = cursor.getString(2);
                double rating = cursor.getDouble(3);
                int reviewCount = cursor.getInt(4);
                double distance = cursor.getDouble(5);
                double latitude = cursor.getDouble(6);
                double longitude = cursor.getDouble(7);
                Restaurant restaurant = new Restaurant(id, name, address, parseTags(tags), rating, reviewCount, distance, latitude, longitude);
                restaurant.reviews.addAll(getReviewsFor(id));
                restaurant.photos.addAll(getPhotosFor(id));
                return restaurant;
            }
        }
        return null;
    }

    ReviewStats addReview(String restaurantId, Review review) {
        SQLiteDatabase db = getWritableDatabase();
        insertReview(db, restaurantId, review);
        ReviewStats stats = computeReviewStats(restaurantId);

        ContentValues values = new ContentValues();
        values.put("rating", stats.average);
        values.put("review_count", stats.count);
        db.update("restaurants", values, "id = ?", new String[]{restaurantId});
        return stats;
    }

    void addPhoto(String restaurantId, RestaurantPhoto photo) {
        SQLiteDatabase db = getWritableDatabase();
        insertPhoto(db, restaurantId, photo);
    }

    private void insertReview(SQLiteDatabase db, String restaurantId, Review review) {
        ContentValues values = new ContentValues();
        values.put("restaurant_id", restaurantId);
        values.put("author", review.author);
        values.put("rating", review.rating);
        values.put("text", review.text);
        values.put("date", review.createdAt);
        db.insert("reviews", null, values);
    }

    private void insertPhoto(SQLiteDatabase db, String restaurantId, RestaurantPhoto photo) {
        ContentValues values = new ContentValues();
        values.put("restaurant_id", restaurantId);
        values.put("caption", photo.caption);
        values.put("uri", photo.uri);
        db.insert("photos", null, values);
    }

    private List<Review> getReviewsFor(String restaurantId) {
        SQLiteDatabase db = getReadableDatabase();
        List<Review> reviews = new ArrayList<>();
        try (Cursor cursor = db.rawQuery(
                "SELECT author, rating, text, date FROM reviews WHERE restaurant_id = ? ORDER BY id DESC",
                new String[]{restaurantId})) {
            while (cursor.moveToNext()) {
                String author = cursor.getString(0);
                int rating = cursor.getInt(1);
                String text = cursor.getString(2);
                String date = cursor.getString(3);
                reviews.add(new Review(author, rating, text, date));
            }
        }
        return reviews;
    }

    private List<RestaurantPhoto> getPhotosFor(String restaurantId) {
        SQLiteDatabase db = getReadableDatabase();
        List<RestaurantPhoto> photos = new ArrayList<>();
        try (Cursor cursor = db.rawQuery(
                "SELECT caption, uri FROM photos WHERE restaurant_id = ? ORDER BY id DESC",
                new String[]{restaurantId})) {
            while (cursor.moveToNext()) {
                String caption = cursor.getString(0);
                String uri = cursor.getString(1);
                photos.add(new RestaurantPhoto(caption, uri));
            }
        }
        return photos;
    }

    private ReviewStats computeReviewStats(String restaurantId) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.rawQuery(
                "SELECT AVG(rating), COUNT(*) FROM reviews WHERE restaurant_id = ?",
                new String[]{restaurantId})) {
            if (cursor.moveToFirst()) {
                double avg = cursor.isNull(0) ? 0 : cursor.getDouble(0);
                int count = cursor.getInt(1);
                return new ReviewStats(roundToTenths(avg), count);
            }
        }
        return new ReviewStats(0, 0);
    }

    private double roundToTenths(double value) {
        return Math.round(value * 10) / 10.0;
    }

    private List<String> parseTags(String tags) {
        if (TextUtils.isEmpty(tags)) return new ArrayList<>();
        String[] parts = tags.split(",");
        List<String> list = new ArrayList<>();
        for (String part : parts) {
            list.add(part.trim());
        }
        return list;
    }

    static class ReviewStats {
        final double average;
        final int count;

        ReviewStats(double average, int count) {
            this.average = average;
            this.count = count;
        }
    }
}
