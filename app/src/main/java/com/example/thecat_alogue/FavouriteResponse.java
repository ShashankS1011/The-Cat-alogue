package com.example.thecat_alogue;

public class FavouriteResponse {
    private String id;
    private String image_id;
    private CatImage image; // The API nests the actual image info here

    public String getId() { return id; }
    public CatImage getImage() { return image; }
}