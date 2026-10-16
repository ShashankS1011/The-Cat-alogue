package com.example.thecat_alogue;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavoriteCatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FavoriteCatEntity cat);

    @Query("DELETE FROM favorite_cats WHERE id = :id")
    void deleteById(String id);

    @Query("SELECT * FROM favorite_cats ORDER BY addedAt DESC")
    List<FavoriteCatEntity> getAllFavoritesSync();
}
