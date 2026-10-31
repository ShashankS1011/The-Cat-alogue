package com.example.thecat_alogue

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BreedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(breeds: List<BreedEntity>)

    @Query("SELECT * FROM cached_breeds ORDER BY name ASC")
    suspend fun getAllBreedsSync(): List<BreedEntity>

    @Query("SELECT * FROM cached_breeds ORDER BY name ASC")
    fun getAllBreedsFlow(): Flow<List<BreedEntity>>

    @Query("DELETE FROM cached_breeds")
    suspend fun deleteAll()
}
