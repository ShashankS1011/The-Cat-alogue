package com.example.thecat_alogue

import com.google.gson.annotations.SerializedName

data class FavouriteRequest(
    @SerializedName("image_id")
    val imageId: String?,

    @SerializedName("sub_id")
    val subId: String?
)
