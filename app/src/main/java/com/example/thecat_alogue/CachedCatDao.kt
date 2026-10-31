package com.example.thecat_alogue

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CachedCatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cats: List<CachedCatEntity>)

    @Query("SELECT * FROM cached_cats WHERE (:breedId IS NULL AND breedId IS NULL) OR breedId = :breedId ORDER BY cachedAt DESC LIMIT :limit")
    suspend fun getCachedCats(breedId: String?, limit: Int): List<CachedCatEntity>

    @Query("DELETE FROM cached_cats")
    suspend fun deleteAll()
}
