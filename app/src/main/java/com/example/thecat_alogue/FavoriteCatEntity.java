package com.example.thecat_alogue;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorite_cats")
public class FavoriteCatEntity {

    @PrimaryKey
    @NonNull
    private final String id;
    private final String url;
    private final long addedAt;

    public FavoriteCatEntity(@NonNull String id, String url, long addedAt) {
        this.id = id;
        this.url = url;
        this.addedAt = addedAt;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public long getAddedAt() {
        return addedAt;
    }
}
