package com.example.thecat_alogue

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_cats")
data class CachedCatEntity(
    @PrimaryKey
    val id: String,
    val url: String,
    val breedId: String? = null,
    val width: Int? = null,
    val height: Int? = null,
    val cachedAt: Long = System.currentTimeMillis()
) {
    fun toDomainModel(): CatImage {
        return CatImage(
            id = id,
            url = url,
            width = width,
            height = height
        )
    }

    companion object {
        fun fromDomainModel(cat: CatImage, breedId: String? = null): CachedCatEntity? {
            val catId = cat.id ?: return null
            val catUrl = cat.url ?: return null
            return CachedCatEntity(
                id = catId,
                url = catUrl,
                breedId = breedId,
                width = cat.width,
                height = cat.height
            )
        }
    }
}
