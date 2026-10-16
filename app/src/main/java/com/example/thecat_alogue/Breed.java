package com.example.thecat_alogue;

import com.google.gson.annotations.SerializedName;

public class Breed {
    private String id;
    private String name;
    private String description;
    private String temperament;
    private String origin;

    @SerializedName("life_span")
    private String lifeSpan;

    @SerializedName("wikipedia_url")
    private String wikipediaUrl;

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getTemperament() { return temperament; }
    public String getOrigin() { return origin; }
    public String getLifeSpan() { return lifeSpan; }
    public String getWikipediaUrl() { return wikipediaUrl; }

    @Override
    public String toString() {
        return name;
    }
}
