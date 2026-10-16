package com.example.thecat_alogue;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class FavouriteRequest {

    @SerializedName("image_id")
    private final String imageId;

    @SerializedName("sub_id")
    private final String subId;

    public FavouriteRequest(@Nullable String imageId, @Nullable String subId) {
        this.imageId = imageId;
        this.subId = subId;
    }

    @Nullable
    public String getImageId() {
        return imageId;
    }

    @Nullable
    public String getSubId() {
        return subId;
    }
}
