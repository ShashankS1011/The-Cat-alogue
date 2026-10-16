package com.example.thecat_alogue;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class FavouriteResponse {

    @SerializedName("id")
    private String id;

    @SerializedName("image_id")
    private String imageId;

    @SerializedName("image")
    private CatImage image;

    @Nullable
    public String getId() { return id; }

    @Nullable
    public CatImage getImage() { return image; }
}
