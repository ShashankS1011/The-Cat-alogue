package com.example.thecat_alogue

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_breeds")
data class BreedEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val temperament: String? = null,
    val origin: String? = null,
    val description: String? = null,
    val lifeSpan: String? = null,
    val wikipediaUrl: String? = null
) {
    fun toDomainModel(): Breed {
        return Breed(
            id = id,
            name = name,
            temperament = temperament,
            origin = origin,
            description = description,
            lifeSpan = lifeSpan,
            wikipediaUrl = wikipediaUrl
        )
    }

    companion object {
        fun fromDomainModel(breed: Breed): BreedEntity {
            return BreedEntity(
                id = breed.id,
                name = breed.name,
                temperament = breed.temperament,
                origin = breed.origin,
                description = breed.description,
                lifeSpan = breed.lifeSpan,
                wikipediaUrl = breed.wikipediaUrl
            )
        }
    }
}
