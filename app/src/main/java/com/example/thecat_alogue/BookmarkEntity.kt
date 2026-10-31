package com.example.thecat_alogue

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarked_cats")
data class BookmarkEntity(
    @PrimaryKey
    val id: String,
    val url: String,
    val collectionName: String = "My Collection",
    val savedAt: Long = System.currentTimeMillis()
)
