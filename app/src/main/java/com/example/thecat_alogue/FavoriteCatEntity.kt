package com.example.thecat_alogue

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_cats")
data class FavoriteCatEntity(
    @PrimaryKey
    val id: String,
    val url: String,
    val addedAt: Long = System.currentTimeMillis()
)
