package com.gb.finddining.model;

import java.util.ArrayList;
import java.util.List;

public class Restaurant {
    public final String id;
    public final String name;
    public final String address;
    public final List<String> tags;
    public double rating;
    public int reviewCount;
    public final double distanceKm;
    public final List<Review> reviews;
    public final List<RestaurantPhoto> photos;

    public Restaurant(String id, String name, String address, List<String> tags, double rating, int reviewCount, double distanceKm) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.tags = tags;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.distanceKm = distanceKm;
        this.reviews = new ArrayList<>();
        this.photos = new ArrayList<>();
    }
}
