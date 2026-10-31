package com.example.thecat_alogue

import com.google.gson.annotations.SerializedName

data class FavouriteResponse(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("image_id")
    val imageId: String? = null,

    @SerializedName("image")
    val image: CatImage? = null
)
