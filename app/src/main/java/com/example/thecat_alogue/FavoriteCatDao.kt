package com.example.thecat_alogue

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteCatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cat: FavoriteCatEntity)

    @Query("DELETE FROM favorite_cats WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM favorite_cats")
    suspend fun deleteAll()

    @Query("SELECT * FROM favorite_cats ORDER BY addedAt DESC")
    suspend fun getAllFavoritesSync(): List<FavoriteCatEntity>

    @Query("SELECT * FROM favorite_cats ORDER BY addedAt DESC")
    fun getAllFavoritesFlow(): Flow<List<FavoriteCatEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_cats WHERE id = :id)")
    suspend fun isFavorite(id: String): Boolean
}
