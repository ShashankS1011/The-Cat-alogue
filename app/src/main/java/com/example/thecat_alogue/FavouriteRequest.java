package com.example.thecat_alogue;

import com.google.gson.annotations.SerializedName;

public class FavouriteRequest {

    @SerializedName("image_id") // Ensures JSON key is always "image_id"
    private final String imageId;

    @SerializedName("sub_id")
    private final String subId;

    public FavouriteRequest(String imageId, String subId) {
        this.imageId = imageId;
        this.subId = subId;
    }
}