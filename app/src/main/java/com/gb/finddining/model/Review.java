package com.gb.finddining.model;

public class Review {
    public final String author;
    public final int rating;
    public final String text;
    public final String createdAt;

    public Review(String author, int rating, String text, String createdAt) {
        this.author = author;
        this.rating = rating;
        this.text = text;
        this.createdAt = createdAt;
    }
}
