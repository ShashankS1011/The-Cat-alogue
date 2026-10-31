package com.example.thecat_alogue

import com.google.gson.annotations.SerializedName

data class Breed(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("temperament")
    val temperament: String? = null,

    @SerializedName("origin")
    val origin: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("life_span")
    val lifeSpan: String? = null,

    @SerializedName("wikipedia_url")
    val wikipediaUrl: String? = null
) {
    override fun toString(): String {
        return name
    }
}
