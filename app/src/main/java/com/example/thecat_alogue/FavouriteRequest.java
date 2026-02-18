package com.example.thecat_alogue;

public class FavouriteRequest {
    private final String image_id;
    private final String sub_id; // This is your user ID (e.g., "user_123")

    public FavouriteRequest(String image_id, String sub_id) {
        this.image_id = image_id;
        this.sub_id = sub_id;
    }

    public String getImageId() {
        return image_id;
    }

    public String getSubId() {
        return sub_id;
    }
}