package com.example.thecat_alogue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class Breed {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("temperament")
    private String temperament;

    @SerializedName("origin")
    private String origin;

    @SerializedName("life_span")
    private String lifeSpan;

    @SerializedName("wikipedia_url")
    private String wikipediaUrl;

    @Nullable
    public String getId() { return id; }

    @Nullable
    public String getName() { return name; }

    @Nullable
    public String getDescription() { return description; }

    @Nullable
    public String getTemperament() { return temperament; }

    @Nullable
    public String getOrigin() { return origin; }

    @Nullable
    public String getLifeSpan() { return lifeSpan; }

    @Nullable
    public String getWikipediaUrl() { return wikipediaUrl; }

    @NonNull
    @Override
    public String toString() {
        return name != null ? name : "";
    }
}
