package com.example.thecat_alogue;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class CatImage {

    @SerializedName("id")
    private String id;

    @SerializedName("url")
    private String url;

    @Nullable
    public String getId() { return id; }

    @Nullable
    public String getUrl() { return url; }

    public void setUrl(@Nullable String url) { this.url = url; }
    public void setId(@Nullable String id) { this.id = id; }
}
