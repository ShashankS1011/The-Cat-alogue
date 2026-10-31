package com.example.thecat_alogue

import com.google.gson.annotations.SerializedName

data class BreedResponse(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("name")
    val name: String? = null
)
