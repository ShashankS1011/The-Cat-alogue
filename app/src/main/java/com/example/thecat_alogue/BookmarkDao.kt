package com.example.thecat_alogue

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BookmarkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmark: BookmarkEntity)

    @Query("SELECT * FROM bookmarked_cats ORDER BY savedAt DESC")
    suspend fun getAllBookmarks(): List<BookmarkEntity>

    @Query("DELETE FROM bookmarked_cats WHERE id = :id")
    suspend fun deleteById(id: String)
}
